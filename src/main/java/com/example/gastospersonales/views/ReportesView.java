package com.example.gastospersonales.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Reportes | Gastos Personales")
@Route(value = "reportes", layout = MainLayout.class)
public class ReportesView extends VerticalLayout {
    public ReportesView() {
        addClassName("reportes-view");
        setSizeFull();
        add(new H2("Reportes y Estadísticas"), new Paragraph("Resumen gráfico de tus finanzas."));
    }
}
