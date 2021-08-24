package cn.baizhi.entity;

import java.io.Serializable;
import java.util.Date;

public class Emp implements Serializable {
    private String id;
    private Integer age;
    private String name;
    private Date bir;
    private String con;

    public Emp() {
    }

    public Emp(String id, Integer age, String name, Date bir, String con) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.bir = bir;
        this.con = con;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBir() {
        return bir;
    }

    public void setBir(Date bir) {
        this.bir = bir;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    @Override
    public String toString() {
        return "Emp{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", bir=" + bir +
                ", con='" + con + '\'' +
                '}';
    }
}
