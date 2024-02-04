import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexBuilder {

    private final static String INDEX_PATH = "src\\main\\resources\\wiki-index";

    public static void main(String[] args) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        FSDirectory index = FSDirectory.open(Paths.get(INDEX_PATH));
        IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(analyzer));

        for (int i = 1; i <= 80; i++) {
            String filePath = "wiki-subset/enwiki-20140602-pages-articles.xml-00";
            if (i < 10) {
                filePath = filePath + "0" + i +".txt";
            } else {
                filePath = filePath + i +".txt";
            }

            ClassLoader classLoader = IndexBuilder.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(filePath);
            String data = readFromInputStream(inputStream);

            data = data.replaceAll("\\[\\[File:.*]]]]", "");
            data = data.replaceAll("\\[\\[File:.*]]", "");

            List<String> fileContents = new ArrayList<>();
            Matcher matcher = Pattern.compile("\\[\\[.*]]").matcher(data);
            int start = 0;

            while (matcher.find()) {
                if (matcher.start() > start) {
                    fileContents.add(data.substring(start, matcher.start()));
                }
                start = matcher.start();
            }

            if (start < data.length()) {
                fileContents.add(data.substring(start));
            }

            for (String article: fileContents){
                Document document = processArticle(article);
                writer.addDocument(document);
            }
        }

        writer.commit();
        writer.close();
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    private static Document processArticle(String article) throws IOException {
        Document document = new Document();

        int index = 0;
        while (article.charAt(index) != ']' && article.charAt(index + 1) != ']') {
            index++;
        }
        String title = article.substring(2, index + 1);
        String content = article.substring(index + 3);
        title = Utils.normalize(title);
        content = Utils.normalize(content);

        document.add(new StringField("title", title, Field.Store.YES));
        document.add(new TextField("content", content, Field.Store.YES));
        return document;
    }
}
