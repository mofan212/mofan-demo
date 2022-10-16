package indi.mofan;

import indi.mofan.pojo.People;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/10/16 17:43
 */
public class TestIO {
    @Test
    public void testSerializable() {
        People people = new People();
        people.setName("mofan");
        byte[] bytes = getBytesFromObject(people);
        People result = (People) deserialize(bytes);
        Assert.assertEquals("mofan", result.getName());
    }

    @SneakyThrows
    private byte[] getBytesFromObject(Serializable serializable) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bo);
        oos.writeObject(serializable);
        return bo.toByteArray();
    }

    @SneakyThrows
    private Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
}
