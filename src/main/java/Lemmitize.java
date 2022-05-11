import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.util.InvalidFormatException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Lemmitize {
    public static String[] lemmatizeTokens(String[] tokens, String[] posTags) throws InvalidFormatException, IOException {
        try (InputStream modelIn = new FileInputStream("en-lemmatizer.bin")) {
            LemmatizerME myLemmatize = new LemmatizerME(new LemmatizerModel(modelIn));
            String[] lemmaTokens = myLemmatize.lemmatize(tokens, posTags);
            showLemmas(lemmaTokens);
            return lemmaTokens;
        }
    }
    private static void showLemmas(String[] lemmaTokens){
        System.out.println("Lemmatizer : " + Arrays.stream(lemmaTokens).collect(Collectors.joining(" | ")));
    }
}
