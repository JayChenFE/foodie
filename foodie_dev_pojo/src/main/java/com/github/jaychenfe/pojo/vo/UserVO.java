package com.github.jaychenfe.pojo.vo;

import java.util.Date;

/**
 * @author jaychenfe
 */

public class UserVO {
    private String username;
    private String face;
    private Date birthday;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "username='" + username + '\'' +
                ", face='" + face + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
