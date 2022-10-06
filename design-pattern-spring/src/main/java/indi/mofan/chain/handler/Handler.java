package indi.mofan.chain.handler;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mofan
 * @date 2022/10/6 16:00
 */
public abstract class Handler {

    /**
     * 链表的下一个对象
     */
    private Handler next;

    public void setNext(Handler next) {
        this.next = next;
    }

    public Handler getNext() {
        return next;
    }

    public final String handle(String request) {
        String resp = handleRequest(request);
        if (StringUtils.isEmpty(resp) && getNext() != null) {
            return getNext().handle(request);
        } else {
            return resp;
        }
    }


    /**
     * 处理请求的方法
     *
     * @param request 请求信息
     * @return 处理结果
     */
    public abstract String handleRequest(String request);

}
