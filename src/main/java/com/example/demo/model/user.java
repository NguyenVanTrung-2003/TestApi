package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Builder;
@Builder
@Entity
@Table(name="nguoidung")
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private  String name;
    private  String dc;
    private  int age;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public user(long id, String name, String dc, int age) {
        this.id = id;
        this.name = name;
        this.dc = dc;
        this.age = age;
    }
    public user(){

    }

}
