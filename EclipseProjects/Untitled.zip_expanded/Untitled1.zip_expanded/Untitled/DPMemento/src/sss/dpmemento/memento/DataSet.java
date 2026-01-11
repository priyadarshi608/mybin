package sss.dpmemento.memento;

public class DataSet {
	private int id;
	private String str;
	
	public DataSet(int id, String str) {
		super();
		this.id = id;
		this.str = str;
	}

	public int getId() {
		return id;
	}
	
	public String getStr() {
		return str;
	}
	
	public void setStr(String str) {
		this.str = str;
	}
}
