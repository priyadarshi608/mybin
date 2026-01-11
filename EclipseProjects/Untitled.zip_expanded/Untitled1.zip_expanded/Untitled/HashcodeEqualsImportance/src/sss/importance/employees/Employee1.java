package sss.importance.employees;

public class Employee1
{
	private final Integer id;
	private String name;

	public Employee1(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
