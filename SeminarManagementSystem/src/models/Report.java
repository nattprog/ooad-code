package models;

import java.util.List;

public class Report {
    private List<Session> sessions;
    private List<Evaluation> evaluations;
    private List<Award> awards;

    public Report(List<Session> sessions, List<Evaluation> evaluations, List<Award> awards) {
        this.sessions = sessions;
        this.evaluations = evaluations;
        this.awards = awards;
    }

    public void generatePDF() {
        System.out.println("Exporting seminar data to PDF...");
    }
}