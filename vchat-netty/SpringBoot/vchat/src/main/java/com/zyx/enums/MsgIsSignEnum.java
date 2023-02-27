package com.zyx.enums;

/**
 *  签收 =》 是否收到消息
 * @Description: 消息签收状态 枚举
 */
public enum MsgIsSignEnum {
	
	unsign(0, "未签收"),
	signed(1, "已签收");
	
	public final Integer type;
	public final String content;
	
	MsgIsSignEnum(Integer type, String content){
		this.type = type;
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}  
}
