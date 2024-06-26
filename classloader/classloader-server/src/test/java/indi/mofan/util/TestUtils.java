package indi.mofan.util;

import java.io.File;

/**
 * @author mofan
 * @date 2024/6/26 16:17
 */
public final class TestUtils {
    private TestUtils() {
    }

    public final static String COMMON_PATH = "D:\\common-sdk";

    public static boolean checkPathExists() {
        File file = new File(COMMON_PATH);
        return file.exists();
    }
}
