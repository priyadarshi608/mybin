package controller;

import reqres.BaseResponse;
import reqres.center.CreateCenterRequest;
import reqres.center.CreateCenterResponse;
import reqres.center.DeleteCenterRequest;
import reqres.center.FindCenterRequest;
import reqres.center.FindCenterResponse;
import reqres.center.UpdateCenterRequest;
import service.CenterService;

public class CenterController {
	CenterService centerService = new CenterService();
	
	public CreateCenterResponse create(CreateCenterRequest request) {
		return centerService.create(request);
	}
	
	public BaseResponse update(UpdateCenterRequest request) {
		return centerService.update(request);
	}
	
	public FindCenterResponse find(FindCenterRequest request) {
		return centerService.find(request);
	}
	
	public BaseResponse delete(DeleteCenterRequest request) {
		return centerService.delete(request);
	}
}
