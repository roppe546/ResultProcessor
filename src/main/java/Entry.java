import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used for testing sending json objects.
 *
 * Created by robin on 2016-03-10.
 */
public class Entry {
    private String entryName;
    private int voteCount;
    private float percentage;

    public Entry(String party, int voteCount, float percentage) {
        this.entryName = party;
        this.voteCount = voteCount;
        this.percentage = percentage;
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

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "{ entryName='" + entryName + '\'' +
                ", voteCount=" + voteCount +
                ", percentage=" + percentage +
                '}';
    }
}
