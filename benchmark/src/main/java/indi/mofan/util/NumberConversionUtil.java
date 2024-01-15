package indi.mofan.util;

import java.math.BigInteger;

/**
 * @author mofan
 * @date 2022/4/1 09:45
 */
public class NumberConversionUtil {
    /**
     * <p>初始化 62 进制数据</p>
     * <p>按照 ASCII 码顺序，先 0-9，再大写英文字母，最后小写英文字母</p>
     */
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * 使用大数表示十进制 62
     */
    private static final BigInteger SCALE_62 = new BigInteger("62", 10);

    /**
     * 使用大数表示十进制 61，作为转换 62 进制时停止递归取模的标志数
     */
    private static final BigInteger FLAG_62 = SCALE_62.subtract(new BigInteger("1", 10));

    /**
     * 将 byte 转换为 8 位二进制字符串
     *
     * @param b 需要进行转换的 byte
     * @return 8 位二进制字符串（不足 8 位以 0 填充）
     */
    private static String getBinaryCode(byte b) {
        return Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
    }

    /**
     * <p>将 byte 数组转换成 62 进制字符串</p>
     * <p>正确性验证：https://tool.lu/hexconvert </p>
     * <strong>上述网站的结果与此方法转换的英文字母大小写相反，以我为准！^-^</strong>
     *
     * @param bytes 字节数组
     * @return 62 进制字符串
     */
    public static String get62(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(getBinaryCode(b));
        }
        String binaryStr = builder.toString();
        BigInteger bigInteger = new BigInteger(binaryStr, 2);
        StringBuilder resultBuilder = new StringBuilder();
        get62StringBuilder(bigInteger, resultBuilder);
        return resultBuilder.toString();
    }

    /**
     * 通过大数（BigInteger）获取其对应的 62 进制字符串的 StringBuilder
     *
     * @param bigInteger    目标大数
     * @param resultBuilder 结果 StringBuilder
     */
    private static void get62StringBuilder(BigInteger bigInteger, StringBuilder resultBuilder) {
        if (bigInteger.compareTo(FLAG_62) > 0) {
            int index = bigInteger.mod(SCALE_62).intValue();
            bigInteger = bigInteger.divide(SCALE_62);
            get62StringBuilder(bigInteger, resultBuilder);
            resultBuilder.append(CHARS.charAt(index));
        } else {
            resultBuilder.append(CHARS.charAt(bigInteger.intValue()));
        }
    }
}
