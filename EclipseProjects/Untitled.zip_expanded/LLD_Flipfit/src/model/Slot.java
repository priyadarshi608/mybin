package model;

import java.io.Serializable;

public class Slot implements Serializable {

	private static final long serialVersionUID = 8493570835894373149L;

	private Integer id;
	
	private Integer centerId;
	
	private WeekDay weekDay;

	private String startTime;
	
	private String endTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCenterId() {
		return centerId;
	}

	public void setCenterId(Integer centerId) {
		this.centerId = centerId;
	}

	public WeekDay getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(WeekDay weekDay) {
		this.weekDay = weekDay;
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
		return "Slot [id=" + id + ", centerId=" + centerId + ", weekDay=" + weekDay + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}
	
}
