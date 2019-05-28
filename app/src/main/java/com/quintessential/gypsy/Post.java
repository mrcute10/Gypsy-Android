package com.quintessential.gypsy;
import com.google.gson.annotations.SerializedName;

public class Post {
    private int userId6;
    private String userEmail6;
    private String userPass6;
    private int usercount6Login;
    private String logoutDate6;
    private String logoutTime6;

    public int getUsercount6Login() {
        return usercount6Login;
    }

    @SerializedName("body")

    private String text;

    public int getUserId6() {
        return userId6;
    }

    public String getUserEmail6() {
        return userEmail6;
    }

    public String getUserPass6() { return userPass6; }

    public Post(String userEmail6, String userPass6) {
        this.userEmail6 = userEmail6;
        this.userPass6=userPass6;
    }
    public Post(String userEmail6, String userPass6,String logoutDate6,String logoutTime6) {
        this.userEmail6 = userEmail6;
        this.userPass6=userPass6;
        this.logoutDate6=logoutDate6;
        this.logoutTime6=logoutTime6;

    }
}
