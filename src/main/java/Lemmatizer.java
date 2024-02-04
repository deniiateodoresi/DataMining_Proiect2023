import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class Lemmatizer {

    private final StanfordCoreNLP pipeline;

    public Lemmatizer() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,pos,lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    public String lemmatize(String input) {
        CoreDocument document = pipeline.processToCoreDocument(input);
        StringBuilder result = new StringBuilder();
        for (CoreLabel tok : document.tokens()) {
            result.append(tok.lemma());
        }
        return result.toString();
    }
}
