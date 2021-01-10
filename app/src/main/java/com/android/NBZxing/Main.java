package com.android.NBZxing;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        int a = 8, b = 9;
        a = b = Math.min(a, b);

        System.out.println(a + ":" + b);

    }

    public static void pro(int[] ori) {
        Arrays.fill(ori, 0);
    }

}
