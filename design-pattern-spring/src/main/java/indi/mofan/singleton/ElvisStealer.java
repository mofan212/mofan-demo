package indi.mofan.singleton;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/10/7 21:12
 */
public class ElvisStealer implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    static Elvis impersonator;
    private Elvis payload;

    @Serial
    private Object readResolve() {
        // Save a reference to the "unresolved" Elvis instance
        impersonator = payload;

        // Return an object of correct type for favorites field
        return new String[] { "A Fool Such as I" };
    }
}
