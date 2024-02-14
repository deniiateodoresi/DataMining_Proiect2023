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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class SearchBuilder {

    private final static String INDEX_PATH = "src\\main\\resources\\wiki-index";
    private final static String QUESTIONS_PATH = "src\\main\\resources\\questions.txt";
    private final static String RANKS_PATH = "src\\main\\resources\\ranks.txt";

    private final static ChatGPTQuestionBuilder chatGPTQuestionBuilder = new ChatGPTQuestionBuilder();
    private static FileWriter promptWriter;

    public static void main(String[] args) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        FSDirectory reader = FSDirectory.open(Paths.get(INDEX_PATH));
        DirectoryReader directoryReader = DirectoryReader.open(reader);

        IndexSearcher searcher = new IndexSearcher(directoryReader);
        QueryParser parser = new QueryParser("content", analyzer);

        promptWriter = new FileWriter(RANKS_PATH, true);

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

                List<String> processedAnswers = Utils.processAnswers(answer);
                TopDocs results = query(parser, searcher, category.trim() + " " + clue, directoryReader.maxDoc());

                int currentRank = getRank(category, clue, processedAnswers, searcher, results, nrQuestions);
                if (currentRank != -1) {
                    ranks.add(currentRank);
                } else {
                    ranks.add(defaultRank);
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        promptWriter.close();

        MetricsHelper.computeMetrics(ranks, nrQuestions);
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
                break;
            }

            i++;
        }

        if (rank > 1 && rank <= 5) {
            chatGPTQuestionBuilder.buildQuestion(category, clue, mapGPT, String.valueOf(nrQuestions));
            System.out.println("Question: " + nrQuestions + " Rank: " + rank);
        }

        promptWriter.append(String.valueOf(nrQuestions)).append(":").append(String.valueOf(rank)).append("\n");

        return rank;
    }
}
