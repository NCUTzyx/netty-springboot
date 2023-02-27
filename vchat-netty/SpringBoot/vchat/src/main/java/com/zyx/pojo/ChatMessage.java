package com.zyx.pojo;

import java.util.Date;
import javax.persistence.*;

@Table(name = "chat_message")
public class ChatMessage {
    @Id
    private String id;

    @Column(name = "send_user")
    private String sendUser;

    @Column(name = "accept_user")
    private String acceptUser;

    private String message;

    @Column(name = "is_read")
    private Integer isRead;  //是否签收

    @Column(name = "create_time")
    private Date createTime;

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
     * @return send_user
     */
    public String getSendUser() {
        return sendUser;
    }

    /**
     * @param sendUser
     */
    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    /**
     * @return accept_user
     */
    public String getAcceptUser() {
        return acceptUser;
    }

    /**
     * @param acceptUser
     */
    public void setAcceptUser(String acceptUser) {
        this.acceptUser = acceptUser;
    }

    /**
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return is_read
     */
    public Integer getIsRead() {
        return isRead;
    }

    /**
     * @param isRead
     */
    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}