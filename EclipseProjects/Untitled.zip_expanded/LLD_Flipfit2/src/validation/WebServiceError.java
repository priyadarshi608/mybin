package validation;

public class WebServiceError {

    private int code;
    private String description;

    public WebServiceError() {}

    /**
     * @param description
     */
    public WebServiceError(String description) {
        this.description = description;
    }

    /**
     * @param code
     * @param message
     */
    public WebServiceError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

	@Override
	public String toString() {
		return "WebServiceError [code=" + code + ", description=" + description + "]";
	}
    
}
