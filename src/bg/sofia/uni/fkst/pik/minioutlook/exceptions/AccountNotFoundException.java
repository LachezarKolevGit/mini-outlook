package bg.sofia.uni.fkst.pik.minioutlook.exceptions;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
