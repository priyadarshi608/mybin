package sss.dpobserver_1.observer;

import sss.dpobserver_1.subject.Subject;

public abstract class Observer {
	protected Subject subject;
	public abstract void update();
}
