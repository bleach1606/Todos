import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
public class RegisterView extends JFrame implements ActionListener{
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtPassword1;
    private JButton btnLogin;
    private JButton btnRegister;
    private LoginModel model;

    public RegisterView(){
        super("Register");
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPassword.setEchoChar('*');
        txtPassword1 = new JPasswordField(15);
        txtPassword1.setEchoChar('*');
        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");

        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(new JLabel("Username:"));
        content.add(txtUsername);
        content.add(new JLabel("Password:"));
        content.add(txtPassword);
        content.add(new JLabel("Password:"));
        content.add(txtPassword1);
        content.add(btnLogin);
        content.add(btnRegister);
        content.setLayout(new GridLayout(5, 1));
        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);
        this.setContentPane(content);
        this.pack();
    }
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        if(btn.equals(btnLogin) ) {
            this.setVisible(false);
            LoginView view  = new LoginView();
            view.setVisible(true);
            this.getDropTarget();
        }
        else if(btn.equals(btnRegister) && txtUsername.getText() != null &&
                txtPassword.getText() != null && txtPassword.getText().equals(txtPassword1.getText())) {
                UserModel userModel = new UserModel(txtUsername.getText(), txtPassword.getText());
            try {
                sendPost(userModel);
                JOptionPane.showMessageDialog(this, "Register successfullly!");
                LoginView test = new LoginView();
                test.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Register not successfullly!");
                txtUsername.setText("");
                txtPassword.setText("");
                txtPassword1.setText("");
                ex.printStackTrace();
            }
        }
    }
    private void sendPost(UserModel userModel) throws Exception {

        String url = "http://localhost:4568/register";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        // Send post request
        con.setDoOutput(true);
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("username", userModel.getusername());
            jsonObject.addProperty("passWord", userModel.getPassWord());
        } catch (JsonIOException ex) {
            ex.printStackTrace();
        }
        String jsonInputString = jsonObject.toString();
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
        con.disconnect();

    }

    public static void main(String[] args) {
        RegisterView test = new RegisterView();
        test.setVisible(true);
    }
}