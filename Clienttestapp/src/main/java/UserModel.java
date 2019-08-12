
public class UserModel {
    private String username;
    private String passWord;

    public UserModel() {
    }

    public UserModel(String username, String passWord) {
        this.username = username;
        this.passWord = passWord;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
