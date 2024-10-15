package com.williammedina.currencyconverter.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.williammedina.currencyconverter.model.CurrencyExchange;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConversionsFileService {

    // Nombre del archivo de historial
    private static final String FILENAME = "conversionsHistory.json";

    // Lista de conversiones de divisas
    private List<CurrencyExchange> currencyExchangeList;

    public ConversionsFileService() {
        this.currencyExchangeList = new ArrayList<>();
        initializeFile();
    }

    // Inicializa el archivo, lo crea si no existe y carga el historial si ya existe
    private void initializeFile() {
        File file = new File(FILENAME);
        try {
            if (!file.exists()) {
                file.createNewFile(); // Crea el archivo si no existe
            } else {
                loadConversionHistory(); // Carga el historial si el archivo ya existe
            }
        } catch (IOException e) {
            System.err.println("Error al crear o cargar el archivo: " + e.getMessage());
        }
    }

    // Carga el historial de conversiones desde el archivo
    private void loadConversionHistory() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type currencyExchangeListType = new TypeToken<List<CurrencyExchange>>(){}.getType();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            // Convierte el contenido del archivo JSON en una lista de objetos CurrencyExchange
            this.currencyExchangeList = gson.fromJson(reader, currencyExchangeListType);

            // Inicializa la lista si el archivo está vacío o contiene datos no válidos
            if (this.currencyExchangeList == null) {
                this.currencyExchangeList = new ArrayList<>();
            }

        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    // Guarda una nueva conversión en el archivo
    public void saveConversion(CurrencyExchange currencyExchange) {
        if (currencyExchange != null) {
            currencyExchangeList.add(currencyExchange); // Agrega la nueva conversión a la lista
            saveToFile(); // Guarda la lista actualizada en el archivo
        }
    }

    // Convierte la lista de conversiones a JSON y lo escribe en el archivo
    private void saveToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(currencyExchangeList); // Convierte la lista a formato JSON

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILENAME))) {
            writer.write(json); // Escribe el JSON en el archivo
        } catch (IOException e) {
            System.err.println("Error al guardar la conversión: " + e.getMessage());
        }
    }

    // Devuelve la lista actual de conversiones
    public List<CurrencyExchange> getCurrencyExchangeList() {
        return currencyExchangeList;
    }
}
