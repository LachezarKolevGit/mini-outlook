package bg.sofia.uni.fkst.pik.minioutlook.exceptions;

public class FolderAlreadyExistsException extends RuntimeException{
    public FolderAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
