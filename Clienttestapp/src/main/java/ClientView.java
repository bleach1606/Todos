import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ClientView extends JFrame implements ActionListener{
    private ArrayList<TaskModel> listTaskModel;
    private ArrayList<JButton> listButton;
    private JButton btnAddNew;
    private JButton btnUpload;
    private JButton btnNew;
    private JButton btnDoing;
    private JButton btnDone;
    private JTable tblResult;
    private UserModel userModel;
    private int k;

    public ClientView(UserModel userModel1){
        super("Search Client Demo");
        userModel = userModel1;
        System.out.println(userModel1.getusername() + " can you heo me!!! " + userModel1.getPassWord());
        listTaskModel = new ArrayList<>();
        listButton = new ArrayList<>();
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));

        JPanel pn1 = new JPanel();
        pn1.setLayout(new BoxLayout(pn1,BoxLayout.X_AXIS));
        pn1.setSize(this.getSize().width-5, 20);
        pn1.add(new JLabel());

        btnAddNew = new JButton("Add new task");
        btnAddNew.addActionListener(this);
        pn1.add(btnAddNew);
        pnMain.add(pn1);

        JPanel pn3 = new JPanel();
        pn3.setLayout(new BoxLayout(pn3,BoxLayout.X_AXIS));
        pn3.setSize(this.getSize().width-5, 20);
        btnNew = new JButton("New");
        btnDoing = new JButton("Doing");
        btnDone = new JButton("Done");
        btnNew.addActionListener(this);
        pn3.add(btnNew);
        btnDoing.addActionListener(this);
        pn3.add(btnDoing);
        btnDone.addActionListener(this);
        pn3.add(btnDone);
        pnMain.add(pn3);

        JPanel pn2 = new JPanel();
        pn2.setLayout(new BoxLayout(pn2,BoxLayout.Y_AXIS));
        tblResult = new JTable(new ClientTableModel());
        JScrollPane scrollPane= new  JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));

        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        tblResult.getColumn("Action").setCellRenderer(buttonRenderer);
        tblResult.getColumn("Action").setWidth(30);
        tblResult.addMouseListener(new JTableButtonMouseListener(tblResult));
        pn2.add(scrollPane);
        pnMain.add(pn2);
        this.add(pnMain);
        pnMain.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JButton btnClicked = (JButton)e.getSource();
        if(k == 3) return ;
        for(int i = 0; i <listButton.size(); i++)
            if(btnClicked.equals(listButton.get(i))){
                System.out.println("this is button : " + i);
                try {
                    sendPost(listTaskModel.get(i));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if(k == 1) btnClicked = btnNew;
                if(k == 2) btnClicked = btnDoing;
                ((DefaultTableModel)tblResult.getModel()).fireTableDataChanged();
                return;
            }
        if(btnClicked.equals(btnAddNew)){
            AddTaskView t = new AddTaskView(userModel.getusername());
            t.setVisible(true);
            ((DefaultTableModel)tblResult.getModel()).fireTableDataChanged();
            return;
        }
        if(btnClicked.equals(btnNew)) {
            k = 1;
            try {
                listTaskModel = sendGet(new UserModel(userModel.getusername(), "New"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(k==-1) return;
            for(int i = 0; i< listTaskModel.size(); i++){
                JButton btn = new JButton("Make Doing");
                btn.addActionListener(this);
                listButton.add(btn);
            }
            ((DefaultTableModel)tblResult.getModel()).fireTableDataChanged();
            return;
        }
        if(btnClicked.equals(btnDoing)) {
            k = 2;
            try {
                listTaskModel = sendGet(new UserModel(userModel.getusername(), "Doing"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            for(int i = 0; i< listTaskModel.size(); i++){
                JButton btn = new JButton("Make Done");
                btn.addActionListener(this);
                listButton.add(btn);
            }
            ((DefaultTableModel)tblResult.getModel()).fireTableDataChanged();
            return;
        }
        if(btnClicked.equals(btnDone)) {
            k = 3;
            try {
                listTaskModel = sendGet(new UserModel(userModel.getusername(), "Done"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(k != -1)
            for(int i = 0; i< listTaskModel.size(); i++){
                JButton btn = new JButton("Done");
                btn.addActionListener(this);
                listButton.add(btn);
            }
            ((DefaultTableModel)tblResult.getModel()).fireTableDataChanged();
            return;
        }

    }

    public void refreshResultAfterUpdate(int index, TaskModel client){
        listTaskModel.remove(index);
        listTaskModel.add(index, client);
        ((DefaultTableModel)tblResult.getModel()).fireTableDataChanged();
    }

    private void btnDeleteClick(int index){

        ((DefaultTableModel)tblResult.getModel()).fireTableDataChanged();
    }

    class ClientTableModel extends DefaultTableModel {
        private String[] columnNames = {"#", "Task Name", "Action"};
        private final Class<?>[] columnTypes = new  Class<?>[] {Integer.class, String.class, JButton.class};

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        @Override
        public int getRowCount() {
            return listTaskModel.size();
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            /*Adding components*/
            switch (columnIndex) {
                case 0:
                    return rowIndex+1;
                case 1:
                    return listTaskModel.get(rowIndex).getTaskName();
                case 2:
                    return listButton.get(rowIndex);
                default: return "Error";
            }
        }
    }

    class JTableButtonMouseListener extends MouseAdapter {
        private final JTable table;

        public JTableButtonMouseListener(JTable table) {
            this.table = table;
        }

        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
            int row    = e.getY()/table.getRowHeight(); //get the row of the button

            //*Checking the row or column is valid or not
            if (row < table.getRowCount() && row >= 0  && column < table.getColumnCount() && column >= 0)  {
                Object value = table.getValueAt(row, column);
                if (value instanceof JButton) {
                    //perform a click event
                    ((JButton)value).doClick();
                }
            }
        }
    }

    class JTableButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                 boolean hasFocus, int row, int column) {
            JButton button = (JButton)value;
            return button;
        }
    }

    private ArrayList<TaskModel> sendGet(UserModel userModel) throws Exception {
        System.out.println(userModel.getusername() + " " + userModel.getPassWord());
        String url = "http://localhost:4568/getTask";
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
            while ((responseLine = br.readLine()) != null) {
                String json = responseLine.toString();
                System.out.println("json = " + json);
                if(json.equals("not size")) {
                    k = -1;

                    ClientView myFrame = new ClientView(userModel);
                    myFrame.setSize(600,300);
                    myFrame.setVisible(true);
                    myFrame.setLocation(200,10);
                    this.setVisible(false);
                }
                String temp = "";
                for(int i = 1; i < json.length()-1; i++) {
                    temp = temp + json.charAt(i);
                }
                json = temp;
                String jsons[] = json.split(", ", 0);
                for(String x : jsons) {
//                    System.out.println(x);
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        TaskModel taskModel = mapper.readValue(x, TaskModel.class);
                        taskModels.add(taskModel);
//                        System.out.println("this task new : " + task.getTaskName() + " " + task.getUsername() );
                    } catch (IOException e) {
                        System.out.println("hom nay toi buon!" + json);
                        e.printStackTrace();
                    }
                }
            }
            con.disconnect();
//            System.out.println(tasks.size());
            return taskModels;
        }
    }
    private void sendPost(TaskModel taskModel) throws Exception {
        System.out.println("convertTask " + taskModel.getTaskName() + " " + taskModel.getUsername() +  " " + taskModel.getAction());
        String url = "http://localhost:4568/convertTask";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        // Send post request
        con.setDoOutput(true);
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("username", taskModel.getUsername());
            jsonObject.addProperty("taskName", taskModel.getTaskName());
            jsonObject.addProperty("action", taskModel.getAction());
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
        con.disconnect();
        return;
    }
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        ClientView myFrame = new ClientView(new UserModel("bleach01", "1"));
        myFrame.setSize(600,300);
        myFrame.setVisible(true);
        myFrame.setLocation(200,10);
    }
}