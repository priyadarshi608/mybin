package service;

import java.util.List;
import dao.CityDao;
import dao.impl.CityDaoImpl;
import model.City;
import reqres.BaseResponse;
import reqres.city.CreateCityRequest;
import reqres.city.CreateCityResponse;
import reqres.city.DeleteCityRequest;
import reqres.city.FindCityRequest;
import reqres.city.FindCityResponse;
import reqres.city.UpdateCityRequest;
import validation.ResponseCode;
import validation.WebServiceError;

public class CityService {
	
	CityDao cityDao = new CityDaoImpl();

	public CreateCityResponse create(CreateCityRequest request) {
		CreateCityResponse response = new CreateCityResponse();
		List<WebServiceError> errors = request.validate();
		if (errors.size() > 0) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNPROCESSABLE_ENTITY.code());
			response.setMessage(ResponseCode.UNPROCESSABLE_ENTITY.message());
		} else {
			City city = request.mapRequest();
			try {
				int id = cityDao.create(city);
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
	
	public BaseResponse update(UpdateCityRequest request) {
		BaseResponse response = new BaseResponse();
		List<WebServiceError> errors = request.validate();
		if (errors.size() > 0) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNPROCESSABLE_ENTITY.code());
			response.setMessage(ResponseCode.UNPROCESSABLE_ENTITY.message());
		} else {
			FindCityRequest findCityRequest = new FindCityRequest();
			findCityRequest.setId(request.getId());
			FindCityResponse findCityResponse = find(findCityRequest);
			if (!findCityResponse.isStatus()) {
				response.setStatus(false);
				response.setStatusCode(ResponseCode.DATA_NOT_FOUND_IN_DB_FOR_UPDATE.code());
				response.setMessage(ResponseCode.DATA_NOT_FOUND_IN_DB_FOR_UPDATE.message());
			} else {
				City city = request.mapRequest();
				try {
					cityDao.create(city);
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
	
	public FindCityResponse find(FindCityRequest request) {
		FindCityResponse response = new FindCityResponse();
		try {
			City City = cityDao.findById(request.getId());
			if (City != null) {
				response.setStatus(true);
				response.setCity(City);
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
	
	public BaseResponse delete(DeleteCityRequest request) {
		BaseResponse response = new BaseResponse();
		try {
			cityDao.delete(request.getId());
			response.setStatus(true);
		} catch (Exception e) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNSUCCESSFUL_DB_OPERATION.code());
			response.setMessage(ResponseCode.UNSUCCESSFUL_DB_OPERATION.message());
		}
		return response;
	}
	
}
