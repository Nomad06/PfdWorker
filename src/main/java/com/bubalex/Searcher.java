package com.bubalex;

public class Searcher {
    public static int binarySearch(int number, int[] array, int lo, int hi) {
        int mid = lo + ((hi - lo)/2);
        if (lo > hi) return -1;
        if (number > array[mid]) return binarySearch(number, array, mid + 1, hi);
        else if (number < array[mid]) return binarySearch(number, array, lo, mid - 1);
        else return number;
    }
}
