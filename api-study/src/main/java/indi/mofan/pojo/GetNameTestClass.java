package indi.mofan.pojo;

/**
 * @author mofan
 * @date 2023/2/15 10:53
 */
public class GetNameTestClass {

    public static class Inner {

    }

    public static void fun() {
        class LocalClassInMethod {

        }

        Class<LocalClassInMethod> localClazz = LocalClassInMethod.class;
        System.out.println(localClazz.getName());
        System.out.println(localClazz.getCanonicalName());
        System.out.println(localClazz.getSimpleName());
        System.out.println(localClazz.getTypeName());
    }

    static {
        class LocalClassInStaticBlock {

        }

        Class<LocalClassInStaticBlock> localClazz = LocalClassInStaticBlock.class;
        System.out.println(localClazz.getName());
        System.out.println(localClazz.getCanonicalName());
        System.out.println(localClazz.getSimpleName());
        System.out.println(localClazz.getTypeName());
    }
}
