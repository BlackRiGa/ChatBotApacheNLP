import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Tokenize {
    public static String[] tokenizeSentence(String sentence) throws FileNotFoundException, IOException {
        try (InputStream modelIn = new FileInputStream("opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin")) {
            TokenizerME myTokens = new TokenizerME(new TokenizerModel(modelIn));
            String[] tokens = myTokens.tokenize(sentence);
            showTokens(tokens);
            return tokens;
        }
    }
    private static void showTokens(String[] tokens){
        System.out.println("Tokenizer : " + Arrays.stream(tokens).collect(Collectors.joining(" | ")));
    }
}
