package indi.mofan.reflections.entity;


import indi.mofan.reflections.annotaion.AnnotationForConstructor;
import indi.mofan.reflections.annotaion.AnnotationForField;
import indi.mofan.reflections.annotaion.AnnotationForMethod;
import indi.mofan.reflections.annotaion.AnnotationForParameter;
import indi.mofan.reflections.annotaion.AnnotationForType;

import java.io.Serial;

/**
 * @author mofan
 * @date 2021/3/20
 */
@AnnotationForType
public class UserInfo extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -3912662513111527890L;

    @AnnotationForField
    private String username;
    private String pwd;
    private String gender;

    @AnnotationForConstructor
    public UserInfo() {
    }

    public UserInfo(String username) {
        this.username = username;
    }

    public UserInfo(String username, String pwd) {
        this(username);
        this.pwd = pwd;
    }

    public UserInfo(String username, @AnnotationForParameter String pwd, String gender) {
        this.username = username;
        this.pwd = pwd;
        this.gender = gender;
    }

    @AnnotationForMethod
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(@AnnotationForParameter String gender) {
        this.gender = gender;
    }

    public String useMethod() {
        return getUsername() + getGender();
    }
}
