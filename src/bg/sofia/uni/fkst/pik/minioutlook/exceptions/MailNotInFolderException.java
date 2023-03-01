package bg.sofia.uni.fkst.pik.minioutlook.exceptions;

public class MailNotInFolderException extends  RuntimeException{
    public MailNotInFolderException(String message, Throwable cause){
        super(message, cause);
    }
}
