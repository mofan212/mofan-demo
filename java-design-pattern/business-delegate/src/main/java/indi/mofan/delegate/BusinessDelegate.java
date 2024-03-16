package indi.mofan.delegate;

import indi.mofan.lookup.BusinessLookup;
import lombok.Setter;

/**
 * @author mofan
 * @date 2024/3/16 23:13
 */
@Setter
public class BusinessDelegate {
    private BusinessLookup lookup;

    public void playbackMovie(String movie) {
        lookup.getBusinessService(movie).doProcessing();
    }
}
