package indi.mofan.app;

import indi.mofan.app.entities.Employee;
import indi.mofan.app.entities.Manager;
import indi.mofan.service.api.Service;

/**
 * @author mofan
 * @date 2024/6/26 10:30
 */
public class CompanyService implements Service {
    @Override
    public void start() {
        System.out.println("start Service[" + this + ']');
        Employee employee = new Employee("张三");
        Manager manager = new Manager("李四");

        String employeeStr = employee.toString().toLowerCase();
        String managerStr = manager.toString().toLowerCase();

        System.out.println("公司员工: " + employeeStr);
        System.out.println("公司经理: " + managerStr);
    }

    @Override
    public void stop() {
        System.out.println("stop Service[" + this + ']');
    }
}
