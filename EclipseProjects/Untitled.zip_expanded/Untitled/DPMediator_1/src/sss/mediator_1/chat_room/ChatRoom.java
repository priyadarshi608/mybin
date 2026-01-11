package sss.mediator_1.chat_room;

import java.util.Date;

import sss.mediator_1.stuffs.User;

public class ChatRoom {
   public static void showMessage(User user, String message){
      System.out.println(new Date().toString() + " [" + user.getName() + "] : " + message);
   }
}