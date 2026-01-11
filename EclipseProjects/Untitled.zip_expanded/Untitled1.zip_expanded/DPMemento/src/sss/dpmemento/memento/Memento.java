package sss.dpmemento.memento;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
public class Memento<T> {
	private static final TreeMap<Integer, Object>  MY_TREE_MAP = 
			new TreeMap<Integer, Object>();

	private static DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

	private static int stateId;

	T t;
	String dateTime;

	public void saveStateToMemento(T t) {
		Date dateobj = new Date();
		this.dateTime = df.format(dateobj);
		this.t = t;
		MY_TREE_MAP.put(stateId, this);
		stateId++;
	}

	@SuppressWarnings("unchecked")
	public T getStateFromMemento(int stateId) {
		Memento<T> memento = null;
		memento = (Memento<T>) MY_TREE_MAP.get(stateId - 1);
		return (T) memento.t;
	}

	public void showAllStates() {
		System.out.println("The time-wise saved states are:");
		for(Map.Entry<Integer, Object> entry : MY_TREE_MAP.entrySet()) {
			int key = entry.getKey();
			@SuppressWarnings("unchecked")
			String timeStamp = ((Memento<T>) entry.getValue()).dateTime;
			System.out.println("State_ID: " + (key + 1) + ",   timeStamp: " + timeStamp);
		}
	}
}
