import opennlp.tools.doccat.*;
import opennlp.tools.util.*;
import opennlp.tools.util.model.ModelUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TrainModel {
    //выбор подходящей категории
    public static String detectCategory(DoccatModel model, String[] finalTokens) throws IOException {
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
        double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalTokens);
        String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
        System.out.println("Category: " + category);
        return category;
    }
    //Обучение модели
    public static DoccatModel trainCategorizerModel() throws FileNotFoundException, IOException {
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
    //Список ответов по категориям
    public static String respond(String category){
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
}
