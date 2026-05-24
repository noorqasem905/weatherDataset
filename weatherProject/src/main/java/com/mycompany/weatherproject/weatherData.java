
package com.mycompany.weatherproject;

/**
 *
 * @author Zaid Alqudsi 202120148
 * 
 */
public class weatherData {
    private String formattedDate;
    private String summary;
    private String precipType;
    private double temperature;
    private double apparentTemperature;
    private double humidity;
    private double windSpeed;

    public weatherData(String formattedDate, String summary, String precipType, 
                       double temperature, double apparentTemperature, double humidity, double windSpeed) {
        this.formattedDate = formattedDate;
        this.summary = summary;
        this.precipType = precipType;
        this.temperature = temperature;
        this.apparentTemperature = apparentTemperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public String getFormattedDate() { return formattedDate; }
    public void setFormattedDate(String formattedDate) { this.formattedDate = formattedDate; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getPrecipType() { return precipType; }
    public void setPrecipType(String precipType) { this.precipType = precipType; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getApparentTemperature() { return apparentTemperature; }
    public void setApparentTemperature(double apparentTemperature) { this.apparentTemperature = apparentTemperature; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
}
