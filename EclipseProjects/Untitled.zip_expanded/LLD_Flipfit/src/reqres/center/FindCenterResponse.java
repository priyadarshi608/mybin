package reqres.center;

import java.util.ArrayList;
import java.util.List;
import model.Center;
import model.Location;
import reqres.BaseRequest;
import reqres.BaseResponse;
import validation.ResponseCode;
import validation.WebServiceError;

public class FindCenterResponse extends BaseResponse {

	private static final long serialVersionUID = -5379743763279384073L;

	private Center center;

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	@Override
	public String toString() {
		return "FindCenterResponse [center=" + center + "]";
	}
	
}
