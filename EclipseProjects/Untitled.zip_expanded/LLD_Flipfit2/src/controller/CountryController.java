package controller;

import reqres.BaseResponse;
import reqres.country.CreateCountryRequest;
import reqres.country.CreateCountryResponse;
import reqres.country.DeleteCountryRequest;
import reqres.country.FindCountryRequest;
import reqres.country.FindCountryResponse;
import reqres.country.UpdateCountryRequest;
import service.CountryService;

public class CountryController {
	CountryService CountryService = new CountryService();
	
	public CreateCountryResponse create(CreateCountryRequest request) {
		return CountryService.create(request);
	}
	
	public BaseResponse update(UpdateCountryRequest request) {
		return CountryService.update(request);
	}
	
	public FindCountryResponse find(FindCountryRequest request) {
		return CountryService.find(request);
	}
	
	public BaseResponse delete(DeleteCountryRequest request) {
		return CountryService.delete(request);
	}
}
