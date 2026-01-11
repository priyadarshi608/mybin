package sss.mediator_1.stuffs;

import sss.mediator_1.chat_room.ChatRoom;

public class User {
	private String name;

	public String getName() {
		return name;
	}

	public User(String name){
		this.name  = name;
	}

	public void sendMessage(String message){
		ChatRoom.showMessage(this,message);
	}
}
