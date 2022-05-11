import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Sentences {
    public static String[] breakSentences(String data) throws FileNotFoundException, IOException {
        try (InputStream modelIn = new FileInputStream("opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin")) {
            SentenceDetectorME mySentence = new SentenceDetectorME(new SentenceModel(modelIn));
            String[] sentences = mySentence.sentDetect(data);
            showSentences(sentences);
            return sentences;
        }
    }
    private static void showSentences(String[] sentences){
        System.out.println("Sentence Detection: " + Arrays.stream(sentences).collect(Collectors.joining(" | ")));
    }
}
