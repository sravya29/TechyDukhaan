package sravya.example.com.techydukhaan;

import java.io.Serializable;

public class Category implements Serializable {
    int image;
    String name;

    public Category(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public Category() {
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
