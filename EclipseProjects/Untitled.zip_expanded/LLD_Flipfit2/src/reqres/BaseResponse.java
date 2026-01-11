package reqres;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import validation.WebServiceError;

public class BaseResponse implements Serializable {
	
	private static final long serialVersionUID = -7413795735883986236L;
	
    private boolean                 status;
    private Integer                 statusCode;
    private String                  message;
    private List<WebServiceError>   errors;

    public BaseResponse() {}

	public BaseResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

	/**
     * @return the successful
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * @param status the successful to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	/**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public void setErrors(List<WebServiceError> errors) {
        this.errors = errors;
    }

    public List<WebServiceError> getErrors() {
        return errors;
    }

    public BaseResponse addError(WebServiceError error) {
        if (this.errors == null) {
            this.errors = new ArrayList<WebServiceError>();
        }
        this.errors.add(error);
        return this;
    }

    public BaseResponse addErrors(List<WebServiceError> errors) {
        if (errors != null) {
            if (this.errors == null) {
                this.errors = new ArrayList<WebServiceError>();
            }
            this.errors.addAll(errors);
        }
        return this;
    }

    public boolean hasErrors() {
        return errors != null && errors.size() > 0;
    }

    public void setResponse(BaseResponse response) {
        this.status = response.isStatus();
        this.message = response.getMessage();
        this.errors = response.getErrors();
    }

	@Override
	public String toString() {
		return "BaseResponse [status=" + status + ", statusCode=" + statusCode + ", message=" + message + ", errors="
				+ errors + "]";
	}
    
}
