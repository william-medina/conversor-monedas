package com.williammedina.currencyconverter;

import com.google.gson.Gson;
import com.williammedina.currencyconverter.model.ApiResponse;
import com.williammedina.currencyconverter.model.CurrencyExchange;
import com.williammedina.currencyconverter.services.ConversionsFileService;
import com.williammedina.currencyconverter.services.CurrencyApiService;

import java.util.List;
import java.util.Scanner;

public class CurrencyConverterConsoleApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            var option = showMenu(scanner);

            switch (option) {
                case 1 -> convertCurrency("USD", "EUR", scanner);
                case 2 -> convertCurrency("USD", "COP", scanner);
                case 3 -> convertCurrency("EUR", "USD", scanner);
                case 4 -> convertCurrency("EUR", "COP", scanner);
                case 5 -> convertCurrency("COP", "USD", scanner);
                case 6 -> convertCurrency("COP", "EUR", scanner);
                case 7 -> convertManualCurrency(scanner);
                case 8 -> showConversionHistory();
                case 9 -> {
                    System.out.println("Saliendo del programa...");
                    return;
                }
                default -> System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }

            waitForEnter(scanner);
        }
    }
    private static int showMenu(Scanner scanner) {
        System.out.print("""
            ******************************************************
            ***             CONVERSOR DE MONEDAS               ***
            ******************************************************

            Selecciona una opción para realizar la conversión:
            ------------------------------------------------------
            | 1) Dólar (USD) =>> Euro (EUR)                      |
            | 2) Dólar (USD) =>> Peso colombiano (COP)           |
            | 3) Euro (EUR) =>> Dólar (USD)                      |
            | 4) Euro (EUR) =>> Peso colombiano (COP)            |
            | 5) Peso colombiano (COP) =>> Dólar (USD)           |
            | 6) Peso colombiano (COP) =>> Euro (EUR)            |
            | 7) Ingresar otras divisas manualmente              |
            | 8) Ver historial de conversiones                   |
            | 9) Salir                                           |
            ------------------------------------------------------
            """);
        System.out.print("Ingrese su opción: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void convertCurrency(String baseCurrency, String quoteCurrency, Scanner scanner) {
        try {
            // Servicio para obtener las tasas de cambio actuales
            CurrencyApiService service = new CurrencyApiService();

            // Obtiene las tasas de cambio para la moneda base
            ApiResponse rates = service.getExchangeRates(baseCurrency);

            // Extrae la tasa de cambio para la moneda de cotización seleccionada
            double exchangeRate = rates.getConversion_rates().get(quoteCurrency);

            // Crea una instancia de CurrencyExchange con los datos de la conversión
            CurrencyExchange currencyExchange = new CurrencyExchange(baseCurrency, quoteCurrency, exchangeRate);

            // Solicita al usuario ingresar la cantidad de la moneda base que desea convertir
            System.out.print("Ingresa el valor a convertir: ");
            var baseAmount = Double.parseDouble(scanner.nextLine());

            // Establece la cantidad base en el objeto CurrencyExchange
            currencyExchange.convertCurrency(baseAmount);

            // Muestra el resultado de la conversión
            System.out.println("------------------------------------------------------");
            System.out.println(currencyExchange.conversionResultString());
            System.out.println("------------------------------------------------------");

            // Almacena la conversión en un archivo .txt
            saveConversionToFile(currencyExchange);
        } catch (NullPointerException e) {
            System.out.println("Error: Moneda no encontrada.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Valor inválido.");
        }
    }

    private static void convertManualCurrency(Scanner scanner) {
        System.out.print("Ingresa la moneda base (ej: USD): ");
        var baseCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Ingresa la moneda cotizante (ej: EUR): ");
        var quoteCurrency = scanner.nextLine().toUpperCase();

        convertCurrency(baseCurrency, quoteCurrency, scanner);
    }

    private static void showConversionHistory() {
        ConversionsFileService conversionsFileService = new ConversionsFileService();
        List<CurrencyExchange> currencyExchangeList = conversionsFileService.getCurrencyExchangeList();

        System.out.println("");
        // Verificar si el historial está vacío
        if (currencyExchangeList.isEmpty()) {
            System.out.println("------------------------------------------------------");
            System.out.println("|           No hay registros disponibles             |");
            System.out.println("------------------------------------------------------");
            return;
        }

        // Mostrar las últimas 10 conversiones, desde la más reciente
        int historySize = currencyExchangeList.size();
        int start = Math.max(0, historySize - 10); // Determinar el punto de inicio para las últimas 10

        System.out.println("                            - Últimas 10 Conversiones -                             ");
        System.out.println("------------------------------------------------------------------------------------");

        System.out.println(String.format(
                "| %-18s | %-18s | %-16s | %-19s |",
                center("Valor Base", 18),
                center("Valor Convertido", 18),
                center("Tasa de Cambio", 16),
                center("Fecha", 19)
        ));
        System.out.println("------------------------------------------------------------------------------------");
        // Recorrer desde el final hacia el inicio
        for (int i = historySize - 1; i >= start; i--) {
            CurrencyExchange conversion = currencyExchangeList.get(i);
            System.out.println(conversion.toTableString());
        }

        System.out.println("------------------------------------------------------------------------------------");

    }

    private static void saveConversionToFile(CurrencyExchange currencyExchange) {
        ConversionsFileService conversionsFileService = new ConversionsFileService();
        conversionsFileService.saveConversion(currencyExchange);
    }

    public static String center(String text, int width) {
        int spaces = width - text.length();
        int pad = spaces / 2;
        StringBuilder sb = new StringBuilder();

        // Agregar espacios a la izquierda
        for (int i = 0; i < pad; i++) {
            sb.append(" ");
        }
        sb.append(text);

        // Agregar espacios a la derecha
        for (int i = 0; i < spaces - pad; i++) {
            sb.append(" ");
        }

        return sb.toString();
    }
    private static void waitForEnter(Scanner scanner) {
        System.out.print("Presiona Enter para continuar... ");
        scanner.nextLine();
        System.out.println("");
    }
}

