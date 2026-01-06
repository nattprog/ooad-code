package models;

public class Award {
    private String name;   
    private String winner; 
    private Session session;

    public Award(String name, Session session) {
        this.name = name;
        this.session = session;
    }

    public void addScore(double score) {
        
    }
}