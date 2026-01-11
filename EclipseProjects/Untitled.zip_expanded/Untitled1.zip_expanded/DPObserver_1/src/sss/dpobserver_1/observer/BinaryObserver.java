package sss.dpobserver_1.observer;

import sss.dpobserver_1.subject.Subject;

public class BinaryObserver extends Observer{

	public BinaryObserver(Subject subject){
		this.subject = subject;
		this.subject.attach(this);
	}

	@Override
	public void update() {
		System.out.println("New update available...");
		System.out.println("Updating the state of this observer...");
		System.out.println( "Binary String: " + Integer.toBinaryString( subject.getState() ) ); 
	}
}
