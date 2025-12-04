package AccountManager;

public class Account {
    private String username;
    private String password;
    private String ID;

    public Account(String username, String password, String ID){
        this.username = username;
        this.password = password;
        this.ID = ID;
    }

    public void setUsername(String username){ this.username = username; }
    public String getUsername(){ return username; }

    public void setPassword(String password){ this.password = password; }
    public String getPassword(){ return password; }

    public void setID(String ID){ this.ID = ID;}
    public String getID(){ return ID;}
}
