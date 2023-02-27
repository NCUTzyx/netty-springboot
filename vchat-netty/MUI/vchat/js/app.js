window.app = {
	
	//前端请求服务 url
	serverUrl: 'http://192.168.1.6:3000',
	
	//websocket 后端netty服务地址
	nettyServerUrl: 'ws://192.168.1.6:8088/ws',
	
	//图片服务器地址 url
	imageServerUrl: 'http://123.57.218.70:88/vchat/',
	
	// 判断字符串str 是否为空
	isNull: function(str){
		if (str != null && str != "" && str != undefined) {
			return false;
		}
		return true;
	},
	
	//封装消息提示框，封装扩展 toast
	showToast: function(msg,type){
		plus.nativeUI.toast(msg,{icon:"image/" + type + ".png",verticalAlign:"center"});
	},
	
	//保存用户全局对象
	setUserGlobalInfo: function(userInfo) {
		var	userInfoString = JSON.stringify(userInfo);
		plus.storage.setItem("userInfo",userInfoString);
	},
	
	//清空用户全局对象
	clearGlobalInfo: function() {
		plus.storage.removeItem("userInfo");
	},
	
	//获取用户全局对象
	getUserGlobalInfo: function(){
		var userInfoString = plus.storage.getItem("userInfo");
		return JSON.parse(userInfoString);
	},
	
	//保存用户联系人
	setPhoneBook: function(phoneBook){	
		var phoneBookString = JSON.stringify(phoneBook);
		plus.storage.setItem("phoneBook",phoneBookString);	
	},
	
	//获取用户联系人
	getUserPhoneBook: function(){
		var phoneBookString = plus.storage.getItem("phoneBook");
		
		if(this.isNull(phoneBookString)){
			return [];
		}
		return JSON.parse(phoneBookString);
	},
	
	//根据用户id，从本地缓存获取中获取朋友信息
	getFriendFromUserPhoneBook: function(friendId){
		var phoneBookString = plus.storage.getItem("phoneBook");
		//判断是否为null
		if(!this.isNull(phoneBookString)){
			//不为空，则返回用户信息
			var phoneBook = JSON.parse(phoneBookString);
			for(var i = 0; i < phoneBook.length; i ++){
				var friend = phoneBook[i];
				if(friend.friendUserId == friendId){	
					return friend;	
					break;
				}
			}	
		} else{
			//为空，直接返回空
			return null;
		}
	},
	
	//保存用户聊天记录 flag => 1:me， 2:friend 
	saveUserChatHistory: function(myId,friendId,message,flag) {
		
		var me = this;
		var chatKey = "chat-" + myId + "-" + friendId;
		
		// 从本地缓存获取聊天记录是否存在
		var chatHistoryListString = plus.storage.getItem(chatKey);
		var chatHistoryList;
		if(!me.isNull(chatHistoryListString)){
			// 如果不为空
			chatHistoryList = JSON.parse(chatHistoryListString);
		} else {
			chatHistoryList = [];
		}
		
		//构建聊天对象
		var singleMsg = new me.ChatHistory(myId,friendId,message,flag);	
		
		//向list集合中追加消息对象
		chatHistoryList.push(singleMsg);
		
		//保存在缓存区
		plus.storage.setItem(chatKey,JSON.stringify(chatHistoryList)); 
	},
	
	//删除本地聊天记录
	delUserChatHistory: function(myId,friendId) {
		var chatKey = "chat-" + myId + "-" + friendId;
		plus.storage.removeItem(chatKey);
	},
	
	//获取用户聊天记录
	getUserChatHistory: function(myId,friendId) {
		var me = this;
		var chatKey = "chat-" + myId + "-" + friendId;
		// 从本地缓存获取聊天记录是否存在
		var chatHistoryListString = plus.storage.getItem(chatKey);
		var chatHistoryList;
		if(!me.isNull(chatHistoryListString)){
			// 如果不为空
			chatHistoryList = JSON.parse(chatHistoryListString);
		} else {
			chatHistoryList = [];
		}
		return chatHistoryList;
	},
	
	//保存聊天记录快照，仅仅保存每次和朋友聊天的最后一条消息
	saveUserChatSnapshot: function(myId, friendId, message, isRead) {
		
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		
		// 从本地缓存获取聊天快照的list
		var chatSnapshotListString = plus.storage.getItem(chatKey);		
		var chatSnapshotList;
		
		if(!me.isNull(chatSnapshotListString)){
			// 如果不为空
			chatSnapshotList = JSON.parse(chatSnapshotListString);

			// 循环快照list,并且判断每个元素是否匹配friendId，如果匹配，则删除
			for (var i = 0; i < chatSnapshotList.length; i ++) {
				 if(chatSnapshotList[i].friendId == friendId){
					 //删除已经存在的friendId 所对应的快照对象
					 chatSnapshotList.splice(i, 1);
					 break;
				 }
			}
		} else {
			// 如果为空，赋一个空的 list
			chatSnapshotList = [];
		}	
		//构建聊天快照对象
		var singleMsg = new me.ChatSnapshot(myId,friendId,message,isRead);	
		
		//向list集合中追加快照对象 => 在前添加
		chatSnapshotList.unshift(singleMsg);
		
		//保存在缓存区
		plus.storage.setItem(chatKey,JSON.stringify(chatSnapshotList)); 
	},
	
	//删除本地聊天快照
	delUserChatSnapshot: function(myId,friendId) {	
		
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		// 从本地缓存获取聊天快照的list
		var chatSnapshotListString = plus.storage.getItem(chatKey);		
		var chatSnapshotList;
		
		if(!me.isNull(chatSnapshotListString)){
			// 如果不为空
			chatSnapshotList = JSON.parse(chatSnapshotListString);
		
			// 循环快照list,并且判断每个元素是否匹配friendId，如果匹配，则删除
			for (var i = 0; i < chatSnapshotList.length; i ++) {
				 if(chatSnapshotList[i].friendId == friendId){
					 //删除已经存在的friendId 所对应的快照对象
					 chatSnapshotList.splice(i, 1);
					 break;
				 }
			}
		} else {
			// 如果为空，不做处理
			return;
		}	
		//保存在缓存区
		plus.storage.setItem(chatKey,JSON.stringify(chatSnapshotList)); 			
	},
	
	//获取聊天记录快照，仅仅获取每次和朋友聊天的最后一条消息
	getUserChatSnapshot: function(myId) {
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		// 从本地缓存获取聊天快照的list
		var chatSnapshotListString = plus.storage.getItem(chatKey);
		var chatSnapshotList;
		if(!me.isNull(chatSnapshotListString)){
			// 如果不为空
			chatSnapshotList = JSON.parse(chatSnapshotListString);
		} else {
			chatSnapshotList = [];
		}
		return chatSnapshotList;
	},
	
	//标记未读消息为已读
	readUserChatSnapshot:function(myId,friendId) {
		
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		// 从本地缓存获取聊天快照的list
		var chatSnapshotListString = plus.storage.getItem(chatKey);
		var chatSnapshotList;
		
		if(!me.isNull(chatSnapshotListString)){
			// 如果不为空
			chatSnapshotList = JSON.parse(chatSnapshotListString);
			//循环list,判断是否存在friendId
			//如果存在，则删除该快照对象，并重新放入一个已读的快照对象
			for (var i = 0; i < chatSnapshotList.length; i ++) {
				var item = chatSnapshotList[i];
				if(item.friendId == friendId){
					item.isRead = true;   //标记为
					chatSnapshotList.splice(i, 1,item); // 替换掉原有的未读快照 
					break;
				}
			}
			//替换原有的快照列表
			plus.storage.setItem(chatKey,JSON.stringify(chatSnapshotList));	
				
		} else {
			//为空
			return;
		}
	},
	
	//后端枚举对应
	CONNECT: 1,    //第一次(或重连)初始化连接
	CHAT: 2,       //聊天消息	
	SIGNED: 3,     //消息签收
	KEEPALIVE: 4,  //客户端保持心跳
	PULL_FRIEND: 5,//重新拉取好友
	
	//和后端对应的聊天模型对象 相对应
	//ChatMsg类的构造函数
	ChatMsg: function(senderId, receiverId, message, msgId){
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.message = message;
		this.msgId = msgId;
	},
	
	//和后端对应的消息模型对象 相对应
	//ChatMsg类的构造函数
	DataContent: function(action, chatMsg, extend){
		this.action = action;
		this.chatMsg = chatMsg;
		this.extend = extend;
	},
	
	//单个聊天记录的对象
	ChatHistory : function(myId,friendId,message,flag){
		this.myId = myId;
		this.friendId = friendId;
		this.message = message;
		this.flag = flag;
	},
	
	//快照对象
	ChatSnapshot : function(myId,friendId,message,isRead){
		this.myId = myId;
		this.friendId = friendId;
		this.message = message;
		this.isRead = isRead;
	},
} 