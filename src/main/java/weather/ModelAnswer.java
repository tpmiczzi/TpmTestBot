package weather;

import lombok.Data;

@Data
public class ModelAnswer {
    private String name;
    private Double temp;
    private Double humidity;
    private String icon;
    private String main;

    @Override
    public String toString() {
        return "City = " + name + '\n' +
                "temp = " + temp + "C" + '\n' +
                "humidity = " + humidity + "%" + '\n' +
                "main = " + main + '\n' +
                "http://openweathermap.org/img/w/" + icon + ".png";
    }
}
