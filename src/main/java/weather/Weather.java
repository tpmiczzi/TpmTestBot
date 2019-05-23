package weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class Weather {

    private static final Logger LOG = Logger.getLogger(Weather.class.getName());

    public static String getWeather(String message, ModelAnswer model) throws IOException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="
                                  + message
                                  + "&units=metric&APPID=6fff53a641b9b9a799cfd6b079f5cd4e");

        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";

        while (scanner.hasNext()) {
            result += scanner.next();
        }

        JSONObject jsonObject = new JSONObject(result);
        model.setName(jsonObject.getString("name"));
        model.setTemp(Double.valueOf(jsonObject.getJSONObject("main").get("temp").toString()));
        model.setHumidity(Double.valueOf(jsonObject.getJSONObject("main").get("humidity").toString()));

        JSONArray jsonArray = jsonObject.getJSONArray("weather");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            model.setIcon(obj.getString("icon"));
            model.setMain(obj.getString("main"));
        }

        LOG.info("Answer - " + model.toString());

        return model.toString();
    }
}
