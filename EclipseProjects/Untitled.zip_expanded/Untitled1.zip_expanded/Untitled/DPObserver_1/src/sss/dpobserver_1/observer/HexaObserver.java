package sss.dpobserver_1.observer;

import sss.dpobserver_1.subject.Subject;

public class HexaObserver extends Observer{

	public HexaObserver(Subject subject){
		this.subject = subject;
		this.subject.attach(this);
	}

	@Override
	public void update() {
		System.out.println("New update available...");
		System.out.println("Updating the state of this observer...");
		System.out.println( "Hex String: " + Integer.toHexString( subject.getState() ).toUpperCase() ); 
	}
}
