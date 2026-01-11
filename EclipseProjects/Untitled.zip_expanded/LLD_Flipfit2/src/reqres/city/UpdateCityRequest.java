package reqres.city;

import model.City;
import reqres.BaseRequest;

public class UpdateCityRequest extends BaseRequest {

	private static final long serialVersionUID = -5379743763279384073L;

//	@NotNull
    private int id;

//	@NotNull
    private String cityName;

//	@NotNull
	private Integer countryId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public City mapRequest() {
		City city = new City();
		city.setId(id);
		city.setCityName(cityName);
		city.setCountryId(countryId);
		return city;
	}
	
//	protected List<WebServiceError> businessValidations() {
//		List<WebServiceError> errors = new ArrayList<>();
//		return errors;
//	}
//	
}
