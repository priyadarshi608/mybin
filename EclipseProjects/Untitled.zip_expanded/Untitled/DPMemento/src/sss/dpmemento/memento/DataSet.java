package sss.dpmemento.memento;

public class DataSet implements Cloneable {
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

	@Override
	public DataSet clone() {
		return new DataSet(this.id, this.str);
	}
	
	public String toString() {
		return "id: " + id + ", str: " + str;
	}
}
