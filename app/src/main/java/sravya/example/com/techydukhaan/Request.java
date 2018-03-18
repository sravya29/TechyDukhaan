package sravya.example.com.techydukhaan;

public class Request {

    @com.google.gson.annotations.SerializedName("sendid")
    String sendid;

    @com.google.gson.annotations.SerializedName("recvid")
    String recvid;

    @com.google.gson.annotations.SerializedName("pid")
    String pid;

    @com.google.gson.annotations.SerializedName("id")
    String id;

    public Request(String sendid, String recvid, String pid, String id) {
        this.sendid = sendid;
        this.recvid = recvid;
        this.pid = pid;
        this.id = id;
    }

    public Request() {
    }

    public String getSendid() {
        return sendid;
    }

    public void setSendid(String sendid) {
        this.sendid = sendid;
    }

    public String getRecvid() {
        return recvid;
    }

    public void setRecvid(String recvid) {
        this.recvid = recvid;
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