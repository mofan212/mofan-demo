package indi.mofan.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author mofan
 * @date 2021/3/20
 */
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 5068983365017772168L;

    private Long id;
    private Date createTime;
    private Date updateTime;
}
