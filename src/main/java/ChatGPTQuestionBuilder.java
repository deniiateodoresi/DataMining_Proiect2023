import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ChatGPTQuestionBuilder {
    private final static String GPT_PATH = "src\\main\\resources\\chatGPT\\articles";
    private final static String GPT_PROMPTS_FILE = "src\\main\\resources\\chatGPT\\prompts.txt";

    public ChatGPTQuestionBuilder() {
        FileWriter promptWriter;
        try {
            promptWriter = new FileWriter(GPT_PROMPTS_FILE);
            promptWriter.write("");
            promptWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void buildQuestion(String category, String clue, Map<String, String> answers, String docNo)
            throws IOException {
        String prompt =
                "Given a clue and a list of articles that represent potential answers (the given document files), "
                        + "rank them, using the titles (file names), from best to worst answer for the given clue. Do"
                        + "not write any additional information, only the ranking for those 5 elements. \n"
                        + "The clue is :" + clue + "\n"
                        + "The category is:" + category + "\n";

        FileWriter promptWriter = new FileWriter(GPT_PROMPTS_FILE, true);
        promptWriter.append(">>>>> Prompt Question ").append(docNo).append(": \n").append(prompt).append(">>>>>\n\n");
        promptWriter.close();

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            FileWriter myWriter = new FileWriter(new File(GPT_PATH, "Q" + docNo + entry.getKey() + ".txt"));
            myWriter.write(entry.getValue() + "\n");
            myWriter.close();
        }
    }
}
