package model;

import java.time.Instant;
import java.util.ArrayList;

/**
 * This class holds all the poll data, including the results and some other information.
 *
 * Created by robin on 2016-03-11.
 */
public class Results {
    private String pollId;
    private String owner;
    private String topic;
    private Instant pollStartTime;
    private Instant pollFinishTime;
    private ArrayList<Question> questions;


    public Results() {
    }

    public Results(String pollId, String owner, String topic, Instant pollStartTime, Instant pollFinishTime, ArrayList<Question> questions) {
        this.pollId = pollId;
        this.owner = owner;
        this.topic = topic;
        this.pollStartTime = pollStartTime;
        this.pollFinishTime = pollFinishTime;
        this.questions = questions;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    // Returns string to use ISO-8601 representation in the JSON
    public String getPollStartTime() {
        return pollStartTime.toString();
    }

    public void setPollStartTime(Instant pollStartTime) {
        this.pollStartTime = pollStartTime;
    }

    // Returns string to use ISO-8601 representation in the JSON
    public String getPollFinishTime() {
        return pollFinishTime.toString();
    }

    public void setPollFinishTime(Instant pollFinishTime) {
        this.pollFinishTime = pollFinishTime;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
