package main.User;

public class User {
    private String username;
    private String password;

    public User(String _username, String _password) {
        this.username = _username;
        this.password = _password;
    }

    public String getUsername(){ return this.username; }
    public String getPassword(){ return this.password; }

    public void setUsername(String username){ this.username = username; }
    public void setPassword(String password){ this.password = password; }
}
