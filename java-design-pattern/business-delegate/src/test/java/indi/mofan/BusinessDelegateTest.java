package indi.mofan;

import indi.mofan.cilent.MobileClient;
import indi.mofan.delegate.BusinessDelegate;
import indi.mofan.lookup.BusinessLookup;
import indi.mofan.service.NetflixService;
import indi.mofan.service.YouTubeService;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2024/3/16 23:16
 */
public class BusinessDelegateTest {
    @Test
    public void test() {
        // 下面的注入在引入 Spring 后将十分方便
        var businessDelegate = new BusinessDelegate();
        var lookup = new BusinessLookup();
        lookup.setNetflixService(new NetflixService());
        lookup.setYouTubeService(new YouTubeService());
        businessDelegate.setLookup(lookup);

        MobileClient client = new MobileClient(businessDelegate);
        client.playbackMovie("Die Hard 2");
        client.playbackMovie("Maradona: The Greatest Ever");
    }
}
