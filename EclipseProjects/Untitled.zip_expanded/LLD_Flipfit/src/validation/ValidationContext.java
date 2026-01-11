package validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationContext {

    private final List<WebServiceError> errors   = new ArrayList<WebServiceError>();

    public void addError(ResponseCode responseCode) {
        this.errors.add(new WebServiceError(responseCode.code(), responseCode.message()));
    }

    public void addErrors(List<WebServiceError> errors) {
        this.errors.addAll(errors);
    }

    public void addError(ResponseCode responseCode, String description) {
        this.errors.add(new WebServiceError(responseCode.code(), description));
    }

    public List<WebServiceError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

}
