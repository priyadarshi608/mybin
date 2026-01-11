package reqres.city;

import reqres.BaseRequest;

public class DeleteCityRequest extends BaseRequest {

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
		return "DeleteCountryRequest [id=" + id + "]";
	}
	
}
