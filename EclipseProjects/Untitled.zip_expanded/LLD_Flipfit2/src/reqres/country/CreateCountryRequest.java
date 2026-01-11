package reqres.country;

import model.Country;
import reqres.BaseRequest;

public class CreateCountryRequest extends BaseRequest {

	private static final long serialVersionUID = -5379743763279384073L;

//	@NotNull
    private String countryName;

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	@Override
	public String toString() {
		return "CreateCountryRequest [countryName=" + countryName + "]";
	}

	public Country mapRequest() {
		Country country = new Country();
		country.setCountryName(countryName);
		return country;
	}
	
//	protected List<WebServiceError> businessValidations() {
//		List<WebServiceError> errors = new ArrayList<>();
//		return errors;
//	}
//	
}
