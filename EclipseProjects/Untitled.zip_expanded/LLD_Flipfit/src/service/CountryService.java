package service;

import java.util.List;
import dao.CountryDao;
import dao.impl.CountryDaoImpl;
import model.Country;
import reqres.BaseResponse;
import reqres.country.CreateCountryRequest;
import reqres.country.CreateCountryResponse;
import reqres.country.DeleteCountryRequest;
import reqres.country.FindCountryRequest;
import reqres.country.FindCountryResponse;
import reqres.country.UpdateCountryRequest;
import validation.ResponseCode;
import validation.WebServiceError;

public class CountryService {
	
	CountryDao countryDao = new CountryDaoImpl();

	public CreateCountryResponse create(CreateCountryRequest request) {
		CreateCountryResponse response = new CreateCountryResponse();
		List<WebServiceError> errors = request.validate();
		if (errors.size() > 0) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNPROCESSABLE_ENTITY.code());
			response.setMessage(ResponseCode.UNPROCESSABLE_ENTITY.message());
		} else {
			Country country = request.mapRequest();
			try {
				int id = countryDao.create(country);
				response.setStatus(true);
				response.setId(id);
				response.setStatusCode(ResponseCode.SUCCESSFUL_DB_OPERATION.code());
				response.setMessage(ResponseCode.SUCCESSFUL_DB_OPERATION.message());
			} catch (Exception e) {
				response.setStatus(false);
				response.setStatusCode(ResponseCode.UNSUCCESSFUL_DB_OPERATION.code());
				response.setMessage(ResponseCode.UNSUCCESSFUL_DB_OPERATION.message());
			}
		}
		response.addErrors(errors);
		return response;
	}
	
	public BaseResponse update(UpdateCountryRequest request) {
		BaseResponse response = new BaseResponse();
		List<WebServiceError> errors = request.validate();
		if (errors.size() > 0) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNPROCESSABLE_ENTITY.code());
			response.setMessage(ResponseCode.UNPROCESSABLE_ENTITY.message());
		} else {
			FindCountryRequest findCountryRequest = new FindCountryRequest();
			findCountryRequest.setId(request.getId());
			FindCountryResponse findCountryResponse = find(findCountryRequest);
			if (!findCountryResponse.isStatus()) {
				response.setStatus(false);
				response.setStatusCode(ResponseCode.DATA_NOT_FOUND_IN_DB_FOR_UPDATE.code());
				response.setMessage(ResponseCode.DATA_NOT_FOUND_IN_DB_FOR_UPDATE.message());
			} else {
				Country country = request.mapRequest();
				try {
					countryDao.create(country);
					response.setStatus(true);
					response.setStatusCode(ResponseCode.SUCCESSFUL_DB_OPERATION.code());
					response.setMessage(ResponseCode.SUCCESSFUL_DB_OPERATION.message());
				} catch (Exception e) {
					response.setStatus(false);
					response.setStatusCode(ResponseCode.UNSUCCESSFUL_DB_OPERATION.code());
					response.setMessage(ResponseCode.UNSUCCESSFUL_DB_OPERATION.message());
				}
			}
		}
		response.addErrors(errors);
		return response;
	}
	
	public FindCountryResponse find(FindCountryRequest request) {
		FindCountryResponse response = new FindCountryResponse();
		try {
			Country Country = countryDao.findById(request.getId());
			if (Country != null) {
				response.setStatus(true);
				response.setCountry(Country);
				response.setStatusCode(ResponseCode.DATA_FOUND_IN_DB.code());
				response.setMessage(ResponseCode.DATA_FOUND_IN_DB.message());
			} else {
				response.setStatus(false);
				response.setStatusCode(ResponseCode.DATA_NOT_FOUND_IN_DB.code());
				response.setMessage(ResponseCode.DATA_NOT_FOUND_IN_DB.message());
			}
		} catch (Exception e) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNSUCCESSFUL_DB_OPERATION.code());
			response.setMessage(ResponseCode.UNSUCCESSFUL_DB_OPERATION.message());
		}
		return response;
	}
	
	public BaseResponse delete(DeleteCountryRequest request) {
		BaseResponse response = new BaseResponse();
		try {
			countryDao.delete(request.getId());
			response.setStatus(true);
		} catch (Exception e) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNSUCCESSFUL_DB_OPERATION.code());
			response.setMessage(ResponseCode.UNSUCCESSFUL_DB_OPERATION.message());
		}
		return response;
	}
	
}
