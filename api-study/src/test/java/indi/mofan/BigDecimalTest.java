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
        String json = "{\n" +
                "  \"A\": 3,\n" +
                "  \"B\": 1,\n" +
                "  \"C\": 2,\n" +
                "  \"D\": 14,\n" +
                "  \"E\": 1,\n" +
                "  \"F\": 6,\n" +
                "  \"G\": 4,\n" +
                "  \"H\": 1,\n" +
                "  \"I\": 1,\n" +
                "  \"J\": 1,\n" +
                "  \"K\": 3,\n" +
                "  \"L\": 3,\n" +
                "  \"M\": 1,\n" +
                "  \"N\": 1,\n" +
                "  \"O\": 1\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Integer> map = mapper.convertValue(mapper.readTree(json), new TypeReference<Map<String, Integer>>() {
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
        String resultJson = "{\n" +
                "  \"A\" : \"6.98%\",\n" +
                "  \"B\" : \"2.33%\",\n" +
                "  \"C\" : \"4.65%\",\n" +
                "  \"D\" : \"32.56%\",\n" +
                "  \"E\" : \"2.33%\",\n" +
                "  \"F\" : \"13.95%\",\n" +
                "  \"G\" : \"9.30%\",\n" +
                "  \"H\" : \"2.33%\",\n" +
                "  \"I\" : \"2.33%\",\n" +
                "  \"J\" : \"2.33%\",\n" +
                "  \"K\" : \"6.98%\",\n" +
                "  \"L\" : \"6.98%\",\n" +
                "  \"M\" : \"2.33%\",\n" +
                "  \"N\" : \"2.33%\",\n" +
                "  \"O\" : \"2.29%\"\n" +
                "}";
        Assertions.assertThat(percentageInfo).isEqualTo(mapper.convertValue(mapper.readTree(resultJson), Map.class));
        BigDecimal percentageSum = decimalMap.values().stream().reduce(new BigDecimal("0"), BigDecimal::add);
        Assertions.assertThat(percentageSum).isEqualByComparingTo(new BigDecimal("1"));
    }
}
