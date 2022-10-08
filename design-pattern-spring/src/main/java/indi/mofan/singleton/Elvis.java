package indi.mofan.singleton;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author mofan
 * @date 2022/10/7 21:03
 */
public class Elvis implements Serializable {
    private static final long serialVersionUID = -8870240565519414478L;

    private static final Elvis INSTANCE = new Elvis();

    private String[] favoriteSongs = { "Hound Dog", "Heartbreak Hotel" };

    private Elvis() {
    }

    public Elvis getInstance() {
        return INSTANCE;
    }

    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }

    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }
}
