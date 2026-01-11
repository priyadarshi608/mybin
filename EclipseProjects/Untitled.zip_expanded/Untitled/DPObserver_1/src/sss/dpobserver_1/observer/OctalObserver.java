package sss.dpobserver_1.observer;

import sss.dpobserver_1.subject.Subject;

public class OctalObserver extends Observer{

	public OctalObserver(Subject subject){
		this.subject = subject;
		this.subject.attach(this);
	}

	@Override
	public void update() {
		System.out.println("New update available...");
		System.out.println("Ignoring the update.");
		// System.out.println( "Octal String: " + Integer.toOctalString( subject.getState() ) ); 
	}
}
