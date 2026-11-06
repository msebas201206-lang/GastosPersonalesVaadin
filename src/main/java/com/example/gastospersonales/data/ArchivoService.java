package com.example.gastospersonales.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ArchivoService {
    // Nombre del archivo de texto donde se guarda todo
    private static final String FILE_NAME = "movimientos.txt";

    // Lee el archivo de texto línea por línea y los convierte en una lista de movimientos
    public static List<Movimiento> leerMovimientos() {
        List<Movimiento> movimientos = new ArrayList<>();
        File archivo = new File(FILE_NAME);
        
        if (!archivo.exists()) {
            return movimientos; // Si el archivo no existe todavía, devuelve la lista vacía
        }

        try (Scanner scanner = new Scanner(archivo)) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split("\\|"); // Corta la línea cada vez que encuentra una barra "|"
                if (partes.length >= 6) {
                    Movimiento m = new Movimiento(
                        partes[0],                  // ID
                        partes[1],                  // Tipo
                        Double.parseDouble(partes[2]), // Valor
                        LocalDate.parse(partes[3]), // Fecha
                        partes[4],                  // Categoría
                        partes[5]                   // Descripción
                    );
                    movimientos.add(m);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movimientos;
    }

    // Toma toda la lista y la escribe completa de nuevo en el archivo de texto
    public static void guardarMovimientos(List<Movimiento> movimientos) {
        try (FileWriter writer = new FileWriter(FILE_NAME, false)) {
            for (Movimiento m : movimientos) {
                // Junta los datos separados por una barra vertical
                String linea = String.format("%s|%s|%.2f|%s|%s|%s%n",
                    m.getId(),
                    m.getTipo(),
                    m.getValor(),
                    m.getFecha(),
                    m.getCategoria(),
                    m.getDescripcion()
                );
                writer.write(linea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
