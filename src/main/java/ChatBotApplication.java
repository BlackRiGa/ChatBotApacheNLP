import java.io.*;
import java.util.Scanner;
import opennlp.tools.doccat.DoccatModel;

public class ChatBotApplication {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter 1 to train a model or 0 to use a ready-made one");
        runChatBot();
    }

    private static void runChatBot() throws IOException {
        DoccatModel model = getActions();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("##### You:");
            String userInput = scanner.nextLine();
            String[] sentences = Sentences.breakSentences(userInput);
            String answer = "";
            boolean conversationComplete = false;
            for (String sentence : sentences) {
                String[] tokens = Tokenize.tokenizeSentence(sentence);
                String[] posTags = POSTags.detectPOSTags(tokens);
                String[] lemmas = Lemmitize.lemmatizeTokens(tokens, posTags);
                String category = TrainModel.detectCategory(model, lemmas);
                String response = TrainModel.respond(category);
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
            return model = TrainModel.trainCategorizerModel();
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
}

