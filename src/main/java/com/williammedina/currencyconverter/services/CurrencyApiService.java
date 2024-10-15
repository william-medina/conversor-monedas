package com.williammedina.currencyconverter.services;

import com.google.gson.Gson;
import com.williammedina.currencyconverter.model.ApiResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrencyApiService {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public ApiResponse getExchangeRates(String baseCurrency) {
        URI url = URI.create(API_URL + baseCurrency);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), ApiResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las tasas de cambio: " + e.getMessage());
        }
    }
}
