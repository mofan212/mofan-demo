package indi.mofan.spi.impl;

import indi.mofan.spi.Runnable;

/**
 * @author mofan
 * @date 2022/6/15 12:42
 */
public class SimpleRunner implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello, World");
    }
}
