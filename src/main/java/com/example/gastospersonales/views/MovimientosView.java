package com.example.gastospersonales.views;

import com.example.gastospersonales.data.ArchivoService;
import com.example.gastospersonales.data.Movimiento;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@PageTitle("Movimientos | Gastos Personales")
@Route(value = "", layout = MainLayout.class) // Página principal de la app
public class MovimientosView extends VerticalLayout {

    private Grid<Movimiento> grid = new Grid<>(Movimiento.class); // La tabla que muestra los registros
    private List<Movimiento> movimientosList;

    // Las casillas para escribir o seleccionar los datos
    private ComboBox<String> tipoCombo = new ComboBox<>("Tipo");
    private NumberField valorField = new NumberField("Valor ($)");
    private DatePicker fechaPicker = new DatePicker("Fecha");
    private ComboBox<String> categoriaCombo = new ComboBox<>("Categoría");
    private TextField descripcionField = new TextField("Descripción");

    // Los botones de acción
    private Button guardarNuevoBtn = new Button("Guardar Nuevo");
    private Button activarEdicionBtn = new Button("Seleccionar para Actualizar");
    private Button guardarCambiosBtn = new Button("Guardar Cambios");
    private Button cancelarBtn = new Button("Cancelar");

    private Movimiento movimientoSeleccionado = null; // Guarda temporalmente el registro que vas a editar
    private boolean modoEdicion = false;              // Dice si estamos editando (true) o creando normal (false)

    public MovimientosView() {
        addClassName("movimientos-view");
        setSizeFull();

        // Opciones que aparecen en las listas desplegables
        tipoCombo.setItems("Ingreso", "Gasto");
        categoriaCombo.setItems("Comida", "Transporte", "Servicios", "Entretenimiento", "Otros");
        fechaPicker.setValue(LocalDate.now());

        // Qué pasa cuando se presiona cada boton 
        guardarNuevoBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarNuevoBtn.addClickListener(e -> guardarNuevoMovimiento());

        activarEdicionBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        activarEdicionBtn.addClickListener(e -> activarModoEdicion());

        guardarCambiosBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        guardarCambiosBtn.setVisible(false); // Arranca oculto
        guardarCambiosBtn.addClickListener(e -> guardarCambiosActualizacion());

        cancelarBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelarBtn.setVisible(false); // Arranca oculto
        cancelarBtn.addClickListener(e -> limpiarFormulario());

        // Acomoda los botones y cajitas en la parte de arriba
        HorizontalLayout formLayout = new HorizontalLayout(
            tipoCombo, valorField, fechaPicker, categoriaCombo, descripcionField, 
            guardarNuevoBtn, activarEdicionBtn, guardarCambiosBtn, cancelarBtn
        );
        formLayout.setVerticalComponentAlignment(Alignment.END, guardarNuevoBtn, activarEdicionBtn, guardarCambiosBtn, cancelarBtn);

        configurarGrid();

        // Muestra todo en la pantalla
        add(new H2("Gestión de Movimientos"), formLayout, grid);
        actualizarGrid();
    }

    // Configura las columnas de la tabla y lo que pasa al hacer clic en un registro
    private void configurarGrid() {
        grid.setColumns("tipo", "valor", "fecha", "categoria", "descripcion");
        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            // Si intenta hacer clic en la tabla sin haber presionado el botón de actualizar, no lo deja
            if (!modoEdicion) {
                if (event.getValue() != null) {
                    grid.asSingleSelect().clear();
                    Notification.show(" Presiona primero 'Seleccionar para Actualizar' si deseas editar un registro.", 3000, Notification.Position.MIDDLE);
                }
                return;
            }

            // Si ya activó la edición, carga los datos en las cajitas de arriba
            movimientoSeleccionado = event.getValue();
            if (movimientoSeleccionado != null) {
                tipoCombo.setValue(movimientoSeleccionado.getTipo());
                valorField.setValue(movimientoSeleccionado.getValor());
                fechaPicker.setValue(movimientoSeleccionado.getFecha());
                categoriaCombo.setValue(movimientoSeleccionado.getCategoria());
                descripcionField.setValue(movimientoSeleccionado.getDescripcion());
                
                Notification.show(" Registro cargado. Modifica los campos y presiona 'Guardar Cambios'.", 3000, Notification.Position.MIDDLE);
            }
        });
    }

    // Prepara la pantalla para poder elegir un registro viejo de la tabla
    private void activarModoEdicion() {
        modoEdicion = true;
        guardarNuevoBtn.setVisible(false);
        activarEdicionBtn.setVisible(false);
        guardarCambiosBtn.setVisible(true);
        cancelarBtn.setVisible(true);
        Notification.show(" Haz clic en el registro de la tabla que deseas modificar.", 4000, Notification.Position.MIDDLE);
    }

    // Actualiza la tabla trayendo los datos del archivo de texto
    private void actualizarGrid() {
        movimientosList = ArchivoService.leerMovimientos();
        grid.setItems(movimientosList);
    }

    // Crea un registro totalmente nuevo y lo guarda
    private void guardarNuevoMovimiento() {
        if (!validarCampos()) return;

        String id = UUID.randomUUID().toString().substring(0, 8); // Crea un código corto al azar
        Movimiento nuevo = new Movimiento(
            id,
            tipoCombo.getValue(),
            valorField.getValue(),
            fechaPicker.getValue(),
            categoriaCombo.getValue(),
            descripcionField.getValue() != null ? descripcionField.getValue() : ""
        );

        movimientosList.add(nuevo);
        ArchivoService.guardarMovimientos(movimientosList);

        Notification.show(" Movimiento guardado correctamente.");
        actualizarGrid();
        limpiarFormulario();
    }

    // Guarda las modificaciones que se le hace a un registro viejo
    private void guardarCambiosActualizacion() {
        if (movimientoSeleccionado == null) {
            Notification.show("⚠️ Por favor selecciona un registro de la tabla primero.", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (!validarCampos()) return;

        movimientoSeleccionado.setTipo(tipoCombo.getValue());
        movimientoSeleccionado.setValor(valorField.getValue());
        movimientoSeleccionado.setFecha(fechaPicker.getValue());
        movimientoSeleccionado.setCategoria(categoriaCombo.getValue());
        movimientoSeleccionado.setDescripcion(descripcionField.getValue() != null ? descripcionField.getValue() : "");

        ArchivoService.guardarMovimientos(movimientosList);
        Notification.show("Movimiento actualizado correctamente.");
        
        actualizarGrid();
        limpiarFormulario();
    }

    // Revisa que no queden casillas obligatorias vacías
    private boolean validarCampos() {
        if (tipoCombo.isEmpty() || valorField.isEmpty() || valorField.getValue() <= 0 || fechaPicker.isEmpty() || categoriaCombo.isEmpty()) {
            Notification.show("⚠️ Por favor completa los campos obligatorios.", 3000, Notification.Position.MIDDLE);
            return false;
        }
        return true;
    }

    // Limpia todo y deja los botones como al principio
    private void limpiarFormulario() {
        tipoCombo.clear();
        valorField.clear();
        categoriaCombo.clear();
        descripcionField.clear();
        fechaPicker.setValue(LocalDate.now());
        grid.asSingleSelect().clear();
        movimientoSeleccionado = null;
        modoEdicion = false;

        guardarNuevoBtn.setVisible(true);
        activarEdicionBtn.setVisible(true);
        guardarCambiosBtn.setVisible(false);
        cancelarBtn.setVisible(false);
    }
}