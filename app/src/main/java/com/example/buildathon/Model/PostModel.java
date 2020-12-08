package com.example.buildathon.Model;

public class PostModel {
    String username , usernumber  , posttext , postimg , postdate , posttime ,postid;

    public PostModel() {
    }

    public PostModel(String username, String usernumber, String posttext, String postimg, String postdate, String posttime, String postid) {
        this.username = username;
        this.usernumber = usernumber;
        this.posttext = posttext;
        this.postimg = postimg;
        this.postdate = postdate;
        this.posttime = posttime;
        this.postid = postid;
    }

    public String getPostId() {
        return postid;
    }

    public void setPostId(String postid) {
        this.postid = postid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernumber() {
        return usernumber;
    }

    public void setUsernumber(String usernumber) {
        this.usernumber = usernumber;
    }


    public String getPosttext() {
        return posttext;
    }

    public void setPosttext(String posttext) {
        this.posttext = posttext;
    }

    public String getPostimg() {
        return postimg;
    }

    public void setPostimg(String postimg) {
        this.postimg = postimg;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }
}
