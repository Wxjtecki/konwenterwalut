//Autor Kamil Pajączkowski



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class konwenterwalut extends JFrame implements ActionListener {
    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JTextField amountField;
    private JButton convertButton;
    private JLabel resultLabel;

    
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public konwenterwalut() {
        setTitle("Konwerter Walut");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new GridLayout(5, 2, 10, 10));

        
        JLabel fromLabel = new JLabel("Z waluty:");
        fromCurrency = new JComboBox<>(getCurrencies());

        JLabel toLabel = new JLabel("Na walutę:");
        toCurrency = new JComboBox<>(getCurrencies());

        JLabel amountLabel = new JLabel("Kwota:");
        amountField = new JTextField();

        convertButton = new JButton("Przelicz");
        convertButton.addActionListener(this);

        resultLabel = new JLabel("Wynik: ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));

        
        add(fromLabel);
        add(fromCurrency);
        add(toLabel);
        add(toCurrency);
        add(amountLabel);
        add(amountField);
        add(new JLabel()); 
        add(convertButton);
        add(new JLabel()); 
        add(resultLabel);

        setVisible(true);
    }

    
    private String[] getCurrencies() {
        return new String[] {
            "USD", "EUR", "GBP", "PLN", "JPY", "CNY", "AUD", "CAD", "CHF", "SEK"
            
        };
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String from = (String) fromCurrency.getSelectedItem();
        String to = (String) toCurrency.getSelectedItem();
        String amountText = amountField.getText();

        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Proszę wprowadzić kwotę.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Proszę wprowadzić poprawną liczbę.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (from.equals(to)) {
            resultLabel.setText("Wynik: " + amount + " " + to);
            return;
        }

        
        double rate = getExchangeRate(from, to);
        if (rate == -1) {
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania kursów walut.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double result = amount * rate;
        resultLabel.setText(String.format("Wynik: %.2f %s", result, to));
    }

    
    private double getExchangeRate(String from, String to) {
        try {
            URL url = new URL(API_URL + from);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if(responseCode != 200){
                System.out.println("Błąd w połączeniu: " + responseCode);
                return -1;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONObject rates = json.getJSONObject("conversion_rates");

            return rates.getDouble(to);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ignored){}

        SwingUtilities.invokeLater(() -> new konwenterwalut());
    }
}
