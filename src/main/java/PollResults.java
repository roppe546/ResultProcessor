import java.time.Instant;
import java.util.ArrayList;

/**
 * This class holds all the poll data, including the results and some other information.
 *
 * Created by robin on 2016-03-11.
 */
public class PollResults {
    private Instant pollFetchTime;
    private Instant pollFinishTime;
    private boolean finalResults;
    private ArrayList<Entry> entries;

    public PollResults() {
    }

    // Returns string to use ISO-8601 representation in the JSON
    public String getPollFetchTime() {
        return pollFetchTime.toString();
    }

    public void setPollFetchTime(Instant pollFetchTime) {
        this.pollFetchTime = pollFetchTime;
    }

    // Returns string to use ISO-8601 representation in the JSON
    public String getPollFinishTime() {
        return pollFinishTime.toString();
    }

    public void setPollFinishTime(Instant pollFinishTime) {
        this.pollFinishTime = pollFinishTime;
    }

    public boolean isFinalResults() {
        return finalResults;
    }

    public void setFinalResults(boolean finalResults) {
        this.finalResults = finalResults;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }
}
