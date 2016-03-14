package model;

import java.util.ArrayList;

/**
 * This class represents a single answer for a choice/question in a vote/poll.
 *
 * Created by robin on 14/3/16.
 */
public class Answer {
    private String answer;
    private int count;
    // TODO: MIGHT NOT BE STRING FOR KEYS, WAITING FOR INFORMATION ABOUT DATA TYPE
    private ArrayList<String> keysVoted;


    public Answer() {
    }

    public Answer(String answer, int count, ArrayList<String> keysVoted) {
        this.answer = answer;
        this.count = count;
        this.keysVoted = keysVoted;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<String> getKeysVoted() {
        return keysVoted;
    }

    public void setKeysVoted(ArrayList<String> keysVoted) {
        this.keysVoted = keysVoted;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "answer='" + answer + '\'' +
                ", count=" + count +
                ", keysVoted=" + keysVoted +
                '}';
    }
}
