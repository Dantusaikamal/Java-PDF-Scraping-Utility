package com;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class pdfextractor {

    public static void main(String[] args) {
        String pdfPath = "C:/Users/dantu/eclipse-workspace/PDF-Extractor/src/com/test2.pdf";
        String url = "https://www.dantusaikamal.codes/humans.txt";

        try {
            String pdfText = extractTextFromPDF(pdfPath);
            String webText = extractTextFromWeb(url);

            int pdfWordCount = countWords(pdfText);
            int webWordCount = countWords(webText);
            int matchingCount = countMatchingWords(pdfText, webText);

            System.out.println("Number of words in the PDF: " + pdfWordCount);
            System.out.println("Number of words in the web page: " + webWordCount);
            System.out.println("Matching count: " + matchingCount);

            Set<String> nonMatchingWords = getNonMatchingWords(pdfText, webText);
            System.out.println("Non-matching words: " + nonMatchingWords);
            
            generateReport(pdfPath, url, pdfWordCount, webWordCount, matchingCount, nonMatchingWords);
            System.out.println("Report generated successfully.");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred: " + e.getMessage());
        }
        
    }

    private static String extractTextFromPDF(String pdfPath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static String extractTextFromWeb(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.text();
    }

    private static int countWords(String text) {
        String[] words = tokenize(text);
        return words.length;
    }

    private static int countMatchingWords(String text1, String text2) {
        Set<String> set1 = new HashSet<>(Arrays.asList(tokenize(text1)));
        Set<String> set2 = new HashSet<>(Arrays.asList(tokenize(text2)));
        set1.retainAll(set2);
        return set1.size();
    }

    private static Set<String> getNonMatchingWords(String text1, String text2) {
        Set<String> set1 = new LinkedHashSet<>(Arrays.asList(tokenize(text1)));
        Set<String> set2 = new LinkedHashSet<>(Arrays.asList(tokenize(text2)));

        Set<String> nonMatching = new LinkedHashSet<>();
        for (String word : set1) {
            if (!set2.contains(word)) {
                nonMatching.add(word);
            }
        }
        for (String word : set2) {
            if (!set1.contains(word)) {
                nonMatching.add(word);
            }
        }
        return nonMatching;
    }
    private static String[] tokenize(String text) {
        return text.toLowerCase().replaceAll("[^a-z0-9\\s]", "").split("\\s+");
    }
    private static void generateReport(String pdfPath, String url, int pdfWordCount, int webWordCount, int matchingCount, Set<String> nonMatchingWords) throws IOException {
        String reportFileName = "TextComparisonReport.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFileName))) {
            writer.write("Text Comparison Report\n");
            writer.write("=====================\n\n");
            writer.write("PDF File: " + pdfPath + "\n");
            writer.write("Web Page: " + url + "\n\n");
            writer.write("Number of words in the PDF: " + pdfWordCount + "\n");
            writer.write("Number of words in the web page: " + webWordCount + "\n");
            writer.write("Matching word count: " + matchingCount + "\n\n");
            writer.write("Non-matching words:\n");
            for (String word : nonMatchingWords) {
                writer.write(word + "\n");
            }
        }
    }
}

