package model;

import java.util.List;

// Its better to store this table in NoSQL DB like mongoDB (which provides document
// level transaction).

// https://stackoverflow.com/questions/11076272/its-not-possible-to-lock-a-mongodb-document-what-if-i-need-to

// With MongoDB 3.2.2 using WiredTiger Storage implementation as default engine,
// MongoDB use default locking at document level.It was introduced in version 
// 3.0 but made default in version 3.2.2. Therefore MongoDB now has document 
// level locking.

public class Seat {

	private Integer id;
	// Seat Number can also be the combination of workoutId_seatNumber.
	
	private Integer workoutId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getWorkoutId() {
		return workoutId;
	}

	public void setWorkoutId(Integer workoutId) {
		this.workoutId = workoutId;
	}

	@Override
	public String toString() {
		return "Seat [id=" + id + ", workoutId=" + workoutId + "]";
	}
	
}
