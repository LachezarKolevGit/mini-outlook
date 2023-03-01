package bg.sofia.uni.fkst.pik.minioutlook.exceptions;

public class FolderNotFoundException extends RuntimeException{
    public FolderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
