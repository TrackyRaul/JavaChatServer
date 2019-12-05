public class User {
    private String ip;
    private String username = "Guest";

    public User(String ip, String username) {
        this.ip = ip;
        this.username = username;
    }

    public User(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
