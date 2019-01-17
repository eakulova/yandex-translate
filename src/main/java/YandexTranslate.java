import exception.IncorrectInputException;
import exception.TextCannotBeTranslatedException;
import exception.YandexApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YandexTranslate {
    private static final String STOP_WORD = "q";

    public static void main(String[] args) {

        RequestHandler requestHandler = new RequestHandler();
        while (true) {
            System.out.println("Please type a word or phrase to translate or type 'q' to exit:");
            String input = readInput();
            if (STOP_WORD.equals(input)) {
                System.out.println("the program will be terminated.");
                System.exit(0);
            }
            try {
                String result = requestHandler.translate(input);
                System.out.println(result);
            } catch (IncorrectInputException | TextCannotBeTranslatedException ex) {
                System.out.println(ex.getMessage());
            } catch (YandexApiException apiEx) {
                System.out.println(apiEx.getMessage());
                System.out.println("The program will be terminated.");
                System.exit(1);
            }
        }
    }

    private static String readInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }
}
