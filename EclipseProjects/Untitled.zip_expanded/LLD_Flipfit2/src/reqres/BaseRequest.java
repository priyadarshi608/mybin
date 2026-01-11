package reqres;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import validation.ResponseCode;
import validation.ValidationContext;
import validation.WebServiceError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaseRequest implements Serializable {

    private static final long serialVersionUID = 5395098634616029885L;

    protected List<WebServiceError> businessValidations() {
		List<WebServiceError> errors = new ArrayList<WebServiceError>();
		return errors;
    }
    
    public List<WebServiceError> validate() {
//		List<WebServiceError> errors = validate(true);
		List<WebServiceError> errors = new ArrayList<WebServiceError>();
		if (errors.size() > 0) {
			return errors;
		} else {
			return businessValidations();
		}
    }

    public List<WebServiceError> validate(boolean validateOptionalBlanks) {
        ValidationContext context = new ValidationContext();
        validate(this, context, validateOptionalBlanks);
        return context.getErrors();
    }

    public static <T> List<WebServiceError> validate(T input, ValidationContext context, boolean validateOptionalBlanks) {

        Set<ConstraintViolation<T>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(input);

        if (violations.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                builder.append(violation.getPropertyPath()).append(' ').append(violation.getMessage()).append('|');
            }
            if (builder.length() > 0) {
                context.addError(ResponseCode.MISSING_REQUIRED_PARAMETERS, builder.deleteCharAt(builder.length() - 1).toString());
            }
        }
        return context.getErrors();
    }

}
