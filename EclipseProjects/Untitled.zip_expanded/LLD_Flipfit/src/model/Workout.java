package model;

public class Workout {

	// @NotNull
	private Integer id;
	
	// @NotNull
	private Integer slotId;

	// @NotNull
	private Integer workoutId;

	private int numberOfSeats;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSlotId() {
		return slotId;
	}

	public void setSlotId(Integer slotId) {
		this.slotId = slotId;
	}

	public Integer getWorkoutId() {
		return workoutId;
	}

	public void setWorkoutId(Integer workoutId) {
		this.workoutId = workoutId;
	}

	public int getNumberOfSeats() {
		return numberOfSeats;
	}

	public void setNumberOfSeats(int numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}

	@Override
	public String toString() {
		return "Workout [id=" + id + ", slotId=" + slotId + ", workoutId=" + workoutId + ", numberOfSeats="
				+ numberOfSeats + "]";
	}

}
