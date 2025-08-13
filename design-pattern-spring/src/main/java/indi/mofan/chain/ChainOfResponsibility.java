package indi.mofan.chain;

import indi.mofan.chain.handler.Handler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author mofan
 * @date 2022/10/6 16:19
 */
@Component
public class ChainOfResponsibility {

    @Autowired
    private List<Handler> handlerList;

    private Handler handler;

    @PostConstruct
    public void initHandlerList() {
        for (int i = 0; i < handlerList.size(); i++) {
            if (i == 0) {
                handler = handlerList.get(0);
            } else {
                Handler currentHandler = handlerList.get(i - 1);
                Handler nextHandler = handlerList.get(i);
                currentHandler.setNext(nextHandler);
            }
        }
    }

    /**
     * 责任链执行主方法
     *
     * @param req 请求
     * @return 响应信息
     */
    public String exec(String req) {
        return handler.handle(req);
    }
}
