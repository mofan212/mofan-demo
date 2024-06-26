package indi.mofan.classloaders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 从任意指定的某个目录中读取字节码类文件，然后创建加载对应的类
 *
 * @author mofan
 * @date 2024/6/26 10:34
 */
public class MyCommonClassLoader extends ClassLoader {

    static {
        // 表明当前的ClassLoader可并行加载不同的类
        registerAsParallelCapable();
    }

    private static String cleanPath(String path) {
        if (!path.isEmpty()
            && path.charAt(path.length() - 1) != File.separatorChar) {
            path += File.separator;
        }
        return path;
    }

    /**
     * 指定的字节码类文件所在的本地目录
     */
    private final String commonPath;

    /**
     * 构造函数。默认的parent ClassLoader是 AppClassLoader
     *
     * @param commonPath 字节码类文件所在的本地目录
     */
    public MyCommonClassLoader(String commonPath) {
        this.commonPath = cleanPath(commonPath);
    }

    /**
     * 构造函数。指定了一个 parent ClassLoader。
     *
     * @param commonPath 字节码类文件所在的本地目录
     * @param parent     指定的parent ClassLoader
     */
    public MyCommonClassLoader(String commonPath, ClassLoader parent) {
        super(parent);
        this.commonPath = cleanPath(commonPath);
    }

    /**
     * 覆盖父类的 findClass(..) 方法。
     * 从指定的目录中查找字节码类文件，并创建加载对应的 Class 对象。
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            // 读取字节码的二进制流
            byte[] b = loadClassFromFile(name);
            // 调用 defineClass(..) 方法创建 Class 对象
            return defineClass(name, b, 0, b.length);
        } catch (IOException ex) {
            throw new ClassNotFoundException(name);
        }
    }

    private byte[] loadClassFromFile(String name) throws IOException {
        String fileName = name.replace('.', File.separatorChar) + ".class";
        String filePath = this.commonPath + fileName;

        try (InputStream inputStream = new FileInputStream(filePath);
             ByteArrayOutputStream byteStream = new ByteArrayOutputStream()
        ) {
            int nextValue;
            while ((nextValue = inputStream.read()) != -1) {
                byteStream.write(nextValue);
            }
            return byteStream.toByteArray();
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 执行 ClassLoaderTranslateTest 时放开注释
        // System.out.println("准备使用 MyCommonClassLoader 加载类：" + name);
        return super.loadClass(name, resolve);
    }
}
