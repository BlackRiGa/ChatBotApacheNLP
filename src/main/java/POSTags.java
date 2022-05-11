import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class POSTags {
    public static String[] detectPOSTags(String[] tokens) throws IOException {
        try (InputStream modelIn = new FileInputStream("en-pos-maxent.bin")) {
            POSTaggerME myTags = new POSTaggerME(new POSModel(modelIn));
            String[] posTokens = myTags.tag(tokens);
            showPOSTags(posTokens);
            return posTokens;
        }
    }
    private static void showPOSTags(String[] posTokens){
        System.out.println("POS Tags : " + Arrays.stream(posTokens).collect(Collectors.joining(" | ")));
    }
}
