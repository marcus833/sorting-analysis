package com.sorting.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelBubbleSort {
    public static void sort(int[] arr, int numThreads) throws InterruptedException {
        int n = arr.length;
        for (int phase = 0; phase < n; phase++) {
            final int ph = phase;
            ExecutorService pool = Executors.newFixedThreadPool(numThreads);
            int start = (ph % 2 == 0) ? 0 : 1;
            for (int i = start; i < n - 1; i += 2) {
                final int idx = i;
                pool.submit(() -> {
                    if (arr[idx] > arr[idx + 1]) {
                        int tmp = arr[idx]; arr[idx] = arr[idx + 1]; arr[idx + 1] = tmp;
                    }
                });
            }
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}