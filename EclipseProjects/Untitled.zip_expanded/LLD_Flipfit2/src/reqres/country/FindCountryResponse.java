package reqres.country;

import model.Country;
import reqres.BaseResponse;

public class FindCountryResponse extends BaseResponse {

	private static final long serialVersionUID = -5379743763279384073L;

	private Country country;

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "FindCountryResponse [country=" + country + "]";
	}
	
}
