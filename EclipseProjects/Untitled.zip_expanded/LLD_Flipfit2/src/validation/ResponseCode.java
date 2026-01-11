package validation;

public class ResponseCode {

    public static final ResponseCode SUCCESSFUL_DB_OPERATION = new ResponseCode(2001, "SUCCESSFUL DB OPERTION");
    public static final ResponseCode DATA_FOUND_IN_DB = new ResponseCode(2002, "DATA FOUND IN DB");
    public static final ResponseCode DATA_FOUND_IN_DB_FOR_UPDATE = new ResponseCode(2003, "DATA FOUND IN DB FOR UPDATE");

    public static final ResponseCode UNSUCCESSFUL_DB_OPERATION = new ResponseCode(1001, "UNSUCCESSFUL DB OPERATION");
    public static final ResponseCode DATA_NOT_FOUND_IN_DB = new ResponseCode(1002, "DATA NOT FOUND IN DB");
    public static final ResponseCode DATA_NOT_FOUND_IN_DB_FOR_UPDATE = new ResponseCode(1003, "DATA NOT FOUND IN DB FOR UPDATE");
    public static final ResponseCode UNPROCESSABLE_ENTITY = new ResponseCode(1004, "UNPROCESSABLE ENTITY");

    public static final ResponseCode MISSING_REQUIRED_PARAMETERS = new ResponseCode(1051, "MISSING_REQUIRED_PARAMETERS");
    
    private final int                code;
    private final String             message;

    public ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
