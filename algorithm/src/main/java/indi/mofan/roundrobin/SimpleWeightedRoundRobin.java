package indi.mofan.roundrobin;


import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author mofan
 * @date 2025/8/29 14:04
 */
public class SimpleWeightedRoundRobin {
    private final List<Server> servers;
    private final int totalWeight;
    private int index = 0;
    private int remaining;

    public SimpleWeightedRoundRobin(List<Server> servers) {
        if (CollectionUtils.isEmpty(servers)) {
            throw new IllegalArgumentException("servers is empty");
        }
        this.servers = new ArrayList<>(servers);
        this.servers.sort(Comparator.comparingInt(Server::getWeight));
        int total = 0;
        for (Server server : this.servers) {
            total += server.getWeight();
        }
        this.totalWeight = total;
        this.remaining = total;
    }

    public Server getNextServer() {
        if (remaining == 0) {
            reset();
        }

        while (true) {
            Server server = servers.get(index++);
            if (index == servers.size()) {
                index = 0;
            }
            if (server.getValue() > 0) {
                remaining--;
                server.decrementValue();
                return server;
            }
        }
    }

    private void reset() {
        for (Server server : servers) {
            server.reset();
        }
        this.remaining = this.totalWeight;
    }

    @Getter
    public static class Server {
        private final String name;
        private final int weight;
        private int value;

        public Server(String name, int weight) {
            this.name = name;
            this.weight = weight;
            this.value = weight;
        }

        @CanIgnoreReturnValue
        public int decrementValue() {
            return --value;
        }

        public void reset() {
            value = weight;
        }
    }
}
