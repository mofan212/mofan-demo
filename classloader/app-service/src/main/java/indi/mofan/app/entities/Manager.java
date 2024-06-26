package indi.mofan.app.entities;

/**
 * @author mofan
 * @date 2024/6/26 10:31
 */
public class Manager extends Employee {
    public Manager() {
    }

    public Manager(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "Manager{" +
               "name='" + getName() + '\'' +
               '}';
    }
}
