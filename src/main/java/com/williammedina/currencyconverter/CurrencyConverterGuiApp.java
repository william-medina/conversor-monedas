package com.williammedina.currencyconverter;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.williammedina.currencyconverter.gui.MainForm;

import javax.swing.*;

public class CurrencyConverterGuiApp {
    public static void main(String[] args) {
        FlatDarculaLaf.setup(); // modo oscuro

        SwingUtilities.invokeLater(() -> {
            MainForm mainForm = new MainForm();
            mainForm.setVisible(true);
        });
    }
}
