package com.example.gastospersonales.views;

import com.example.gastospersonales.data.Presupuesto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@PageTitle("Presupuestos | Gastos Personales")
@Route(value = "presupuestos", layout = MainLayout.class)
public class PresupuestosView extends VerticalLayout {

    private Grid<Presupuesto> grid = new Grid<>(Presupuesto.class, false);
    private List<Presupuesto> presupuestosList;
    private HorizontalLayout cardsLayout = new HorizontalLayout();

    private ComboBox<String> categoriaCombo = new ComboBox<>("Categoría");
    private BigDecimalField montoLimiteField = new BigDecimalField("Presupuesto Mensual ($)");

    private Button guardarNuevoBtn = new Button("Guardar Nuevo");
    private Button activarEdicionBtn = new Button("Seleccionar para Actualizar");
    private Button guardarCambiosBtn = new Button("Guardar Cambios");
    private Button cancelarBtn = new Button("Cancelar");

    private Presupuesto presupuestoSeleccionado = null;
    private boolean modoEdicion = false;

    public PresupuestosView() {
        addClassName("presupuestos-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        categoriaCombo.setItems("Comida", "Transporte", "Servicios", "Entretenimiento", "Salud", "Educación", "Otros");
        montoLimiteField.setPrefixComponent(new Span("$"));

        guardarNuevoBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarNuevoBtn.addClickListener(e -> guardarNuevoPresupuesto());

        activarEdicionBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        activarEdicionBtn.addClickListener(e -> activarModoEdicion());

        guardarCambiosBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        guardarCambiosBtn.setVisible(false);
        guardarCambiosBtn.addClickListener(e -> guardarCambiosActualizacion());

        cancelarBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelarBtn.setVisible(false);
        cancelarBtn.addClickListener(e -> limpiarFormulario());

        HorizontalLayout formLayout = new HorizontalLayout(
            categoriaCombo, montoLimiteField, 
            guardarNuevoBtn, activarEdicionBtn, guardarCambiosBtn, cancelarBtn
        );
        formLayout.setVerticalComponentAlignment(Alignment.END, guardarNuevoBtn, activarEdicionBtn, guardarCambiosBtn, cancelarBtn);

        configurarGrid();

        cardsLayout.setWidthFull();

        add(new H2("Gestión de Presupuestos"), cardsLayout, formLayout, grid);
        actualizarGrid();
    }

    private void configurarGrid() {
        grid.addColumn(p -> p.getCategoria()).setHeader("Categoría").setAutoWidth(true);
        grid.addColumn(p -> formatearMoneda(p.getMontoLimite())).setHeader("Límite").setAutoWidth(true);
        grid.addColumn(p -> formatearMoneda(p.getMontoGastado())).setHeader("Gastado").setAutoWidth(true);
        grid.addColumn(p -> formatearMoneda(p.getRestante())).setHeader("Disponible").setAutoWidth(true);

        grid.addComponentColumn(p -> {
            ProgressBar pb = new ProgressBar();
            pb.setValue(Math.min(p.getPorcentajeConsumido(), 1.0));
            return pb;
        }).setHeader("Estado del Gasto");

        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (!modoEdicion) {
                if (event.getValue() != null) {
                    grid.asSingleSelect().clear();
                    Notification.show(" Presiona primero 'Seleccionar para Actualizar' si deseas editar un presupuesto.", 3000, Notification.Position.MIDDLE);
                }
                return;
            }

            presupuestoSeleccionado = event.getValue();
            if (presupuestoSeleccionado != null) {
                categoriaCombo.setValue(presupuestoSeleccionado.getCategoria());
                montoLimiteField.setValue(presupuestoSeleccionado.getMontoLimite());
                Notification.show(" Presupuesto cargado. Modifica y presiona 'Guardar Cambios'.", 3000, Notification.Position.MIDDLE);
            }
        });
    }

    private void activarModoEdicion() {
        modoEdicion = true;
        guardarNuevoBtn.setVisible(false);
        activarEdicionBtn.setVisible(false);
        guardarCambiosBtn.setVisible(true);
        cancelarBtn.setVisible(true);
        Notification.show(" Haz clic en el presupuesto de la tabla que deseas modificar.", 4000, Notification.Position.MIDDLE);
    }

    private void actualizarGrid() {
        presupuestosList = leerPresupuestosSimulados();
        grid.setItems(presupuestosList);
        renderizarTarjetas(presupuestosList);
    }

    private void guardarNuevoPresupuesto() {
        if (!validarCampos()) return;

        String id = UUID.randomUUID().toString().substring(0, 8);
        Presupuesto nuevo = new Presupuesto(
            id,
            categoriaCombo.getValue(),
            montoLimiteField.getValue(),
            BigDecimal.ZERO
        );

        presupuestosList.add(nuevo);
        guardarPresupuestosSimulados(presupuestosList);

        Notification.show(" Presupuesto guardado correctamente.");
        actualizarGrid();
        limpiarFormulario();
    }

    private void guardarCambiosActualizacion() {
        if (presupuestoSeleccionado == null) {
            Notification.show("⚠️ Por favor selecciona un presupuesto de la tabla primero.", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (!validarCampos()) return;

        presupuestoSeleccionado.setCategoria(categoriaCombo.getValue());
        presupuestoSeleccionado.setMontoLimite(montoLimiteField.getValue());

        guardarPresupuestosSimulados(presupuestosList);
        Notification.show(" Presupuesto actualizado correctamente.");
        
        actualizarGrid();
        limpiarFormulario();
    }

    private boolean validarCampos() {
        if (categoriaCombo.isEmpty() || montoLimiteField.isEmpty() || montoLimiteField.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            Notification.show("⚠️ Por favor completa los campos correctamente (monto mayor a 0).", 3000, Notification.Position.MIDDLE);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        categoriaCombo.clear();
        montoLimiteField.clear();
        grid.asSingleSelect().clear();
        presupuestoSeleccionado = null;
        modoEdicion = false;

        guardarNuevoBtn.setVisible(true);
        activarEdicionBtn.setVisible(true);
        guardarCambiosBtn.setVisible(false);
        cancelarBtn.setVisible(false);
    }

    private void renderizarTarjetas(List<Presupuesto> lista) {
        cardsLayout.removeAll();
        for (Presupuesto p : lista) {
            VerticalLayout card = new VerticalLayout();
            card.setWidth("240px");
            card.setPadding(true);
            card.setSpacing(false);
            card.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                           .set("border-radius", "8px")
                           .set("box-shadow", "0 2px 4px rgba(0,0,0,0.05)");

            Span cat = new Span(p.getCategoria());
            cat.getStyle().set("font-weight", "bold").set("font-size", "1.1em");

            Span detalle = new Span(formatearMoneda(p.getMontoGastado()) + " / " + formatearMoneda(p.getMontoLimite()));
            detalle.getStyle().set("font-size", "0.85em").set("color", "var(--lumo-secondary-text-color)");

            ProgressBar pb = new ProgressBar();
            double ratio = p.getPorcentajeConsumido();
            pb.setValue(Math.min(ratio, 1.0));

            if (ratio >= 1.0) {
                card.getStyle().set("border-left", "5px solid var(--lumo-error-color)");
            } else if (ratio >= 0.8) {
                card.getStyle().set("border-left", "5px solid var(--lumo-warning-color)");
            } else {
                card.getStyle().set("border-left", "5px solid var(--lumo-success-color)");
            }

            card.add(cat, detalle, pb);
            cardsLayout.add(card);
        }
    }

    private String formatearMoneda(BigDecimal valor) {
        if (valor == null) return "$0";
        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
        return formato.format(valor);
    }

    private static List<Presupuesto> listaTemporal = new ArrayList<>();
    
    private List<Presupuesto> leerPresupuestosSimulados() {
        return listaTemporal;
    }

    private void guardarPresupuestosSimulados(List<Presupuesto> lista) {
        listaTemporal = lista;
    }
}