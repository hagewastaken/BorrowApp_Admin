package com.example.testfx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiUtil {
    private static String baseUrl;

    static {
        try (InputStream input = ApiUtil.class.getClassLoader().getResourceAsStream("com/example/testfx/config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                throw new FileNotFoundException("config.properties file not found in the classpath");
            }
            prop.load(input);
            baseUrl = prop.getProperty("baseUrl");
        } catch (IOException ex) {
            ex.printStackTrace();
            baseUrl = "http://localhost:3000"; // Fallback URL
            System.out.println("Using localhost");
        }
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String newBaseUrl) {
        baseUrl = newBaseUrl;
    }

    public static String getEndpoint(String endpoint) {
        return baseUrl + endpoint;
    }
}