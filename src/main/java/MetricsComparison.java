import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MetricsComparison {

    private final static String QUESTIONS_PATH = "src\\main\\resources\\questions.txt";
    private final static String RANKS_PATH = "src\\main\\resources\\ranks.txt";
    private final static String RESULTS_PATH = "src\\main\\resources\\chatGPT\\results.txt";

    public static void main(String[] args) throws IOException {
        Map<Integer, Integer> searchBuilderRanks = getAnswers();
        Map<Integer, LinkedList<String>> gptResults = getGPTResults();

        System.out.println("Initial metrics");
        MetricsHelper.computeMetrics(searchBuilderRanks.values().stream().toList(), 100);

        BufferedReader buffReader = new BufferedReader(new FileReader(QUESTIONS_PATH));
        int nrQuestions = 0;
        String line;
        while ((line = buffReader.readLine()) != null) {
            ++nrQuestions;

            line = buffReader.readLine();
            line = buffReader.readLine();
            String answer = line;
            line = buffReader.readLine();

            if (gptResults.containsKey(nrQuestions)) {
                List<String> processedAnswers = Utils.processAnswers(answer).stream().map(String::strip).toList();
                int newRank = getGPTRank(gptResults.get(nrQuestions), processedAnswers);
                searchBuilderRanks.put(nrQuestions, newRank);
            }
        }

        System.out.println("\nAfter GPT metrics");
        MetricsHelper.computeMetrics(searchBuilderRanks.values().stream().toList(), nrQuestions);
    }

    private static Map<Integer, Integer> getAnswers() throws IOException {
        Map<Integer, Integer> searchBuilderRanks = new HashMap<>();

        BufferedReader buffReader = new BufferedReader(new FileReader(RANKS_PATH));
        String line;
        while ((line = buffReader.readLine()) != null) {
            String[] fileValues = line.split(":");
            Integer question = Integer.valueOf(fileValues[0]);
            Integer rank = Integer.valueOf(fileValues[1]);

            searchBuilderRanks.put(question, rank);
        }

        return searchBuilderRanks;
    }

    private static Map<Integer, LinkedList<String>> getGPTResults() throws IOException {
        Map<Integer, LinkedList<String>> gptResults = new HashMap<>();

        BufferedReader buffReader = new BufferedReader(new FileReader(RESULTS_PATH));
        String line;
        while ((line = buffReader.readLine()) != null) {
            String[] fileValues = line.split(":");
            Integer question = Integer.valueOf(fileValues[0]);
            int rank = Integer.parseInt(fileValues[1]);
            LinkedList<String> topAnswers = new LinkedList<>();

            while (rank > 0) {
                line = buffReader.readLine();
                topAnswers.add(line);
                rank--;
            }

            line = buffReader.readLine();

            gptResults.put(question, topAnswers);
        }

        return gptResults;
    }

    private static Integer getGPTRank(List<String> results, List<String> answers) {
        int i = 1;

        for (String title : results) {
            if (answers.contains(title)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
