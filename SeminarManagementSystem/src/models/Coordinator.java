package models;

public class Coordinator extends User {
    private String name;

    public Coordinator(String username, String password, String name) {
        super(username, password, "Coordinator");// ? is this the user's name or the user's type?
        this.name = name;
    }

    public void createSession() {
    }

    public void assign() {
    }

    public void generateReport() {
    }

    public void nominateAward() {
    }
}