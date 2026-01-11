package object.workout;

import object.gymfacility.GymFacility;

public abstract class Workout {

	private GymFacility requiredGymFacility;

	public GymFacility getRequiredGymFacility() {
		return requiredGymFacility;
	}

	public abstract void setRequiredGymFacility(GymFacility requiredGymFacility);

}
