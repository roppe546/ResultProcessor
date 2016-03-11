package model;

import java.util.ArrayList;

/**
 * This class is used for testing sending json objects.
 *
 * Created by robin on 2016-03-10.
 */
public class Entry {
    private String entryName;
    private int voteCount;
    private ArrayList<Long> keysVoted;

    public Entry(String party, int voteCount) {
        this.entryName = party;
        this.voteCount = voteCount;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String party) {
        this.entryName = party;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public ArrayList<Long> getKeysVoted() {
        return keysVoted;
    }

    public void setKeysVoted(ArrayList<Long> keysVoted) {
        this.keysVoted = keysVoted;
    }

    @Override
    public String toString() {
        return "{ entryName='" + entryName + '\'' +
                ", voteCount=" + voteCount +
                '}';
    }
}