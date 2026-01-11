package reqres.country;

import java.util.ArrayList;
import java.util.List;
import model.Center;
import model.Country;
import model.Location;
import reqres.BaseRequest;
import validation.ResponseCode;
import validation.WebServiceError;

public class UpdateCountryRequest extends BaseRequest {

	private static final long serialVersionUID = -5379743763279384073L;

//	@NotNull
    private int id;

//	@NotNull
    private String countryName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	@Override
	public String toString() {
		return "UpdateCountryRequest [id=" + id + ", countryName=" + countryName + "]";
	}

	public Country mapRequest() {
		Country country = new Country();
		country.setId(id);
		country.setCountryName(countryName);
		return country;
	}
	
//	protected List<WebServiceError> businessValidations() {
//		List<WebServiceError> errors = new ArrayList<>();
//		return errors;
//	}
//	
}
