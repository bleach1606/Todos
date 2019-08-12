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
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
public class LoginView extends JFrame implements ActionListener{

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private LoginModel model;
    public LoginView() {
        super("Login");
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPassword.setEchoChar('*');
        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");

        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(new JLabel("Username:"));
        content.add(txtUsername);
        content.add(new JLabel("Password:"));
        content.add(txtPassword);
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
            actionLogin();
        }
        else if(btn.equals(btnRegister)) {
            this.setVisible(false);
            RegisterView registerView = new RegisterView();
            registerView.setVisible(true);
        }
    }

    public void actionLogin() {
        try {
            UserModel userModel = new UserModel(txtUsername.getText(), txtPassword.getText());
            if(checkUser(userModel)){
                this.showMessage("Login succesfully!");
                this.setVisible(false);
                ClientView myFrame = new ClientView(new UserModel(userModel.getusername(), userModel.getPassWord()));
                myFrame.setSize(600,300);
                myFrame.setVisible(true);
                myFrame.setLocation(200,10);
                // làm gì đó
            }else{
                this.showMessage("Invalid username and/or password!");
            }
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace().toString());
            this.showMessage(ex.getStackTrace().toString());
        }
    }
    public boolean checkUser(UserModel userModel) throws Exception {
        try {
            sendPost(userModel);
            return true;
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login not successfullly!");
            ex.printStackTrace();
        }
        return false;
    }
    private void sendPost(UserModel userModel) throws Exception {
        System.out.println(userModel.getusername() + " " + userModel.getPassWord());
        String url = "http://localhost:4568/login";
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
            ArrayList<TaskModel> taskModels = new ArrayList<>();
            System.out.println(taskModels.size());
        }
        con.disconnect();
    }
    public LoginModel getUser(){
        model = new LoginModel(txtUsername.getText(), txtPassword.getText());
        return model;
    }

    public void showMessage(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }

    public static void main(String[] args) {
        LoginView test = new LoginView();
        test.setVisible(true);
    }
}