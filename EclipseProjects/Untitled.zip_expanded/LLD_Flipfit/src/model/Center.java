package model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class Center implements Serializable {

    private static final long serialVersionUID = -6498822736832597642L;

//    @NotNull
    private Integer id;

    private String centerName;

    private String centerEmail;

    private Integer cityId;
    
    private String centerAddress;

    private Location location;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public String getCenterEmail() {
		return centerEmail;
	}

	public void setCenterEmail(String centerEmail) {
		this.centerEmail = centerEmail;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCenterAddress() {
		return centerAddress;
	}

	public void setCenterAddress(String centerAddress) {
		this.centerAddress = centerAddress;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Center [id=" + id + ", centerName=" + centerName + ", centerEmail=" + centerEmail + ", cityId=" + cityId
				+ ", centerAddress=" + centerAddress + ", location=" + location + "]";
	}
	
}
