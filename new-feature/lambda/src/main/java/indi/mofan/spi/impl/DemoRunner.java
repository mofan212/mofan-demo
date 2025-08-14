package indi.mofan.spi.impl;

import indi.mofan.spi.Runnable;

/**
 * @author mofan
 * @date 2022/6/15 12:43
 */
public class DemoRunner implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello, Mofan");
    }
}
