package com.zyx.enums;

/**
 * 
 * @Description: 同意或者拒绝 好友请求的枚举
 */
public enum OperatorFriendRequestTypeEnum {
	
	REFUSE(0, "拒绝"),
	ACCEPT(1, "同意");
	
	public final Integer type;
	public final String msg;
	
	OperatorFriendRequestTypeEnum(Integer type, String msg){
		this.type = type;
		this.msg = msg;
	}
	
	public Integer getType() {
		return type;
	}
	
	public static String getMsgByType(Integer type) {
		for (OperatorFriendRequestTypeEnum operType : OperatorFriendRequestTypeEnum.values()) {
			if (operType.getType() == type) {
				return operType.msg;
			}
		}
		return null;
	}
	
}
