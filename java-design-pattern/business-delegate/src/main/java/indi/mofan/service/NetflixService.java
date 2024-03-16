package indi.mofan.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mofan
 * @date 2024/3/16 22:49
 */
@Slf4j
public class NetflixService implements VideoStreamingService{
    @Override
    public void doProcessing() {
        log.info("NetflixService is now processing");
    }
}
