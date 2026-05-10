package com.sorting.parallel;

import java.util.concurrent.*;

public class ParallelSelectionSort {
    public static void sort(int[] arr, int numThreads) throws Exception {
        int n = arr.length;
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < n - 1; i++) {
            final int start = i;
            int chunkSize = Math.max(1, (n - start) / numThreads);
            Future<Integer>[] futures = new Future[numThreads];
            for (int t = 0; t < numThreads; t++) {
                final int from = start + t * chunkSize;
                final int to = (t == numThreads - 1) ? n : Math.min(from + chunkSize, n);
                if (from >= n) break;
                futures[t] = pool.submit(() -> {
                    int minIdx = from;
                    for (int j = from + 1; j < to; j++)
                        if (arr[j] < arr[minIdx]) minIdx = j;
                    return minIdx;
                });
            }
            int minIdx = start;
            for (Future<Integer> f : futures) {
                if (f == null) break;
                int idx = f.get();
                if (arr[idx] < arr[minIdx]) minIdx = idx;
            }
            int tmp = arr[minIdx]; arr[minIdx] = arr[start]; arr[start] = tmp;
        }
        pool.shutdown();
    }
}