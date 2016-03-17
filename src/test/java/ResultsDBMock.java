import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import model.Answer;
import model.Question;
import model.Results;

import java.time.Instant;
import java.util.ArrayList;

/**
 * Created by robin on 17/3/16.
 */
public class ResultsDBMock {

    private ArrayList<Results> results;

    public ResultsDBMock() {
        this.results = new ArrayList<>();

        ArrayList<String> keysList1 = new ArrayList<>();
        keysList1.add("111");
        keysList1.add("222");
        keysList1.add("333");
        keysList1.add("444");
        keysList1.add("555");
        keysList1.add("666");

        ArrayList<String> keysList2 = new ArrayList<>();
        keysList1.add("777");
        keysList1.add("888");
        keysList1.add("999");
        keysList1.add("101010");
        keysList1.add("111111");

        Answer ans1 = new Answer("Yes", keysList1.size(), keysList1);
        Answer ans2 = new Answer("No", keysList2.size(), keysList2);

        ArrayList<Answer> answers = new ArrayList<>();
        answers.add(ans1);
        answers.add(ans2);

        Question questions = new Question("Do you like cats?", answers);
        ArrayList questionsList = new ArrayList<>();
        questionsList.add(questions);

        Results res1 = new Results("testcollection", "Owner", "Cats", Instant.now(), Instant.now(), questionsList);

        this.results.add(res1);
    }

    public JsonObject getResults() {
        JsonObject obj = new JsonObject()
                .put("_id", "asd")
                .put("pollId", "testcollection")
                .put("owner", results.get(0).getOwner())
                .put("topic", results.get(0).getTopic())
                .put("pollStartTime", results.get(0).getPollFinishTime())
                .put("pollFinishTime", results.get(0).getPollFinishTime())
                .put("questions", results.get(0).getQuestions());

        return obj;
    }
}
