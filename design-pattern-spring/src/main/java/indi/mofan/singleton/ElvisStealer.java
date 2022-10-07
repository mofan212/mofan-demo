package indi.mofan.singleton;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/10/7 21:12
 */
public class ElvisStealer implements Serializable {
    private static final long serialVersionUID = -5039770845538503395L;

    public ElvisStealer(Elvis payload) {
        this.payload = payload;
    }

    @Getter
    static Elvis impersonator;
    private Elvis payload;

    private Object readResolve() {
        // Save a reference to the "unresolved" Elvis instance
        impersonator = payload;

        // Return an object of correct type for favorites field
        return new String[] { "A Fool Such as I" };
    }
}
