package exception;

public class EmptyKeyException extends RuntimeException {

    public EmptyKeyException(){
        super("");
    }
}
