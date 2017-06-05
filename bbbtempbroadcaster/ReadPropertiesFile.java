/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bbbtempbroadcaster;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author don
 * # This is the config.properties file
 * favoriteFood=kun pao chicken
 * favoriteVegetable=artichoke
 * favoriteSoda=Dr Pepper
 */
public class ReadPropertiesFile {

    public static Map<String,String> read() {
        Map<String,String> props = new HashMap<>();
        
        try {
            System.out.println("Attempting to read config.properties");
            
            // get the local working directory, so we can run this from cron and not
            // have to worry about current working directories and such
            String path = ReadPropertiesFile.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            decodedPath = decodedPath.replace("BBBTempBroadcaster.jar","");
            
            File file = new File(decodedPath + "config.properties");
            Properties properties;
            try (FileInputStream fileInput = new FileInputStream(file)) {
                properties = new Properties();
                properties.load(fileInput);
            }

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();
                String value = properties.getProperty(key);
                System.out.println("Adding key/value to in-memory properties: " + key + " = " + value);
                props.put(key,value);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

}
