package rw.config;

public class Config {
    public User user;
    public Account account;

    Config() {
        this.user = new User();
        this.account = new Account();
    }
}
