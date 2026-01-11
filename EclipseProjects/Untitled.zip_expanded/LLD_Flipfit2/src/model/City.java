package model;

import java.io.Serializable;

public class City implements Serializable {

	private static final long serialVersionUID = 8493570835894373149L;

	private Integer id;
	
	private String cityName;
	
	private Integer countryId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", cityName=" + cityName + ", countryId=" + countryId + "]";
	}
	
}
