package reqres.city;

import java.util.ArrayList;
import java.util.List;
import model.Center;
import model.Location;
import reqres.BaseRequest;
import validation.ResponseCode;
import validation.WebServiceError;

public class FindCityRequest extends BaseRequest {

	private static final long serialVersionUID = -5379743763279384073L;

//	@NotNull
    private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "FindCountryRequest [id=" + id + "]";
	}
	
}
