import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ChatGPTQuestionBuilder {
    private final static String GPT_PATH = "src\\main\\resources\\chatGPT";

    public void buildQuestion(String category, String clue, Map<String, String> answers, String docNo)
            throws IOException {
        String prompt =
                "Given a clue and a list of articles that represent potential answers (the given document files), "
                        + "rank them, using the titles (file names), from best to worst answer for the given clue. Do"
                        + "not write any additional information, only the ranking for those 5 elements. \n"
                        + "The clue is :" + clue + "\n"
                        + "The category is:" + category + "\n";

        System.out.println(">>>>> For " + docNo + ": \n" + prompt +"\n>>>>> \n");
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            FileWriter myWriter = new FileWriter(new File(GPT_PATH, "Q" + docNo + entry.getKey() + ".txt"));
            myWriter.write(entry.getValue() + "\n");
            myWriter.close();
        }
    }
}
