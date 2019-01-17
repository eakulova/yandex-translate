import exception.IncorrectInputException;
import exception.TextCannotBeTranslatedException;
import exception.TranslationException;
import exception.YandexApiException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestHandler {
    private static final String URL_PART = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=";
    private final List<Long> YANDEX_API_BAD_CODES = new ArrayList<>(Arrays.asList(401L, 402L, 404L, 501L));
    private final List<Long> INCORRECT_INPUT_CODES = new ArrayList<>(Arrays.asList(413L, 422L));

    public String translate(String input) {
        HttpsURLConnection connection = sendRequest(input);
        String translation = handleResponse(connection);
        if (input.equals(translation)) {
            throw new TextCannotBeTranslatedException();
        }
        return translation;
    }

    private HttpsURLConnection sendRequest(String word) {
        ConfigParser config = ConfigParser.getConfigParser();
        String urlStr = URL_PART + config.getKey();

        try {
            URL url = new URL(urlStr);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            String urlParameters = "lang=" + config.getDestinationLang() + "&text=" + word;

            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            return connection;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    private String handleResponse(HttpsURLConnection connection) throws TranslationException {
        try {
            int responseCode = connection.getResponseCode();
            String response = readResponse(connection);
            if (responseCode == 200) {
                return extractTranslationFromResponse(response);
            } else {
                throw getTranslationException(response, responseCode);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    private TranslationException getTranslationException(String response, int responseCode)
            throws TranslationException {
        long responseCodeFromJson = extractReasonCodeFromResponse(response);
        if (INCORRECT_INPUT_CODES.contains(responseCodeFromJson)) {
            throw new IncorrectInputException("Exception while translation: "
                    + extractExceptionMessageFromResponse(response));
        }
        if (YANDEX_API_BAD_CODES.contains(responseCodeFromJson)) {
            throw new YandexApiException("Exception while handling request: "
                    + extractExceptionMessageFromResponse(response));
        } else {
            throw new YandexApiException("Exception while handling request. Response code = " + responseCode);
        }
    }

    private String extractTranslationFromResponse(String response) {
        String result = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response);
            JSONArray array = (JSONArray) jsonObject.get("text");
            return array.get(0).toString();
        } catch (ParseException ex) {
            ex.printStackTrace();
            System.out.println("Exception while parsing response from server. Please try again later.");
        }
        return result;
    }

    private long extractReasonCodeFromResponse(String response) {
        long reasonCode;
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response);
            reasonCode = (long) jsonObject.get("code");
        } catch (ParseException ex) {
            reasonCode = 0;
        }
        return reasonCode;
    }

    private String extractExceptionMessageFromResponse(String response) {
        String exceptionMessage = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response);
            exceptionMessage = jsonObject.get("message").toString();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return exceptionMessage;
    }

    private String readResponse(HttpsURLConnection connection) {
        try {
            BufferedReader reader = getReader(connection);
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            return response.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    private BufferedReader getReader(HttpsURLConnection connection) {
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ex) {
            inputStream = connection.getErrorStream();
        }
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
