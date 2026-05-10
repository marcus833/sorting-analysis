package com.sorting.parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelQuickSort {

    private static class QuickSortTask extends RecursiveAction {
        private final int[] arr;
        private final int low, high;
        private static final int THRESHOLD = 2000;

        QuickSortTask(int[] arr, int low, int high) {
            this.arr = arr;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (low >= high) return;

            if (high - low < THRESHOLD) {
                insertionSort(arr, low, high);
                return;
            }

            // Pivot mediana de 3 para evitar pior caso em arrays ordenados
            int mid = low + (high - low) / 2;
            if (arr[mid] < arr[low])  swap(arr, low, mid);
            if (arr[high] < arr[low]) swap(arr, low, high);
            if (arr[mid] < arr[high]) swap(arr, mid, high);
            int pivot = arr[high];

            int i = low - 1;
            for (int j = low; j < high; j++) {
                if (arr[j] <= pivot) {
                    i++;
                    swap(arr, i, j);
                }
            }
            swap(arr, i + 1, high);
            int pi = i + 1;

            invokeAll(
                new QuickSortTask(arr, low, pi - 1),
                new QuickSortTask(arr, pi + 1, high)
            );
        }

        private void insertionSort(int[] arr, int left, int right) {
            for (int i = left + 1; i <= right; i++) {
                int key = arr[i];
                int j = i - 1;
                while (j >= left && arr[j] > key) {
                    arr[j + 1] = arr[j];
                    j--;
                }
                arr[j + 1] = key;
            }
        }

        private void swap(int[] arr, int a, int b) {
            int tmp = arr[a]; arr[a] = arr[b]; arr[b] = tmp;
        }
    }

    public static void sort(int[] arr, int numThreads) {
        if (arr.length <= 1) return;
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        pool.invoke(new QuickSortTask(arr, 0, arr.length - 1));
        pool.shutdown();
    }
}