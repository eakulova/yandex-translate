package exception;

public class TextCannotBeTranslatedException extends TranslationException {
    public TextCannotBeTranslatedException() {
        super("The text cannot be translated!");
    }
}
