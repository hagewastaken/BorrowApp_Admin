package com.example.testfx;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Auth {

    //private static final String BASE_URL = "http://localhost:3000"; // Replace with your backend URL
    private static String token; // Store token locally in Auth class

    // Method to handle login and retrieve token
    public static boolean login(String email, String password) {
        try {
            String endpoint = "/users/login";
            String urlString = ApiUtil.getEndpoint(endpoint);

            URL url = new URL(urlString);
            //URL url = new URL(BASE_URL + "/users/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Chrome/51.0.2704.103");
            conn.setRequestProperty("Content-Type", "application/json");

            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            requestBody.put("password", password);

            // Send POST request
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString().trim());
                    token = jsonResponse.getString("token"); // Store token locally
                    return true;
                }
            } else {
                System.out.println("Login failed. Response code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error occurred during login: " + e.getMessage());
            return false;
        }
    }

    // Method to retrieve the stored token
    public static String getToken() {
        return token;
    }
}