package indi.mofan.lookup;

import indi.mofan.service.NetflixService;
import indi.mofan.service.VideoStreamingService;
import indi.mofan.service.YouTubeService;
import lombok.Setter;

/**
 * @author mofan
 * @date 2024/3/16 23:11
 */
@Setter
public class BusinessLookup {
    private NetflixService netflixService;
    private YouTubeService youTubeService;

    public VideoStreamingService getBusinessService(String movie) {
        if (movie.toLowerCase().contains("die hard")) {
            return netflixService;
        } else {
            return youTubeService;
        }
    }
}
