package org.example;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Main {
    @JsonProperty("temp")
    private double temp;

    @JsonProperty("feels_like")
    private double feelsLike;

    @JsonProperty("temp_min")
    private double tempMin;

    @JsonProperty("temp_max")
    private double tempMax;

    @JsonProperty("pressure")
    private int pressure;

    @JsonProperty("humidity")
    private int humidity;

    @JsonProperty("sea_level")
    private int seaLevel;

    @JsonProperty("grnd_level")
    private int grndLevel;

    public double getTempFahrenheit() {
        return (temp * 9.0 / 5.0) + 32;
    }

    public double getFeelsLikeFahrenheit() {
        return (feelsLike * 9.0 / 5.0) + 32;
    }

    public double getTempMinFahrenheit() {
        return (tempMin * 9.0 / 5.0) + 32;
    }

    public double getTempMaxFahrenheit() {
        return (tempMax * 9.0 / 5.0) + 32;
    }

    @Override
    public String toString() {
        return "Main{" +
                "temp=" + temp +
                ", feelsLike=" + feelsLike +
                ", tempMin=" + tempMin +
                ", tempMax=" + tempMax +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", seaLevel=" + seaLevel +
                ", grndLevel=" + grndLevel +
                '}';
    }
}
