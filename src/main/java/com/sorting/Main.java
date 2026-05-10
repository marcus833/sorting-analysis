package com.sorting;

import com.sorting.algorithms.*;
import com.sorting.parallel.*;
import com.sorting.framework.TestRunner;
import com.sorting.results.CsvWriter;

public class Main {

    static final int[] SIZES     = {1000, 5000, 10000, 20000};
    static final int[] THREADS   = { 2, 4, 8};
    static final int   SAMPLES   = 5;
    static final String CSV_PATH = "results/resultados.csv";

    public static void main(String[] args) throws Exception {

        CsvWriter csv = new CsvWriter(CSV_PATH);
        String[] dataTypes = {"random", "sorted", "reverse"};

        for (String dataType : dataTypes) {
            System.out.println("\n=== Tipo de dados: " + dataType + " ===");

            for (int size : SIZES) {
                System.out.println("  Tamanho: " + size);

                int[] base;
                if (dataType.equals("sorted"))       base = TestRunner.generateSorted(size);
                else if (dataType.equals("reverse")) base = TestRunner.generateReverse(size);
                else                                 base = TestRunner.generateRandom(size);

                // SERIAL
                runSerial("BubbleSort",    csv, base, dataType, size);
                runSerial("QuickSort",     csv, base, dataType, size);
                runSerial("MergeSort",     csv, base, dataType, size);
                runSerial("SelectionSort", csv, base, dataType, size);

                // PARALELO
                for (int t : THREADS) {
                    runParallel("ParallelBubbleSort",    csv, base, dataType, size, t);
                    runParallel("ParallelQuickSort",     csv, base, dataType, size, t);
                    runParallel("ParallelMergeSort",     csv, base, dataType, size, t);
                    runParallel("ParallelSelectionSort", csv, base, dataType, size, t);
                }
            }
        }

        csv.write();
        System.out.println("\nTodos os testes concluidos!");
    }

    static void runSerial(String name, CsvWriter csv,
                          int[] base, String dataType, int size) {
        System.out.print("    [SERIAL] " + name + " ... ");
        for (int s = 1; s <= SAMPLES; s++) {
            int[] arr = TestRunner.copy(base);
            long ns = TestRunner.measureSerial(() -> {
                if (name.equals("BubbleSort"))         BubbleSort.sort(arr);
                else if (name.equals("QuickSort"))     QuickSort.sort(arr, 0, arr.length - 1);
                else if (name.equals("MergeSort"))     MergeSort.sort(arr, 0, arr.length - 1);
                else if (name.equals("SelectionSort")) SelectionSort.sort(arr);
            });
            csv.addRow(name, dataType, size, 1, s, ns / 1_000_000.0);
        }
        System.out.println("OK");
    }

    static void runParallel(String name, CsvWriter csv,
                            int[] base, String dataType, int size, int threads) {
        System.out.print("    [PARALLEL t=" + threads + "] " + name + " ... ");
        for (int s = 1; s <= SAMPLES; s++) {
            int[] arr = TestRunner.copy(base);
            long ns = TestRunner.measureSerial(() -> {
                try {
                    if (name.equals("ParallelBubbleSort"))
                        ParallelBubbleSort.sort(arr, threads);
                    else if (name.equals("ParallelQuickSort"))
                        ParallelQuickSort.sort(arr, threads);
                    else if (name.equals("ParallelMergeSort"))
                        ParallelMergeSort.sort(arr, threads);
                    else if (name.equals("ParallelSelectionSort"))
                        ParallelSelectionSort.sort(arr, threads);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            csv.addRow(name, dataType, size, threads, s, ns / 1_000_000.0);
        }
        System.out.println("OK");
    }
}