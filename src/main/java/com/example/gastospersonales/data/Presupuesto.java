package com.example.gastospersonales.data;

import java.math.BigDecimal;

public class Presupuesto {
    private String id;
    private String categoria;
    private BigDecimal montoLimite;
    private BigDecimal montoGastado;

    public Presupuesto() {
        this.montoGastado = BigDecimal.ZERO;
    }

    public Presupuesto(String id, String categoria, BigDecimal montoLimite, BigDecimal montoGastado) {
        this.id = id;
        this.categoria = categoria;
        this.montoLimite = montoLimite;
        this.montoGastado = montoGastado != null ? montoGastado : BigDecimal.ZERO;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getMontoLimite() { return montoLimite; }
    public void setMontoLimite(BigDecimal montoLimite) { this.montoLimite = montoLimite; }

    public BigDecimal getMontoGastado() { return montoGastado; }
    public void setMontoGastado(BigDecimal montoGastado) { this.montoGastado = montoGastado; }

    public BigDecimal getRestante() {
        if (montoLimite == null || montoGastado == null) return BigDecimal.ZERO;
        return montoLimite.subtract(montoGastado);
    }

    public double getPorcentajeConsumido() {
        if (montoLimite == null || montoLimite.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return montoGastado.doubleValue() / montoLimite.doubleValue();
    }
}