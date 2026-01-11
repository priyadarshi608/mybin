package sss.dpstate_1.context;

import sss.dpstate_1.state.State;

public class Context {
	private State state = null;

	public void setState(State state){
		this.state = state;		
	}

	public State getState(){
		return state;
	}
}
