package indi.mofan.cilent;

import indi.mofan.delegate.BusinessDelegate;
import lombok.RequiredArgsConstructor;

/**
 * @author mofan
 * @date 2024/3/16 23:15
 */
@RequiredArgsConstructor
public class MobileClient {
    private final BusinessDelegate businessDelegate;

    public void playbackMovie(String movie) {
        businessDelegate.playbackMovie(movie);
    }
}
