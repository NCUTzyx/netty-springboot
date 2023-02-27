package com.zyx.pojo;

import javax.persistence.*;

public class Relevance {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "friend_id")
    private String friendId;

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
     * @return user_id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return friend_id
     */
    public String getFriendId() {
        return friendId;
    }

    /**
     * @param friendId
     */
    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}