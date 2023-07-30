package indi.mofan;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2023/3/21 18:08
 */
public class BigDecimalTest {
    @Test
    @SneakyThrows
    public void testCalculatePercentage() {
        //language=JSON
        String json = """
                {
                  "A": 3,
                  "B": 1,
                  "C": 2,
                  "D": 14,
                  "E": 1,
                  "F": 6,
                  "G": 4,
                  "H": 1,
                  "I": 1,
                  "J": 1,
                  "K": 3,
                  "L": 3,
                  "M": 1,
                  "N": 1,
                  "O": 1
                }""";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Integer> map = mapper.convertValue(mapper.readTree(json), new TypeReference<>() {
        });

        int sum = map.values().stream().mapToInt(Integer::intValue).sum();
        BigDecimal numSum = new BigDecimal(String.valueOf(sum));
        BigDecimal ratioSum = new BigDecimal("0");
        int size = map.size();
        Map<String, BigDecimal> decimalMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (size == 1) {
                decimalMap.put(entry.getKey(), new BigDecimal("1").subtract(ratioSum));
            } else {
                BigDecimal decimal = new BigDecimal(String.valueOf(entry.getValue()));
                BigDecimal ratio = decimal.divide(numSum, 4, RoundingMode.HALF_UP);
                ratioSum = ratioSum.add(ratio);
                decimalMap.put(entry.getKey(), ratio);
                size--;
            }
        }
        DecimalFormat format = new DecimalFormat("0.00%");
        Map<String, String> percentageInfo = decimalMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, i -> format.format(i.getValue())));

        //language=JSON
        String resultJson = """
                {
                  "A" : "6.98%",
                  "B" : "2.33%",
                  "C" : "4.65%",
                  "D" : "32.56%",
                  "E" : "2.33%",
                  "F" : "13.95%",
                  "G" : "9.30%",
                  "H" : "2.33%",
                  "I" : "2.33%",
                  "J" : "2.33%",
                  "K" : "6.98%",
                  "L" : "6.98%",
                  "M" : "2.33%",
                  "N" : "2.33%",
                  "O" : "2.29%"
                }""";
        Assertions.assertThat(percentageInfo).isEqualTo(mapper.convertValue(mapper.readTree(resultJson), Map.class));
        BigDecimal percentageSum = decimalMap.values().stream().reduce(new BigDecimal("0"), BigDecimal::add);
        Assertions.assertThat(percentageSum).isEqualByComparingTo(new BigDecimal("1"));
    }
}
