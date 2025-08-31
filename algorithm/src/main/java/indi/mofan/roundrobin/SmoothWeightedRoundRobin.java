package indi.mofan.roundrobin;


import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author mofan
 * @date 2025/8/29 16:41
 * @link <a href="https://mp.weixin.qq.com/s/u5ZebSbliyBoiwprXNHkjg">平滑加权轮询的数学原理</a>
 * @link <a href="https://tenfy.cn/posts/smooth-weighted-round-robin/">nginx平滑的基于权重轮询算法分析</a>
 */
public class SmoothWeightedRoundRobin {
    private final List<Server> servers;
    private final int totalWeight;

    public SmoothWeightedRoundRobin(Collection<Server> servers) {
        if (CollectionUtils.isEmpty(servers)) {
            throw new IllegalArgumentException("servers is empty");
        }
        this.servers = new ArrayList<>();
        int total = 0;
        for (Server server : servers) {
            this.servers.add(server);
            total += server.getWeight();
        }
        this.totalWeight = total;
    }

    public Server getNextServer() {
        /*
         * 1. 遍历节点列表，让它们的 currentWeight 都加上自己的 weight
         * 2. 找出最大的 currentWeight
         */
        Server maxCurrentWeight = this.servers.getFirst();
        for (Server server : this.servers) {
            server.currentWeight += server.getWeight();
            if (server.currentWeight > maxCurrentWeight.currentWeight) {
                maxCurrentWeight = server;
            }
        }
        // 3. 选中节点的权重减去总权重
        Objects.requireNonNull(maxCurrentWeight).currentWeight -= this.totalWeight;
        return maxCurrentWeight;
    }

    @Getter
    public static class Server {
        private final String name;
        private final int weight;
        private int currentWeight = 0;

        public Server(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }
    }
}
