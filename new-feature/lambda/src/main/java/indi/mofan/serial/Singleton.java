package indi.mofan.serial;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/7/1 12:53
 */
public class Singleton implements Serializable {
    private static final long serialVersionUID = 5603073114730981002L;

    private Singleton() {

    }

    private static class SingletonHolder {
        private static final Singleton SINGLETON = new Singleton();
    }

    public static synchronized Singleton getSingleton() {
        return SingletonHolder.SINGLETON;
    }

    private Object readResolve() throws ObjectStreamException {
        return getSingleton();
    }
}
