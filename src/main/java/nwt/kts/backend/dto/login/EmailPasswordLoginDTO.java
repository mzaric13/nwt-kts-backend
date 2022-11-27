package nwt.kts.backend.dto.login;

public class EmailPasswordLoginDTO {

    private String username;
    private String password;

    public EmailPasswordLoginDTO() {
        super();
    }

    public EmailPasswordLoginDTO(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
