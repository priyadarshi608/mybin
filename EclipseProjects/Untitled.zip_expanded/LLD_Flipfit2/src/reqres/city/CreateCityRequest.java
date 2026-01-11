package reqres.city;

import model.City;
import reqres.BaseRequest;

public class CreateCityRequest extends BaseRequest {

	private static final long serialVersionUID = -5379743763279384073L;

//	@NotNull
    private String cityName;

//	@NotNull
	private Integer countryId;

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
		return "CreateCityRequest [cityName=" + cityName + ", countryId=" + countryId + "]";
	}

	public City mapRequest() {
		City city = new City();
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
