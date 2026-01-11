package reqres.center;

import reqres.BaseResponse;

public class CreateCenterResponse extends BaseResponse {
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "CreateCenterResponse [id=" + id + "]";
	}
	
}
