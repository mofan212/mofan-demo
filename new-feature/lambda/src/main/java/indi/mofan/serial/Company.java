package indi.mofan.serial;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author mofan
 * @date 2022/6/23 15:24
 */
@Getter
@Setter
public class Company implements Serializable {
    private static final long serialVersionUID = 2033276417972897957L;

    private String companyName;
    private List<People> employees;

    public Company(String companyName, List<People> employees) {
        this.companyName = companyName;
        this.employees = employees;
    }
}
