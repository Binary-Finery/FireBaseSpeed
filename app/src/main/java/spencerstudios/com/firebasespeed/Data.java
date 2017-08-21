package spencerstudios.com.firebasespeed;

public class Data {

    public String userName;
    public String make;
    public String model;
    public long time;

    public Data(){}

    public Data(String userName, String make, String model, long time){
        this.userName = userName;
        this.make = make;
        this.model = model;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
