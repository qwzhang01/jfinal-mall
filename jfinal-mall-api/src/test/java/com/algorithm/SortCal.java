package com.algorithm;

import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;

public class SortCal {

    public static void main(String[] args)  {

        long l = System.currentTimeMillis();
        Integer[] data = getData(99999999);
        long l1 = System.currentTimeMillis();
        System.out.println("组装耗时：" + (l1 - l) + " 豪秒");

        l = System.currentTimeMillis();
        quicksort(data, 0, data.length - 1);
        l1 = System.currentTimeMillis();
        System.out.println("排序耗时：" + (l1 - l) + " 豪秒");

        if (data.length <= 10) {
            System.out.println(" Sort Result " + Arrays.toString(data));
        }

        Runtime.getRuntime().exit(0);
    }

    private static void quicksort(Integer[] data, int i, int i1) {

    }

    private static void merge(Integer[] data, int s, int e) {
        if (s >= e) {
            return;
        }
        int m = (s + e) / 2;
        merge(data, s, m);
        merge(data, m + 1, e);
        mergeData(data, s, m, e);
    }

    private static void mergeData(Integer[] data, int s, int m, int e) {
        int[] tmp = new int[1 + e - s];
        int k = 0;
        int a = s;
        int b = m + 1;
        while (a <= m && b <= e) {
            if (data[a] > data[b]) {
                tmp[k++] = data[a++];
            } else {
                tmp[k++] = data[b++];
            }
        }
        while (a <= m) {
            tmp[k++] = data[a++];
        }
        while (b <= e) {
            tmp[k++] = data[b++];
        }
        k = 0;
        while (s <= e) {
            data[s++] = tmp[k++];
        }
    }


    private static Integer[] breakIn(Integer[] data) {
        for (int i = 1; i < data.length; ++i) {
            int v = data[i];
            for (int j = i - 1; j >= 0; --j) {
                if (data[j] > v) {
                    data[j + 1] = data[j];
                    data[j] = v;
                }
            }
        }
        return data;
    }

    // 3521521 豪秒
    private static Integer[] bubbling(Integer[] data) {
        int tmp = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length - 1 - i; j++) {
                if (data[j] > data[j + 1]) {
                    tmp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = tmp;
                }
            }
        }
        return data;
    }

    private static Integer[] getData(int length) {
        Integer[] data = new Integer[length];
        for (int i = 0; i < length; i++) {
            data[i] = RandomUtils.nextInt(0, 100);
        }
        if (length <= 10) {
            System.out.println("Gen Result" + Arrays.toString(data));
        }
        return data;
    }
}
