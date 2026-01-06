package models;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private String name;
    private String date;
    private String venue;
    private String type; // Oral or Poster
    private List<Student> presenters;
    private List<Evaluator> evaluators;

    public Session(String name, String date, String venue, String type) {
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.type = type;
        this.presenters = new ArrayList<>();
        this.evaluators = new ArrayList<>();
    }

    public void addPresenter(Student student) {
        presenters.add(student);
    }

    public void addEvaluator(Evaluator evaluator) {
        evaluators.add(evaluator);
    }
}