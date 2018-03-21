package sravya.example.com.techydukhaan;

public class Request {

    @com.google.gson.annotations.SerializedName("uid")
    String uid;

    @com.google.gson.annotations.SerializedName("pid")
    String pid;

    @com.google.gson.annotations.SerializedName("id")
    String id;

    public Request(String uid, String pid, String id) {
        this.uid = uid;
        this.pid = pid;
        this.id = id;
    }

    public Request() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
