package indi.mofan.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mofan
 * @date 2024/3/16 22:50
 */
@Slf4j
public class YouTubeService implements VideoStreamingService {
    @Override
    public void doProcessing() {
        log.info("YouTubeService is now processing");
    }
}
