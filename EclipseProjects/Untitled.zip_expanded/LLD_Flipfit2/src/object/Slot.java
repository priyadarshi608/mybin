package object;

public class Slot {
	
	private String startTime;
	
	private String endTime;

	public Slot(String startTime, String endTime) {
		if (!validateTimeFormat(startTime)) {
			throw new RuntimeException("Format exception for the startTime. Please provide time in HH:MM format with 24 hours measurement.");
		}
		if (!validateTimeFormat(endTime)) {
			throw new RuntimeException("Format exception for the endTime. Please provide time in HH:MM format with 24 hours measurement.");
		}
		if (!validateDuration(startTime, endTime)) {
			throw new RuntimeException("SlotDurationException. Duration of the slot should be 1 hour.");
		}
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	private boolean validateTimeFormat(String time) {
		return true;
	}
	
	private boolean validateDuration(String startTime, String endTime) {
		return true;
	}
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "Slot [startTime=" + startTime + ", endTime=" + endTime + "]";
	}
	
}
