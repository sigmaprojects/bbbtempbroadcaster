/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bbbtempbroadcaster;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.Map;
import bbbtempbroadcaster.ReadPropertiesFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author don
 */
public class BBBTempBroadcaster {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        Map<String, String> props = ReadPropertiesFile.read();

        // Create I2C bus
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        // Get I2C device, SI7021 I2C address is 0x40(64)
        I2CDevice device = bus.getDevice(0x40);

        // Send humidity measurement command
        device.write((byte) 0xF5);
        Thread.sleep(300);

        // Read 2 bytes of humidity data, msb first
        byte[] data = new byte[2];
        device.read(data, 0, 2);

        // Convert humidity data
        double humidity = (((((data[0] & 0xFF) * 256) + (data[1] & 0xFF)) * 125.0) / 65536.0) - 6;

        // Send temperature measurement command
        device.write((byte) 0xF3);
        Thread.sleep(300);

        // Read 2 bytes of temperature data, msb first
        device.read(data, 0, 2);

        // Convert temperature data
        double cTemp = (((((data[0] & 0xFF) * 256) + (data[1] & 0xFF)) * 175.72) / 65536.0) - 46.85;
        double fTemp = (cTemp * 1.8) + 32;

        // Output data to screen
        System.out.printf("Relative Humidity : %.2f %% %n", humidity);
        System.out.printf("Temperature in Celsius : %.2f C%n", cTemp);
        System.out.printf("Temperature in Fahrenheit : %.2f F%n", fTemp);
        sendData(
                props.get("url"),
                cTemp,
                humidity
        );
    }

    private static void sendData(
            String sURL,
            double cTemp,
            double humidity
    ) {
        String fullStringURL = sURL + "&temp=" + String.valueOf(cTemp) + "&humidity=" + String.valueOf(humidity);
        StringBuilder result = new StringBuilder();
        System.out.printf("Sending data using URL: " + fullStringURL);
        try {
            URL url = new URL(fullStringURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {
            System.out.printf("Error sending data to collector.");
            e.printStackTrace();
        }
        //return result.toString();
    }

}
