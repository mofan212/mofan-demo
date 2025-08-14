package indi.mofan;

import indi.mofan.serial.Animal;
import indi.mofan.serial.City;
import indi.mofan.serial.Company;
import indi.mofan.serial.Complex;
import indi.mofan.serial.Fruit;
import indi.mofan.serial.People;
import indi.mofan.serial.Simple;
import indi.mofan.serial.Singleton;
import indi.mofan.serial.Student;
import indi.mofan.serial.TransientComplex;
import indi.mofan.serial.User;
import indi.mofan.serial.Vip;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mofan
 * @date 2022/6/22 19:51
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SerialTest {
    @Test
    @Order(1)
    public void testSerial() throws Exception {
        People people = new People("Mofan", 20);
        FileOutputStream fos = new FileOutputStream("mofan.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(people);
        // 判断序列化生成的文件是否存在
        Assertions.assertTrue(new File("mofan.out").exists());
    }

    @Test
    @Order(2)
    public void testDeSerial() throws Exception {
        FileInputStream fis = new FileInputStream("mofan.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        /*
         * 构造 People 对象时，并没有调用它的无参构造方法也没有调用 clone 方法
         */
        People people = (People) ois.readObject();
        Assertions.assertEquals("Mofan", people.getName());
        Assertions.assertEquals(20, people.getAge());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerialReferenceType() throws Exception {
        List<People> list = new ArrayList<>();
        list.add(new People("mofan", 20));
        list.add(new People("MOFAN", 21));
        list.add(new People("默烦", 21));

        Company hugeCompany = new Company("大公司", list);
        Company smallCompany = new Company("小公司", list);

        // 序列化对象
        FileOutputStream fos = new FileOutputStream("company.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(list);
        oos.writeObject(hugeCompany);
        oos.writeObject(smallCompany);

        // 依次反序列化对象
        FileInputStream fis = new FileInputStream("company.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<People> deSerialList = (List<People>) ois.readObject();
        Company deSerialHugeCompany = (Company) ois.readObject();
        Company deSerialSmallCompany = (Company) ois.readObject();

        Assertions.assertSame(deSerialList, deSerialHugeCompany.getEmployees());
        Assertions.assertSame(deSerialList, deSerialSmallCompany.getEmployees());
        Assertions.assertNotSame(deSerialHugeCompany, deSerialSmallCompany);

        Assertions.assertNotSame(list, deSerialList);
    }

    @Test
    public void testNotImplSerializable() throws Exception {
        Simple simple = new Simple("simple");

        // 序列化时序列化对象未实现序列化接口
        FileOutputStream fos = new FileOutputStream("simple.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        try {
            oos.writeObject(simple);
            Assertions.fail();
        } catch (IOException e) {
            Assertions.assertTrue(e instanceof NotSerializableException);
        }
    }

    @Test
    public void testNotImplSerializableFiled() throws Exception {
        Simple simple = new Simple("test");
        Complex complex = new Complex(100, simple);

        // 序列化
        FileOutputStream fos = new FileOutputStream("complex.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        try {
            oos.writeObject(complex);
            Assertions.fail();
        } catch (IOException e) {
            // 抛出 NotSerializableException
            Assertions.assertTrue(e instanceof NotSerializableException);
        }
    }

    @Test
    public void testTransient() throws Exception {
        Simple simple = new Simple("TestString");
        TransientComplex transientComplex = new TransientComplex("string", simple, true);

        // 序列化
        FileOutputStream fos = new FileOutputStream("transient.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(transientComplex);

        // 反序列化
        FileInputStream fis = new FileInputStream("transient.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        TransientComplex object = (TransientComplex) ois.readObject();

        Assertions.assertEquals("string", object.getString());
        Assertions.assertTrue(object.isBool());
        // 虽然 Simple 没有实现序列化接口，但它被 transient 修饰，不会被序列化，因此也不会报错
        Assertions.assertNull(object.getSimple());
    }

    @Test
    public void testReadObject() throws Exception {
        Student student = new Student("mofan", -1);

        // 序列化
        FileOutputStream fos = new FileOutputStream("student.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(student);

        // 反序列化
        FileInputStream fis = new FileInputStream("student.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        try {
            ois.readObject();
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testTransientAndReadObject() throws Exception {
        User user = new User("mofan", "12345678");

        FileOutputStream fos = new FileOutputStream("user.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(user);

        FileInputStream fis = new FileInputStream("user.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        User mofan = (User) ois.readObject();

        Assertions.assertEquals("mofan", mofan.getUserName());
        Assertions.assertEquals("12345678", mofan.getPassword());
    }

    @Test
    @Disabled
    public void test() throws Exception {
        // Vip 还未继承 User
        Vip vip = new Vip();
        vip.setLevel("2");

        FileOutputStream fos = new FileOutputStream("vip.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(vip);
    }
    
    @Test
    @Disabled
    public void testReadObjectNoData() throws Exception {
        FileInputStream fis = new FileInputStream("vip.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Vip newVip = (Vip) ois.readObject();

        Assertions.assertEquals("Unknown", newVip.getUserName());
        Assertions.assertEquals("***", newVip.getPassword());
    }

    @Test
    public void testWriteReplace() throws Exception {
        City city = new City("Chengdu", 143);

        // 序列化
        FileOutputStream fos = new FileOutputStream("chengdu.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(city);

        // 反序列化
        FileInputStream fis = new FileInputStream("chengdu.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();

        Assertions.assertTrue(object instanceof People);
        People people = (People) object;
        Assertions.assertEquals("mofan", people.getName());
        Assertions.assertEquals(20, people.getAge());
    }

    @Test
    public void testReadResolve() throws Exception {
        Fruit fruit = new Fruit("orange", "orange", 2.12);

        // 序列化
        FileOutputStream fos = new FileOutputStream("orange.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(fruit);

        // 反序列化
        FileInputStream fis = new FileInputStream("orange.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();

        Assertions.assertTrue(object instanceof Simple);
        Simple simple = (Simple) object;
        Assertions.assertEquals("Simple", simple.getStringField());
    }
    
    @Test
    public void testSingleton() throws Exception {
        // 序列化
        FileOutputStream fos = new FileOutputStream("singleton.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(Singleton.getSingleton());

        // 反序列化
        FileInputStream fis = new FileInputStream("singleton.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Singleton singleton = (Singleton) ois.readObject();

//        Assertions.assertNotSame(Singleton.getSingleton(), singleton);
        Assertions.assertSame(Singleton.getSingleton(), singleton);
    }

    @Test
    public void testExternalizable() throws Exception {
        Animal animal = new Animal("cat", 1);

        // 序列化
        FileOutputStream fos = new FileOutputStream("cat.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(animal);

        // 反序列化
        FileInputStream fis = new FileInputStream("cat.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();

        Animal cat = (Animal) object;
        Assertions.assertEquals("cat", cat.getType());
        Assertions.assertEquals(1, cat.getAge());
    }
}
