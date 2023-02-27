package com.zyx.pojo;

import javax.persistence.*;

public class User {
    @Id
    private String id;

    private String username;

    private String password;

    @Column(name = "user_photo")
    private String userPhoto;

    @Column(name = "user_photo_big")
    private String userPhotoBig;

    private String nickname;

    private String qrcode;

    private String cid;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return user_photo
     */
    public String getUserPhoto() {
        return userPhoto;
    }

    /**
     * @param userPhoto
     */
    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    /**
     * @return user_photo_big
     */
    public String getUserPhotoBig() {
        return userPhotoBig;
    }

    /**
     * @param userPhotoBig
     */
    public void setUserPhotoBig(String userPhotoBig) {
        this.userPhotoBig = userPhotoBig;
    }

    /**
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return qrcode
     */
    public String getQrcode() {
        return qrcode;
    }

    /**
     * @param qrcode
     */
    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    /**
     * @return cid
     */
    public String getCid() {
        return cid;
    }

    /**
     * @param cid
     */
    public void setCid(String cid) {
        this.cid = cid;
    }
}