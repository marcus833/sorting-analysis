package com.sorting.results;

import java.io.*;
import java.util.*;

public class CsvWriter {

    private final String filePath;
    private final List<String[]> rows = new ArrayList<>();

    public CsvWriter(String filePath) {
        this.filePath = filePath;
        rows.add(new String[]{
            "Algoritmo", "Tipo", "Tamanho", "Threads", "Amostra", "Tempo_ms"
        });
    }

    public void addRow(String algorithm, String type, int size,
                       int threads, int sample, double timeMs) {
        rows.add(new String[]{
            algorithm, type, String.valueOf(size),
            String.valueOf(threads), String.valueOf(sample),
            String.format("%.4f", timeMs)
        });
    }

    public void write() throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }
        }
        System.out.println("CSV salvo em: " + file.getAbsolutePath());
    }
}