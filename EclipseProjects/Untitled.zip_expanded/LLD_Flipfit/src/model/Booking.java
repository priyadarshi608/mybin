package model;

import java.util.List;

public class Booking {

	private String id;      // seatId_date
	
	private String date;
	
	private Integer workoutId;
	
	private Integer memberId;
	
	private Integer bookingStatus;
	
	private List<BookingStatusObject> bookingStatusObjectList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<BookingStatusObject> getBookingStatusObjectList() {
		return bookingStatusObjectList;
	}

	public void setBookingStatusObjectList(List<BookingStatusObject> bookingStatusObjectList) {
		this.bookingStatusObjectList = bookingStatusObjectList;
	}

	@Override
	public String toString() {
		return "Booking [id=" + id + ", date=" + date + ", bookingStatusObjectList=" + bookingStatusObjectList + "]";
	}
	
}
