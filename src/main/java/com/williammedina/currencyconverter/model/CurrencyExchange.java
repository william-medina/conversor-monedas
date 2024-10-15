package com.williammedina.currencyconverter.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CurrencyExchange {
    private String baseCurrency;      // Moneda base
    private String quoteCurrency;     // Moneda de cotización
    private double baseAmount;        // Cantidad de la moneda base
    private double exchangeRate;      // Tasa de cambio
    private double convertedAmount;   // Cantidad resultante en la moneda de cotización
    private String date;              // Fecha y hora de la conversión

    public CurrencyExchange(String baseCurrency, String quoteCurrency, double exchangeRate) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.exchangeRate = exchangeRate;
    }

    public void convertCurrency(double baseAmount) {
        this.baseAmount =  baseAmount;
        this.convertedAmount = baseAmount * this.exchangeRate;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.date = LocalDateTime.now().format(formatter);
    }

    @Override
    public String toString() {
        return "CurrencyExchange{" +
                "baseCurrency='" + baseCurrency + '\'' +
                ", quoteCurrency='" + quoteCurrency + '\'' +
                ", baseAmount=" + baseAmount +
                ", exchangeRate=" + exchangeRate +
                ", convertedAmount=" + convertedAmount +
                ", date=" + date +
                '}';
    }

    public String conversionResultString() {
        return String.format("=> \t%.2f %s equivalen a %.2f %s",
                baseAmount, baseCurrency, convertedAmount, quoteCurrency);
    }

    public String toTableString() {
        return String.format(
                "| %-18s | %-18s | %-16s | %-19s |",
                String.format("%.2f", baseAmount) + " " + baseCurrency,
                String.format("%.2f", convertedAmount) + " " + quoteCurrency,
                exchangeRate,
                date
        );
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
