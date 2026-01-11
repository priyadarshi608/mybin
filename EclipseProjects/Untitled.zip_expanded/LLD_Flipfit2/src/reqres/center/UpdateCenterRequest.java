package reqres.center;

import java.util.ArrayList;
import java.util.List;
import model.Center;
import model.Location;
import reqres.BaseRequest;
import validation.ResponseCode;
import validation.WebServiceError;

public class UpdateCenterRequest extends BaseRequest {

	private static final long serialVersionUID = -5379743763279384073L;

//	@NotNull
    private int id;

//	@NotNull
    private String centerName;

//	@NotNull
    private String centerEmail;

//	@NotNull
    private Integer cityId;
    
//	@NotNull
    private String centerAddress;

//	@NotNull
    private Location location;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public String getCenterEmail() {
		return centerEmail;
	}

	public void setCenterEmail(String centerEmail) {
		this.centerEmail = centerEmail;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCenterAddress() {
		return centerAddress;
	}

	public void setCenterAddress(String centerAddress) {
		this.centerAddress = centerAddress;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	@Override
	public String toString() {
		return "UpdateCenterRequest [id=" + id + ", centerName=" + centerName + ", centerEmail=" + centerEmail
				+ ", cityId=" + cityId + ", centerAddress=" + centerAddress + ", location=" + location + "]";
	}

	public Center mapRequest() {
		Center center = new Center();
		center.setId(id);
		center.setCenterName(this.centerName);
		center.setCenterEmail(this.centerEmail);
		center.setCityId(this.cityId);
		center.setCenterAddress(this.centerAddress);
		center.setLocation(this.location);
		return center;
	}
	
	protected List<WebServiceError> businessValidations() {
		List<WebServiceError> errors = new ArrayList<>();
		Double latitude = this.getLocation().getLatitude();
		Double longitude = this.getLocation().getLongitude();
		if (latitude == null || latitude < -180 || latitude > 180) {
			errors.add(new WebServiceError(
					ResponseCode.UNPROCESSABLE_ENTITY.code(), 
					"Invalid center's location's latitude."));
		}
		if (longitude == null || longitude < -180 || longitude > 180) {
			errors.add(new WebServiceError(
					ResponseCode.UNPROCESSABLE_ENTITY.code(), 
					"Invalid center's location's longitude."));
		}
		return errors;
	}
	
}
