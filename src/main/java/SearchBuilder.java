import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class SearchBuilder {

    private final static String INDEX_PATH = "src\\main\\resources\\wiki-index";
    private final static String QUESTIONS_PATH = "src\\main\\resources\\questions.txt";

    private final static ChatGPTQuestionBuilder chatGPTQuestionBuilder = new ChatGPTQuestionBuilder();

    public static void main(String[] args) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        FSDirectory reader = FSDirectory.open(Paths.get(INDEX_PATH));
        DirectoryReader directoryReader = DirectoryReader.open(reader);

        IndexSearcher searcher = new IndexSearcher(directoryReader);
        QueryParser parser = new QueryParser("content", analyzer);

        int nrQuestions = 0;
        int defaultRank = directoryReader.numDocs();
        List<Integer> ranks = new ArrayList<>();

        try (BufferedReader buffReader = new BufferedReader(new FileReader(QUESTIONS_PATH))) {
            String line;
            while ((line = buffReader.readLine()) != null) {
                ++nrQuestions;
                String category = line;

                line = buffReader.readLine();
                String clue = line;

                line = buffReader.readLine();
                String answer = line;

                line = buffReader.readLine();

//                System.out.println("\n>>> Query for: " + answer);

                List<String> processedAnswers = processAnswers(answer);
                TopDocs results = query(parser, searcher, category.trim() + " " + clue, directoryReader.maxDoc());

                int currentRank = getRank(category, clue, processedAnswers, searcher, results, nrQuestions);
                if (currentRank != -1) {
                    ranks.add(currentRank);
                } else {
                    ranks.add(defaultRank);
                }

//                System.out.println(">>> End of Query");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        computeMetrics(ranks, nrQuestions);
    }

    private static List<String> processAnswers(String answers) {
        return Arrays.stream(answers.split("\\|"))
                .map(String::toLowerCase)
                .map(ans -> {
                    try {
                        return Utils.normalize(ans);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private static TopDocs query(QueryParser parser, IndexSearcher searcher, String clue, int docCount) throws IOException, ParseException {
        clue = Utils.normalize(clue);
        Query query = parser.parse(clue);
        return searcher.search(query, docCount);
    }

    private static Integer getRank(String category, String clue, List<String> answers, IndexSearcher searcher, TopDocs results, int nrQuestions) throws IOException {
        int i = 1;
        int rank = -1;

        Map<String, String> mapGPT = new HashMap<>();
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);

            String title = doc.get("title");
            String content = doc.get("content");
            mapGPT.put(title, content);

            if (answers.contains(title.toLowerCase())) {
                rank = i;
//                System.out.println(title + " ---> " + rank);
                break;
            }

            i++;
        }

        if (rank == 5) {
            chatGPTQuestionBuilder.buildQuestion(category, clue, mapGPT, String.valueOf(nrQuestions));
        }
        return rank;
    }

    private static void computeMetrics(List<Integer> ranks, int nrQuestions) {
        double pAt1 = calculatePrecision(ranks, nrQuestions, 1);
        double pAt5 = calculatePrecision(ranks, nrQuestions, 5);
        double MRR = calculateMeanReciprocalRank(ranks, nrQuestions);

        System.out.printf("Precision at 1: %f\n", pAt1);
        System.out.printf("Precision at 5: %f\n", pAt5);
        System.out.printf("Mean Reciprocal Rank: %f\n", MRR);
    }

    private static double calculatePrecision(List<Integer> ranks, int nrQuestions, int k) {
        long nrCorrectItems = ranks.stream().filter(rank -> rank > 0 && rank <= k).count();
        return (double) nrCorrectItems / nrQuestions;
    }

    private static double calculateMeanReciprocalRank(List<Integer> ranks, int nrQuestions) {
        double sumMRR = ranks.stream().mapToDouble(rank -> 1.0 / rank).sum();
        return sumMRR / nrQuestions;
    }
}
