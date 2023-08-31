package indi.mofan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2023/8/31 19:23
 */
public class AustralianInterviewQuestionsTest {
    @AllArgsConstructor(staticName = "from")
    @Getter
    static class Txn {
        String userId;
        TxnType txnType;
        BigDecimal amount;
        Timestamp txnTime;
    }

    enum TxnType {
        ONE,
        TWO
    }

    Map<Long, Txn> MAP = Map.of(1L, Txn.from("123", TxnType.ONE, new BigDecimal("1"), new Timestamp(1693483200000L)));

    public BigDecimal sum(String userId, TxnType txnType) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map.Entry<Long, Txn> entry : MAP.entrySet()) {
            Txn txn = entry.getValue();
            if (!userId.equals(txn.getUserId())) {
                continue;
            }
            if (txnType != txn.getTxnType()) {
                continue;
            }
            totalAmount = totalAmount.add(txn.getAmount());
        }
        return totalAmount;
    }

    @Test
    public void testSum() {
        String userId = "";
        TxnType txnType = TxnType.ONE;
        BigDecimal sum = MAP.values().stream()
                .filter(i -> userId.equals(i.userId))
                .filter(i -> txnType.equals(i.txnType))
                .map(i -> i.amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TxnType, BigDecimal> sumGroupByTxnType(String userId) {
        Map<TxnType, BigDecimal> resultMap = new EnumMap<>(TxnType.class);
        for (Txn txn : MAP.values()) {
            if (!userId.equals(txn.getUserId())) {
                continue;
            }
            TxnType txnType = txn.getTxnType();
            BigDecimal amount = resultMap.computeIfAbsent(txnType, type -> BigDecimal.ZERO);
            resultMap.put(txnType, amount.add(txn.getAmount()));
        }
        return resultMap;
    }

    @Test
    public void testSumGroupByTxnType() {
        String userId = "";
        Map<TxnType, BigDecimal> result = MAP.values().stream()
                .filter(i -> userId.equals(i.userId))
                .collect(Collectors.groupingBy(i -> i.txnType,
                        Collectors.mapping(i -> i.amount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    public Map<Timestamp, Set<String>> getUserSetMap() {
        Map<Timestamp, Set<String>> resultMap = new TreeMap<>();
        for (Map.Entry<Long, Txn> entry : MAP.entrySet()) {
            Txn txn = entry.getValue();
            Set<String> userSet = resultMap.computeIfAbsent(txn.getTxnTime(), k -> new LinkedHashSet<>());
            userSet.add(txn.getUserId());
        }
        return resultMap;
    }

    @Test
    public void testGetUserSetMap() {
        Map<Timestamp, Set<String>> result = MAP.values().stream()
                .collect(Collectors.groupingBy(i -> i.txnTime, Collectors.mapping(i -> i.userId, Collectors.toCollection(LinkedHashSet::new))));
    }
}
