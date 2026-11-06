package com.example.gastospersonales.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Presupuestos | Gastos Personales")
@Route(value = "presupuestos", layout = MainLayout.class)
public class PresupuestosView extends VerticalLayout {
    public PresupuestosView() {
        addClassName("presupuestos-view");
        setSizeFull();
        add(new H2("Gestión de Presupuestos"), new Paragraph("Controla tus límites de gastos."));
    }
}
