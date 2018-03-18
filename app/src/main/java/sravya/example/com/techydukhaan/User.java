package sravya.example.com.techydukhaan;

import java.io.Serializable;

public class User implements Serializable {

    @com.google.gson.annotations.SerializedName("name")
    private String name;

    @com.google.gson.annotations.SerializedName("cllg")
    private String cllg;

    @com.google.gson.annotations.SerializedName("addr")
    private String addr;

    @com.google.gson.annotations.SerializedName("phNum")
    private String phNum;

    @com.google.gson.annotations.SerializedName("mail")
    private String mail;

    @com.google.gson.annotations.SerializedName("id")
    private String uid;


    public User() {
    }

    public User(String name, String cllg, String addr, String phNum, String mail, String uid) {
        this.name = name;
        this.cllg = cllg;
        this.addr = addr;
        this.phNum = phNum;
        this.mail = mail;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCllg() {
        return cllg;
    }

    public void setCllg(String cllg) {
        this.cllg = cllg;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhNum() {
        return phNum;
    }

    public void setPhNum(String phNum) {
        this.phNum = phNum;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
