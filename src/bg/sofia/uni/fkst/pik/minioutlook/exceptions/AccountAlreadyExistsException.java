package bg.sofia.uni.fkst.pik.minioutlook.exceptions;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
