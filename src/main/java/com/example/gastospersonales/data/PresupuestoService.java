package com.example.gastospersonales.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PresupuestoService {
    private final List<Presupuesto> presupuestos = new ArrayList<>();

    public PresupuestoService() {
        // Inicializa la lista vacía 
    }

    public List<Presupuesto> findAll() {
        return new ArrayList<>(presupuestos);
    }

    public void save(Presupuesto presupuesto) {
        if (presupuesto.getId() == null || presupuesto.getId().isEmpty()) {
            presupuesto.setId(UUID.randomUUID().toString());
            if (presupuesto.getMontoGastado() == null) {
                presupuesto.setMontoGastado(BigDecimal.ZERO);
            }
            presupuestos.add(presupuesto);
        } else {
            presupuestos.removeIf(p -> p.getId().equals(presupuesto.getId()));
            presupuestos.add(presupuesto);
        }
    }

    public void delete(Presupuesto presupuesto) {
        presupuestos.removeIf(p -> p.getId().equals(presupuesto.getId()));
    }
}