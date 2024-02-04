import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;

public class Utils {

    private static final Lemmatizer lemmatizer = new Lemmatizer();

    public static String normalize(String input) throws IOException {
        input = input.toLowerCase();
        input = removeSpecialChars(input);
        input = input.strip();

        StandardTokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(new StringReader(input));

        // Create a stop filter, using the English stop words set (more details in the documentation)
        TokenStream tokenStream = new StopFilter(tokenizer, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);

        // Extract the tokens after stop words removal
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        StringBuilder result = new StringBuilder();
        while (tokenStream.incrementToken()) {
            result.append(charTermAttribute.toString()).append(" ");
        }

        tokenStream.end();
        tokenStream.close();

        return lemmatizer.lemmatize(result.toString());
    }

    private static String removeSpecialChars(String input) {
        return input.replace(".", "")
                .replace(",", "")
                .replace(":", "")
                .replace(";", "");
    }
}
