import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

public class ChatBot {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Enter 1 to train a model or 0 to use a ready-made one");
        DoccatModel model = getActions();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("##### You:");
            String userInput = scanner.nextLine();
            String[] sentences = breakSentences(userInput);
            String answer = "";
            boolean conversationComplete = false;
            for (String sentence : sentences) {
                String[] tokens = tokenizeSentence(sentence);
                String[] posTags = detectPOSTags(tokens);
                String[] lemmas = lemmatizeTokens(tokens, posTags);
                String category = detectCategory(model, lemmas);
                String response = respond(category);
                answer = answer + " " + response;
                if ("conversation_complete".equals(category)) {
                    conversationComplete = true;
                }
            }
            System.out.println("##### Chat Bot: " + answer);
            if (conversationComplete) {
                break;
            }
        }
    }

    //Sample interface
    private static DoccatModel getActions() throws IOException {
        Scanner scannerActions = new Scanner(System.in);
        int val = scannerActions.nextInt();
        DoccatModel model;
        if (val == 1) {   //Обучение модели
            return model = trainCategorizerModel();
        } else if (val == 0) {//Использование готовой модели
            return model = getModelFile("modelTest1.bin");
        } else return null;
    }

    //Чтение модели
    private static DoccatModel getModelFile(String modelPath) {
        DoccatModel  model = null;
        try {
            try (InputStream modelIn = new FileInputStream(modelPath)) {
                model = new DoccatModel (modelIn);
                return model;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    //Список ответов по категориям, нужно сделать тоже модель, только из ответов?
    private static String respond(String category){
        String[] greetings = {"Hello, how can I help you?","Hi","Hello"};
        String[] conversation_continue = {"What else can I help you with?"};
        String[] choice = {"Hip hop","rock","pop","jazz","indie","rap"};
        String[] conversation_complete = {"Nice chatting with you. Bye.", "bye"};

        if (category.equals("greetings")) return greetings[(int)(Math.random()*greetings.length)];
            else if (category.equals("conversation_continue")) return conversation_continue[(int)(Math.random()*conversation_continue.length)];
            else if (category.equals("choice")) return choice[(int)(Math.random()*choice.length)];
            else if (category.equals("conversation_complete")) return conversation_complete[(int)(Math.random()*conversation_complete.length)];
            else return "Sorry, I don't understand...";
    }

    //Обучение модели
    private static DoccatModel trainCategorizerModel() throws FileNotFoundException, IOException {
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("categories.txt"));
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

        DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });

        TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, 0);

        DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, factory);

        File output = new File("modelTest1.bin");
        FileOutputStream outputStream = new FileOutputStream(output);
        model.serialize(outputStream);
        return model;
    }

    //выбор подходящей категории
    private static String detectCategory(DoccatModel model, String[] finalTokens) throws IOException {
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
        double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalTokens);
        String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);

        System.out.println("Category: " + category);
        return category;
    }

    private static String[] breakSentences(String data) throws FileNotFoundException, IOException {
        try (InputStream modelIn = new FileInputStream("opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin")) {

            SentenceDetectorME myCategorizer = new SentenceDetectorME(new SentenceModel(modelIn));

            String[] sentences = myCategorizer.sentDetect(data);
            System.out.println("Sentence Detection: " + Arrays.stream(sentences).collect(Collectors.joining(" | ")));

            return sentences;
        }
    }


    private static String[] tokenizeSentence(String sentence) throws FileNotFoundException, IOException {
        try (InputStream modelIn = new FileInputStream("opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin")) {

            TokenizerME myCategorizer = new TokenizerME(new TokenizerModel(modelIn));

            String[] tokens = myCategorizer.tokenize(sentence);
            System.out.println("Tokenizer : " + Arrays.stream(tokens).collect(Collectors.joining(" | ")));

            return tokens;
        }
    }

    private static String[] detectPOSTags(String[] tokens) throws IOException {
        try (InputStream modelIn = new FileInputStream("en-pos-maxent.bin")) {

            POSTaggerME myCategorizer = new POSTaggerME(new POSModel(modelIn));

            String[] posTokens = myCategorizer.tag(tokens);
            System.out.println("POS Tags : " + Arrays.stream(posTokens).collect(Collectors.joining(" | ")));

            return posTokens;
        }
    }

    private static String[] lemmatizeTokens(String[] tokens, String[] posTags) throws InvalidFormatException, IOException {
        try (InputStream modelIn = new FileInputStream("en-lemmatizer.bin")) {

            LemmatizerME myCategorizer = new LemmatizerME(new LemmatizerModel(modelIn));
            String[] lemmaTokens = myCategorizer.lemmatize(tokens, posTags);
            System.out.println("Lemmatizer : " + Arrays.stream(lemmaTokens).collect(Collectors.joining(" | ")));

            return lemmaTokens;
        }
    }
}
