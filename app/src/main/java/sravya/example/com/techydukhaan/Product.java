package sravya.example.com.techydukhaan;


import java.io.Serializable;

public class Product implements Serializable {

    @com.google.gson.annotations.SerializedName("img")
    private String img;

    @com.google.gson.annotations.SerializedName("age")
    private String age;

    @com.google.gson.annotations.SerializedName("cat")
    private String cat;

    @com.google.gson.annotations.SerializedName("name")
    private String name;

    @com.google.gson.annotations.SerializedName("desc")
    private String desc;

    @com.google.gson.annotations.SerializedName("id")
    private String pid;

    @com.google.gson.annotations.SerializedName("uid")
    private String uid;

    @com.google.gson.annotations.SerializedName("price")
    private String price;

    public Product(String img, String age, String cat, String name, String desc, String pid, String uid, String price) {
        this.img = img;
        this.age = age;
        this.cat = cat;
        this.name = name;
        this.desc = desc;
        this.pid = pid;
        this.uid = uid;
        this.price = price;
    }

    public Product() {
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}