package sss.dpstate_1.state;

import sss.dpstate_1.context.Context;

public class StartState implements State {

	public void doAction(Context context) {
		System.out.println("Player is in start state");
		context.setState(this);	
	}

	public String toString(){
		return "Start State";
	}
}
