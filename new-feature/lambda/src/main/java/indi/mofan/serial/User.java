package indi.mofan.serial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/7/2 21:27
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = -4647801022792976129L;

    private String userName;
    private transient String password;

    private void writeObject(ObjectOutputStream oos) throws Exception {
        oos.defaultWriteObject();

        oos.writeObject(this.password);
    }

    private void readObject(ObjectInputStream ois) throws Exception{
        ois.defaultReadObject();

        this.password = (String) ois.readObject();
    }

    private void readObjectNoData() {
        this.userName = "Unknown";
        this.password = "***";
    }
}
