import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import spark.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static spark.Spark.*;


public class Main {
    private static Set<String> sessions = new HashSet<>();
    private static List<UserModel> userModels = new ArrayList<>();
    private static Set<String> setUser = new HashSet<>();

    public static void Init() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Todos", "postgres", "123456");
            PreparedStatement stmt = con.prepareStatement("select * from table_user");
            ResultSet rs = stmt.executeQuery();
            setUser.clear();
            userModels.clear();
            while(rs.next()) {
                UserModel userModel = new UserModel(rs.getString(1).trim(), rs.getString(2).trim());
                setUser.add(userModel.getusername());
                userModels.add(userModel);
                System.out.println(userModel.getusername() + " " + userModel.getPassWord());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        port(4568);
        Init();
        path("/data", () -> {
            before("/*", ((request, response) -> {
                boolean isAuth = false;
                String session = getSeeion(request);
                if(!sessions.contains(session)) {
                    halt(401, "bạn không được xác thực");
                }
            }));
        });
        get("/getAllUsers", ((request, response) -> new Gson().toJson(userModels)));
        after("*/", ((request, response) -> {
            //Can you make something
        }));
        post("/register", ((request, response) -> {
            String body = request.body();
            if(body != null) {
                String message = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
                message = message.replace("=", "" );
                UserModel userModel = new Gson().fromJson(message, UserModel.class);
                userModel.setusername(userModel.getusername().trim());
                userModel.setPassWord(userModel.getPassWord().trim());
                if(setUser.contains(userModel.getusername())) {
                    response.status(400);
                    return "not register";
                }
                System.out.println(userModel.getusername());
                userModels.add(userModel);
                setUser.add(userModel.getusername());
                String sql = "insert into table_user (username, password) " +
                        "values ('" + userModel.getusername() + "', '" + userModel.getPassWord() + "')";
                System.out.println(sql);
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Todos", "postgres", "123456");
                    PreparedStatement stmt = con.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                response.status(200);
                return "success";
            }
            response.status(400);
            return "not success";
        }));
        post("/addTask", ((request, response) -> {
            String body = request.body();
            if(body != null) {
                String message = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
                message = message.replace("=", "" );
                UserModel userModel = new Gson().fromJson(message, UserModel.class);
                userModel.setusername(userModel.getusername().trim());
                userModel.setPassWord(userModel.getPassWord().trim());
                String sql = "insert into table_Task (username, taskName, action) values ('" +
                        userModel.getusername() + "', '" + userModel.getPassWord() + "', 'New');";
                System.out.println(sql);
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Todos", "postgres", "123456");
                    PreparedStatement stmt = con.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                response.status(200);
                return "success";
            }
            response.status(400);
            return "not success";
        }));
        post("/login", ((request, response) -> {
            System.out.println("!!!!!!!!!!!!!!");
            String body = request.body();
            if(body != null) {
                String message = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
                message = message.replace("=", "" );
                UserModel userModel = new Gson().fromJson(message, UserModel.class);
                userModel.setusername(userModel.getusername().trim());
                userModel.setPassWord(userModel.getPassWord().trim());
                System.out.printf(userModel.getusername() + " ~~~~ " + userModel.getPassWord());
                for( UserModel x : userModels) {
                    System.out.println(x.getusername() + "___" + x.getPassWord());
                    if(x.getusername().equals(userModel.getusername()) && x.getPassWord().equals(userModel.getPassWord())) {
                        sessions.add(getSeeion(request));
                        response.status(200);
                        return "success";
                    }
                }
                System.out.println("\n" + userModel.getusername() + " " + userModel.getPassWord());
                halt(401, "Đăng nhập sai");
            }
            return "Login failed";
        }));
        post("/getTask", ((request, response) -> {
            System.out.println("star getTask");
            String body = request.body();
            if(body != null) {
                String message = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
                message = message.replace("=", "" );
                UserModel userModel = new Gson().fromJson(message, UserModel.class);
                userModel.setusername(userModel.getusername().trim());
                userModel.setPassWord(userModel.getPassWord().trim());
                System.out.println(userModel.getusername() + " ~~~~ " + userModel.getPassWord());
                String sql = "select * from table_Task where username = '" + userModel.getusername() +  "' and action = '" +
                        userModel.getPassWord() + "'";
                System.out.println(sql);
                List<JsonObject> tasks = new ArrayList<>();
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Todos", "postgres", "123456");
                    PreparedStatement stmt = con.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                    while(rs.next()) {
                        JsonObject jsonObject = new JsonObject();
                        try {
                            jsonObject.addProperty("username", rs.getString(1).trim());
                            jsonObject.addProperty("taskName", rs.getString(2).trim());
                            jsonObject.addProperty("action", rs.getString(3).trim());
                        } catch (JsonIOException ex) {
                            ex.printStackTrace();
                        }
                        System.out.println(jsonObject.toString());
                        tasks.add(jsonObject);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                response.status(200);
                System.out.println(tasks.toString());
                if(tasks.toString() == "[]") return "not size";
                return tasks;
            }
            return "Login failed";
        }));
        post("/convertTask", ((request, response) -> {
            System.out.println("star convertTask");
            String body = request.body();
            if(body != null) {
                String message = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
                message = message.replace("=", "" );
                TaskModel taskModel = new Gson().fromJson(message, TaskModel.class);
                taskModel.setUsername(taskModel.getUsername().trim());
                taskModel.setTaskName(taskModel.getTaskName().trim());
                taskModel.setAction(taskModel.getAction().trim());
                String tmp = "Doing";
                if(taskModel.getAction() == "Doing") tmp = "Done";
                String sql = "update table_Task set action = '" + tmp + "' where taskName = '"
                        + taskModel.getTaskName() + "' and username = '" + taskModel.getUsername() + "'";
                System.out.println(sql);
                List<JsonObject> tasks = new ArrayList<>();
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Todos", "postgres", "123456");
                    PreparedStatement stmt = con.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                response.status(200);
                return tasks;
            }
            return "Login failed";
        }));
        post("/logout", ((request, response) ->  {
            String session = getSeeion(request);
            if(sessions.contains(session)) {
                sessions.remove(session);
            }
            response.status(200);
            return "success";
        }));
        get("/test", (request, response) -> {
            return "Hello: 3";
        });
        stop();
    }

    private static String getSeeion(Request request) throws UnsupportedEncodingException {
        Map<String, String> cookies = request.cookies();
        for( String key : cookies.keySet() ) {
            String str = URLDecoder.decode(key, StandardCharsets.UTF_8.name());
            if(str.contains("JSESSIONID")) {
                String value = str.split("=")[1];
                return value;
            }
        }
        return "";
    }
}
