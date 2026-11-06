package com.example.gastospersonales.data;

import java.time.LocalDate;

public class Movimiento {
    // Aquí definimos qué datos va a tener cada gasto o ingreso
    private String id;          // Un código único para identificar el registro
    private String tipo;        // Si fue "Ingreso" o "Gasto"
    private Double valor;       // La cantidad de dinero
    private LocalDate fecha;    // El dia en que pasó
    private String categoria;   // En qué se gastó (Comida, Transporte, etc.)
    private String descripcion; // Un detalle opcional 

    // Constructor vacío obligatorio
    public Movimiento() {}

    // Constructor para armar un movimiento nuevo con todos sus datos listos
    public Movimiento(String id, String tipo, Double valor, LocalDate fecha, String categoria, String descripcion) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.fecha = fecha;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    // Métodos para sacar y poner la información de cada cajita
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}