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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchBuilder {

    private final static String INDEX_PATH = "src\\main\\resources\\wiki-index";
    private final static String QUESTIONS_PATH = "src\\main\\resources\\questions.txt";

    public static void main(String[] args) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        FSDirectory reader = FSDirectory.open(Paths.get(INDEX_PATH));
        DirectoryReader directoryReader = DirectoryReader.open(reader);

        IndexSearcher searcher = new IndexSearcher(directoryReader);
        QueryParser parser = new QueryParser("content", analyzer);

        try (BufferedReader buffReader = new BufferedReader(new FileReader(QUESTIONS_PATH))) {
            String line;
            while ((line = buffReader.readLine()) != null) {
                String category = line;

                line = buffReader.readLine();
                String clue = line;

                line = buffReader.readLine();
                String answer = line;

                line = buffReader.readLine();

                System.out.println("\n>>> Query for: " + answer);

                List<String> processedAnswers = processAnswers(answer);
                TopDocs results = query(parser, searcher, category.trim() + " " + clue, directoryReader.maxDoc());
                checkRank(processedAnswers, searcher, results);

                System.out.println(">>> End of Query");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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

    private static void checkRank(List<String> answers, IndexSearcher searcher, TopDocs results) throws IOException {
        int i = 1;
        int rank;
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);

            String title = doc.get("title");
            if (answers.contains(title.toLowerCase())) {
                rank = i;
                System.out.println(title + " ---> " + rank);
                break;
            }

            i++;
        }
    }
}
