package sss.vending_machine_1.exceptions;

public class SoldOutException extends RuntimeException {
    private String message;
   
    public SoldOutException(String string) {
        this.message = string;
    }
   
    @Override
    public String getMessage(){
        return message;
    }
   
}
