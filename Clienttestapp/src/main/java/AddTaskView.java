import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddTaskView extends JFrame implements ActionListener {
    private JButton btnAddTask;
    private JTextField jtext;
    private String stinguser;
    private JButton btnCancel;

    public AddTaskView(String user) {
        super("Add task");
        stinguser = user;
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new FlowLayout());
        jtext = new JTextField(15);
        pnMain.add(jtext);
        btnAddTask = new JButton("Add Task");
        btnCancel = new JButton("Cancel");
        btnAddTask.addActionListener(this);
        pnMain.add(btnAddTask);
        btnCancel.addActionListener(this);
        pnMain.add(btnCancel);
        pnMain.setLayout(new GridLayout(5, 1));
        this.add(pnMain);
        this.setSize(300, 300);
        pnMain.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(111);
        JButton btn = (JButton) e.getSource();
        if(btn.equals(btnAddTask) ) {
            System.out.println("-> :" + jtext.getText());
            if(jtext.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Add task not successfullly!");
                return;
            }
            UserModel userModel = new UserModel(stinguser, jtext.getText());
            try {
                System.out.println( "data send sever " + userModel.getusername() + " " + userModel.getPassWord());
                sendPost(userModel);
                jtext.setText("");
                JOptionPane.showMessageDialog(this, "Add task successfullly!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Add task not successfullly!");
                ex.printStackTrace();
            }
        }
        if (btn.equals(btnCancel)) {
            this.setVisible(false);
        }
    }
    private void sendPost(UserModel userModel) throws Exception {

        String url = "http://localhost:4568/addTask";
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
    }
    public static void main(String[] args) {
        AddTaskView t = new AddTaskView("admin");
        t.setVisible(true);
    }
}
