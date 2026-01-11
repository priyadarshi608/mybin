package reqres.city;

import model.City;
import reqres.BaseResponse;

public class FindCityResponse extends BaseResponse {

	private static final long serialVersionUID = -5379743763279384073L;

	private City city;

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "FindCityResponse [city=" + city + "]";
	}
	
}
