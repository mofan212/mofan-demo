package indi.mofan;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import indi.mofan.pojo.serial.Computer;
import indi.mofan.pojo.serial.Phone;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

/**
 * @author mofan
 * @date 2023/6/15 20:42
 */
public class SerialPersistentFieldsTest implements WithAssertions {
    @Test
    @SneakyThrows
    public void testSerializedPhone() {
        Phone phone = buildPhone("Are you Ok?");

        String filePath = "./target/mi.out";
        FileOutputStream fos = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        try (fos; oos) {
            oos.writeObject(phone);
        } catch (IOException e) {
            Assertions.fail();
        }

        assertThat(new File(filePath))
                .isNotEmpty()
                .content()
                .asString()
                .doesNotContain("Are you Ok?");

        FileInputStream fis = new FileInputStream(filePath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        try (fis; ois) {
            Phone result = (Phone) ois.readObject();
            assertThat(result).extracting(Phone::getName, Phone::getPrice, i -> Phone.staticStr)
                    .containsSequence("XiaoMi", null, "Are you Ok?");
        } catch (IOException e) {
            Assertions.fail();
        }
    }

    private Phone buildPhone(String staticStr) {
        Phone phone = new Phone();
        phone.setName("XiaoMi");
        phone.setPrice(new BigDecimal("1999.0"));
        Phone.staticStr = staticStr;
        return phone;
    }

    @Test
    @SneakyThrows
    public void testSerializedPhoneByJackson() {
        Phone phone = buildPhone("Hello, Thank you");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(phone);

        String except = """
                {
                    "name": "XiaoMi"
                }
                """;
        assertThatJson(json)
                .isObject()
                .isNotEqualTo(except)
                .containsEntry("price", new BigDecimal("1999.0"))
                // 静态字段不会被反序列化
                .doesNotContainKey("staticStr");

        // 不序列化被 transient 修饰的字段
        mapper = JsonMapper.builder().configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true).build();
        json = mapper.writeValueAsString(phone);
        assertThatJson(json).isEqualTo(except);
    }

    @Test
    @SneakyThrows
    public void testSerializedComputer() {
        Computer computer = buildComputer();

        String filePath = "./target/rog.out";
        FileOutputStream fos = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        try (fos; oos) {
            oos.writeObject(computer);
        } catch (IOException e) {
            Assertions.fail();
        }
        assertThat(new File(filePath))
                .isNotEmpty()
                .content()
                .doesNotContain("Republic of Gamers");

        FileInputStream fis = new FileInputStream(filePath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        try (fis; ois) {
            Computer result = (Computer) ois.readObject();
            assertThat(result).extracting(Computer::getBrand, Computer::getSize, Computer::getPrice, i -> Computer.staticStr)
                    .containsSequence("ROG", null, new BigDecimal("10000.00"), "Republic of Gamers");
        } catch (IOException e) {
            Assertions.fail();
        }
    }

    private Computer buildComputer() {
        Computer computer = new Computer();
        computer.setBrand("ROG");
        computer.setPrice(new BigDecimal("10000.00"));
        computer.setSize("16 inch");
        Computer.staticStr = "Republic of Gamers";
        return computer;
    }
}
