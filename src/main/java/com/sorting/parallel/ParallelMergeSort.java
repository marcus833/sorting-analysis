package com.sorting.parallel;

import com.sorting.algorithms.MergeSort;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class ParallelMergeSort {

    private static class MergeSortTask extends RecursiveAction {
        private final int[] arr;
        private final int left, right;
        private final int threshold = 5000;

        MergeSortTask(int[] arr, int left, int right) {
            this.arr = arr; this.left = left; this.right = right;
        }

        @Override
        protected void compute() {
            if (right - left < threshold) {
                MergeSort.sort(arr, left, right);
            } else {
                int mid = (left + right) / 2;
                MergeSortTask t1 = new MergeSortTask(arr, left, mid);
                MergeSortTask t2 = new MergeSortTask(arr, mid + 1, right);
                invokeAll(t1, t2);
                MergeSort.merge(arr, left, mid, right);
            }
        }
    }

    public static void sort(int[] arr, int numThreads) {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        pool.invoke(new MergeSortTask(arr, 0, arr.length - 1));
        pool.shutdown();
    }
}