package model;

import java.util.ArrayList;

/**
 * This class represents a choice/question in a vote/poll.
 *
 * Created by robin on 2016-03-10.
 */
public class Question {
    private String question;
    private ArrayList<Answer> answers;


    public Question() {
    }

    public Question(String question, ArrayList<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}
