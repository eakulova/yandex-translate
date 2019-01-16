package exception;

public class EmptyKeyException extends RuntimeException {

    public EmptyKeyException(){
        super("Yandex key is empty! The program will be terminated.");
    }
}
