package com.sorting.algorithms;

import java.util.Stack;

public class QuickSort {
    public static void sort(int[] arr, int low, int high) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{low, high});

        while (!stack.isEmpty()) {
            int[] range = stack.pop();
            int l = range[0], h = range[1];
            if (l >= h) continue;

            // Mediana de 3 como pivot
            int mid = l + (h - l) / 2;
            if (arr[mid] < arr[l])  { int t = arr[l]; arr[l] = arr[mid]; arr[mid] = t; }
            if (arr[h]   < arr[l])  { int t = arr[l]; arr[l] = arr[h];   arr[h]   = t; }
            if (arr[mid] < arr[h])  { int t = arr[mid]; arr[mid] = arr[h]; arr[h] = t; }
            int pivot = arr[h];

            int i = l - 1;
            for (int j = l; j < h; j++) {
                if (arr[j] <= pivot) {
                    i++;
                    int t = arr[i]; arr[i] = arr[j]; arr[j] = t;
                }
            }
            int t = arr[i+1]; arr[i+1] = arr[h]; arr[h] = t;
            int pi = i + 1;

            stack.push(new int[]{l, pi - 1});
            stack.push(new int[]{pi + 1, h});
        }
    }
}