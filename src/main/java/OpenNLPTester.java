import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenNLPTester {

    private final Parser parser;
    private final SentenceDetectorME sentence_detector;

    public OpenNLPTester() throws IOException {
        parser = ParserFactory.create(new ParserModel(new FileInputStream("E:\\6семестр\\Java\\ChatBotApacheNLP\\en-parser-chunking.bin")));
        sentence_detector = new SentenceDetectorME(new SentenceModel(new FileInputStream("E:\\6семестр\\Java\\ChatBotApacheNLP\\opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin")));
    }

    // Given a single sentence, returns its tokens
    public String[] getTokens(String sentence) {
        return SimpleTokenizer.INSTANCE.tokenize(sentence);
    }

    // Given some text, returns the sentences in the text
    public String[] getSentences(String text) {
        return sentence_detector.sentDetect(text);
    }

    // Given a single sentence, returns its parse tree(s) depending on the
    // number of parses requested
    public Parse[] parseSentence(String sentence, int num_parses) {
        return ParserTool.parseLine(sentence, parser, SimpleTokenizer.INSTANCE, num_parses);
    }

    // Given some text comprising multiple sentences,
    // returns the resulting parse trees
    public List<Parse[]> parseText(String text, int num_parses) {
        ArrayList<Parse[]> parses = new ArrayList<>();

        for(String sentence : getSentences(text))
            parses.add(parseSentence(sentence, num_parses));
        return parses;
    }

    // Print a parse tree on the console, including probability if asked for
    public static void printParseTree(Parse p, boolean include_probability) {
        StringBuffer sbuf = new StringBuffer();
        p.show(sbuf);
        if(include_probability)
            System.out.println(sbuf + " => " + p.getProb());
        else
            System.out.println(sbuf);
    }

    // Convenience function for parsing text and printing the parse tree
    // Parse trees are sorted in descending order of probability
    public static void testParse(OpenNLPTester tester, String sentence, int num_parses, boolean include_probability) {
        Parse[] parses = tester.parseSentence(sentence, num_parses);
        Arrays.sort(parses, (e1, e2) ->  Double.compare(e2.getProb(), e1.getProb()));

        for(Parse p : parses)
            printParseTree(p, include_probability);
    }

    public static void main(String[] args) {
        try {
            OpenNLPTester tester = new OpenNLPTester();

            String example_sentence = "I like chat bot.";

            // Case 1: Get 1 parse tree and print it with probability
             testParse(tester, example_sentence, 1, true);

            // Case 2: Print 10 parse trees with probabilities in descending order
            // testParse(tester, example_sentence, 10, true);

            // Case 3: Print 25 parse trees with probabilities in descending order
//            testParse(tester, example_sentence, 25, true);
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
}
