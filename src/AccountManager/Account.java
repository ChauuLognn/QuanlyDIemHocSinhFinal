package AccountManager;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    //dòng này để định danh phiên bản
    private String username;
    private String password;
    private String ID;
    private String role;

    public Account(String username, String password, String ID, String role){
        this.username = username;
        this.password = password;
        this.ID = ID;
        this.role = role;
    }

    public Account(String username, String password, String ID){
        this(username, password, ID, "student");
    }

    public void setUsername(String username){ this.username = username; }
    public String getUsername(){ return username; }

    public void setPassword(String password){ this.password = password; }
    public String getPassword(){ return password; }

    public void setID(String ID){ this.ID = ID;}
    public String getID(){ return ID;}

    public void setRole(String role) { this.role = role; }
    public String getRole() { return role; }
}
