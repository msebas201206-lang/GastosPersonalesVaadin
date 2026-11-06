package com.example.gastospersonales.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
        // Botón para abrir y cerrar el menú lateral
        DrawerToggle toggle = new DrawerToggle();
        
        // Título principal en la parte superior
        H1 title = new H1("Gastos Personales");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        // Menú lateral con las opciones disponibles
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Movimientos", MovimientosView.class));
        nav.addItem(new SideNavItem("Presupuestos", PresupuestosView.class)); //Añadido
        nav.addItem(new SideNavItem("Reportes", ReportesView.class));         //Añadido

        // Barra de desplazamiento para el menú
        Scroller scroller = new Scroller(nav);
        scroller.addClassName(LumoUtility.Padding.SMALL);

        // Ubica los elementos en la interfaz
        addToDrawer(scroller);
        addToNavbar(true, toggle, title);
    }
}