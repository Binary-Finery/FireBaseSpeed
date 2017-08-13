package spencerstudios.com.firebasespeed;

public class Data {

    public String userName;
    public String device;
    public long time;

    public Data(){ /*empty constructor*/}

    public Data(String userName, String device, long time){
        this.userName = userName;
        this.device = device;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
