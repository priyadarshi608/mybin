package controller;

import reqres.BaseResponse;
import reqres.city.CreateCityRequest;
import reqres.city.CreateCityResponse;
import reqres.city.DeleteCityRequest;
import reqres.city.FindCityRequest;
import reqres.city.FindCityResponse;
import reqres.city.UpdateCityRequest;
import service.CityService;

public class CityController {
	CityService CityService = new CityService();
	
	public CreateCityResponse create(CreateCityRequest request) {
		return CityService.create(request);
	}
	
	public BaseResponse update(UpdateCityRequest request) {
		return CityService.update(request);
	}
	
	public FindCityResponse find(FindCityRequest request) {
		return CityService.find(request);
	}
	
	public BaseResponse delete(DeleteCityRequest request) {
		return CityService.delete(request);
	}
}
