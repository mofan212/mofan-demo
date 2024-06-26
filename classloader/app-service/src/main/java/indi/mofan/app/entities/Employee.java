package indi.mofan.app.entities;

/**
 * @author mofan
 * @date 2024/6/26 10:31
 */
public class Employee {
    private String name;

    public Employee() {
        this.name = "";
    }

    public Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Employee{" +
               "name='" + name + '\'' +
               '}';
    }
}
