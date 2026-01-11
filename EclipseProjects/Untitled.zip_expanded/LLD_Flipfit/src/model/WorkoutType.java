package model;

public class WorkoutType {

	public Integer id;
	
	public String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "WorkoutType [id=" + id + ", name=" + name + "]";
	}
	
}
