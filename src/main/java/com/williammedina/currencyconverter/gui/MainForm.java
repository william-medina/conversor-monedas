package com.williammedina.currencyconverter.gui;

import com.williammedina.currencyconverter.model.ApiResponse;
import com.williammedina.currencyconverter.model.CurrencyExchange;
import com.williammedina.currencyconverter.services.ConversionsFileService;
import com.williammedina.currencyconverter.services.CurrencyApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainForm extends JFrame {
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 700;
    private static final String WINDOW_TITLE = "Conversor de Monedas";
    private static final String CURRENCY_EMOJI = "\uD83D\uDCB0";
    private static final String HEADER_COLOR = "#3B3B3B";
    private static final String DECIMAL_FORMAT_PATTERN = "#.00";

    private JPanel mainPanel;
    private JTextField textFieldBase;
    private JTextField textFieldQuote;
    private JComboBox<String> comboBoxBase;
    private JComboBox<String> comboBoxQuote;
    private JButton convertButton;
    private JLabel titleLabel;
    private JTable tableConvertions;
    private JScrollPane scrollTable;
    private DefaultTableModel tableModelConvertions;

    Map<String, String> currencyMap = new LinkedHashMap<>();

    public MainForm() {
        setupFrame();
        customizeTitleLabel();
        setupScrollPane();
        loadIcon();
        populateCurrencyComboBox(comboBoxBase, "Dólar Estadounidense (USD)");
        populateCurrencyComboBox(comboBoxQuote, "Euro (EUR)");
        applyNumericFilter(textFieldBase);
        convertButton.addActionListener(e -> convertCurrency() );
    }

    private void setupFrame() {
        setContentPane(mainPanel);
        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        convertButton.setMargin(new Insets(8, 12, 10, 12));
    }

    private void customizeTitleLabel() {
        titleLabel.setText(CURRENCY_EMOJI + " " + titleLabel.getText() + " " + CURRENCY_EMOJI);
    }

    private void setupScrollPane() {
        scrollTable.setPreferredSize(new Dimension(scrollTable.getPreferredSize().width, 200));
    }

    private void loadIcon() {
        ImageIcon icon = new ImageIcon("src/main/resources/logo.png");
        setIconImage(icon.getImage());
    }

    private void populateCurrencyComboBox(JComboBox<String> comboBox, String defaultCurrency) {

        currencyMap.put("Dólar Australiano (AUD)", "AUD");
        currencyMap.put("Dólar Canadiense (CAD)", "CAD");
        currencyMap.put("Dólar Estadounidense (USD)", "USD");
        currencyMap.put("Euro (EUR)", "EUR");
        currencyMap.put("Franco Suizo (CHF)", "CHF");
        currencyMap.put("Libra Esterlina (GBP)", "GBP");
        currencyMap.put("Peso Colombiano (COP)", "COP");
        currencyMap.put("Peso Mexicano (MXN)", "MXN");
        currencyMap.put("Real Brasileño (BRL)", "BRL");
        currencyMap.put("Rublo Ruso (RUB)", "RUB");
        currencyMap.put("Yen Japonés (JPY)", "JPY");
        currencyMap.put("Yuan Chino (CNY)", "CNY");

        // Llenar el JComboBox con las monedas
        currencyMap.forEach((name, code) -> comboBox.addItem(name));

        // Seleccionar el valor por defecto
        comboBox.setSelectedItem(defaultCurrency);
    }

    private void applyNumericFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
    }

    private void convertCurrency() {

        if (textFieldBase.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un valor para convertir.",
                    "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        var baseCurrency = currencyMap.get(comboBoxBase.getSelectedItem());
        var quoteCurrency = currencyMap.get(comboBoxQuote.getSelectedItem());
        var baseAmount = Double.parseDouble(textFieldBase.getText());

        // Servicio para obtener las tasas de cambio actuales
        CurrencyApiService service = new CurrencyApiService();

        // Obtiene las tasas de cambio para la moneda base
        ApiResponse rates = service.getExchangeRates(baseCurrency);

        // Extrae la tasa de cambio para la moneda de cotización seleccionada
        var exchangeRate = rates.getConversion_rates().get(quoteCurrency);

        CurrencyExchange currencyExchange = new CurrencyExchange(baseCurrency, quoteCurrency, exchangeRate);
        currencyExchange.convertCurrency(baseAmount);

        textFieldQuote.setText(String.format("%.2f", currencyExchange.getConvertedAmount()));

        ConversionsFileService conversionsFileService = new ConversionsFileService();
        conversionsFileService.saveConversion(currencyExchange);
        listTableValues();
    }

    private void createUIComponents() {
        createTableModel();
        createTable();
        customizeTableHeader();
        listTableValues();
    }

    private void createTableModel() {
        this.tableModelConvertions = new DefaultTableModel(0, 4) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Desactiva la edición de celdas
            }
        };
        this.tableModelConvertions.setColumnIdentifiers(new String[]{"Valor Base", "Valor Convertido", "Tasa de Cambio", "Fecha"});
    }

    private void createTable() {
        this.tableConvertions = new JTable(tableModelConvertions);
        tableConvertions.setCellSelectionEnabled(false);
        tableConvertions.setFocusable(false);
    }

    private void customizeTableHeader() {
        JTableHeader header = tableConvertions.getTableHeader();
        header.setBackground(Color.decode(HEADER_COLOR));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Franklin Gothic Medium Cond", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
    }

    private void listTableValues() {
        ConversionsFileService conversionsFileService = new ConversionsFileService();
        this.tableModelConvertions.setRowCount(0);
        var conversions = conversionsFileService.getCurrencyExchangeList();

        DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT_PATTERN);

        // Invertir el orden de las conversiones para listar de atrás hacia adelante
        for (int i = conversions.size() - 1; i >= 0; i--) {
            var conversion = conversions.get(i);
            Object[] row = {
                    conversion.getBaseAmount() + " " + conversion.getBaseCurrency(),
                    decimalFormat.format(conversion.getConvertedAmount()) + " " + conversion.getQuoteCurrency(),
                    conversion.getExchangeRate(),
                    conversion.getDate()
            };
            this.tableModelConvertions.addRow(row);
        }
    }

    private static class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("[0-9]*")) { // Solo permite números
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("[0-9]*")) { // Solo permite números
                super.replace(fb, offset, length, string, attr);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException, BadLocationException {
            super.remove(fb, offset, length);
        }
    }
}
