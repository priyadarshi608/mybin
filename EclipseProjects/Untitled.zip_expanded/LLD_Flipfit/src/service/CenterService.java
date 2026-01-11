package service;

import java.util.List;
import dao.CenterDao;
import dao.impl.CenterDaoImpl;
import model.Center;
import reqres.BaseResponse;
import reqres.center.CreateCenterRequest;
import reqres.center.CreateCenterResponse;
import reqres.center.DeleteCenterRequest;
import reqres.center.FindCenterRequest;
import reqres.center.FindCenterResponse;
import reqres.center.UpdateCenterRequest;
import validation.ResponseCode;
import validation.WebServiceError;

public class CenterService {
	
	CenterDao centerDao = new CenterDaoImpl();

	public CreateCenterResponse create(CreateCenterRequest request) {
		CreateCenterResponse response = new CreateCenterResponse();
		List<WebServiceError> errors = request.validate();
		if (errors.size() > 0) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNPROCESSABLE_ENTITY.code());
			response.setMessage(ResponseCode.UNPROCESSABLE_ENTITY.message());
		} else {
			Center center = request.mapRequest();
			try {
				int id = centerDao.create(center);
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
	
	public BaseResponse update(UpdateCenterRequest request) {
		BaseResponse response = new BaseResponse();
		List<WebServiceError> errors = request.validate();
		if (errors.size() > 0) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNPROCESSABLE_ENTITY.code());
			response.setMessage(ResponseCode.UNPROCESSABLE_ENTITY.message());
		} else {
			FindCenterRequest findCenterRequest = new FindCenterRequest();
			findCenterRequest.setId(request.getId());
			FindCenterResponse findCenterResponse = find(findCenterRequest);
			if (!findCenterResponse.isStatus()) {
				response.setStatus(false);
				response.setStatusCode(ResponseCode.DATA_NOT_FOUND_IN_DB_FOR_UPDATE.code());
				response.setMessage(ResponseCode.DATA_NOT_FOUND_IN_DB_FOR_UPDATE.message());
			} else {
				Center center = request.mapRequest();
				try {
					centerDao.create(center);
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
	
	public FindCenterResponse find(FindCenterRequest request) {
		FindCenterResponse response = new FindCenterResponse();
		try {
			Center center = centerDao.findById(request.getId());
			if (center != null) {
				response.setStatus(true);
				response.setCenter(center);
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
	
	public BaseResponse delete(DeleteCenterRequest request) {
		BaseResponse response = new BaseResponse();
		try {
			centerDao.delete(request.getId());
			response.setStatus(true);
		} catch (Exception e) {
			response.setStatus(false);
			response.setStatusCode(ResponseCode.UNSUCCESSFUL_DB_OPERATION.code());
			response.setMessage(ResponseCode.UNSUCCESSFUL_DB_OPERATION.message());
		}
		return response;
	}
	
}
