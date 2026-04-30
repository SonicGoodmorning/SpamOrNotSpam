import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Classifier {
/*

- Loop through and parse the CSV, each iteration of the loop will create a email object ✅
- For each created email object from the loop its iD, rawText, and TrueSpam fields will be created when looped through ✅
- iD is so we can track which exact email we are checking and if it passes its tests ✅
- rawText is for our spam classification, which we will use to find the characteristics of spam emails (Will be mentioned later) ✅
- TrueSpam tells us if its actually spam or not so then we can compare later with our SpamGuess. ✅
- Based on if its TrueSpam it will be placed in the NotSpamEmail/SpamEmail ArrayList<>, for later use.✅
- After all of this the Classifier class will run methods which we find to be characteristics of spam emails
- After finding these characteristics we will also look for spam words which will be added to our SpamWords ArrayList<>
- SpamWords ArrayList<> will then be used once again to check the emails again to see if it uses the likely words from spam emails.
- After all of this we will then TestAccuracy by sending in our ArrayList<> of Emails which hold the TrueSpam and GuessSpam fields and then compare
- Then returning a accuracy rate of all of this
*/

    public ArrayList<Email> Emails = new ArrayList<>();

    public ArrayList<Email> SpamEmails = new ArrayList<>();
    public ArrayList<Email> NotSpamEmails = new ArrayList<>();
    
    public ArrayList<String> SpamWords = new ArrayList<>();
    public ArrayList<String> StopWords = new ArrayList<>();

    public Classifier() throws FileNotFoundException {
        Scanner scan = new Scanner(new File("spam_or_not_spam.csv"));
        scan.nextLine();

        int id = 0;

        // extract info from CSV
        while(scan.hasNextLine()){
            String email = scan.nextLine();
            Email e = new Email(email, id++);
            Emails.add(e);

            // adds to the spam/not spam lists
            if (e.getTrueSpam() == 1)
                SpamEmails.add(e);
            else
                NotSpamEmails.add(e);
            
            // System.out.println(e.toString());
            // for testing ^^
        }

    }

    // thinking of using a hashmap to count the frequency of words
    // by doing this we can also see what words will be words we just ignore (the, to, and, of, etc)
    // we will then just ignore those words to get to the naughty words that are actually spam
    // these stopwords will be collected in a separate list, I dont think we can do this dynamically on the fly with code, 
    // unless we just assume the first top 10 words are just stop words, so we may have to do a static stop word list idk if cui will 
    // like that but we will find out

    public void SpamWords(ArrayList<Email> SpamEmails, ArrayList<Email> NotSpamEmails) {
        // find common words in spam emails and add to SpamWords list
        Map<String, Integer> wordCount = new HashMap<>();
        for (Email e : SpamEmails) {
            String[] words = e.getrawText().split("\\s+");
            for (String word : words) {
                word = word.toLowerCase();
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        // find the most common words.
        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordCount.entrySet());
        sortedWords.sort((a, b) -> b.getValue() - a.getValue());

        // filter out StopWords (the, to, and, of, etc)
        List<String> stopWords = Arrays.asList("the", "to", "and", "of", "in", "a",
        "is", "it", "that", "on", "you", "for", "with", "as", "this", "but", "be", "are",
        "not", "or", "if", "at", "by", "an", "be", "from", "i", "have");

        sortedWords.removeIf(entry -> stopWords.contains(entry.getKey()));

//Used to refine stop words list
//        System.out.println("Spam words after comparison filter:");
//        for (int i = 0; i < Math.min(7, sortedWords.size()); i++) {
//            System.out.println(sortedWords.get(i).getKey() + " : " + sortedWords.get(i).getValue());
//        }

        // add the top 5 most common words to the SpamWords list and also mention their frequencies
        for (int i = 0; i < Math.min(7, sortedWords.size()); i++) {
            Map.Entry<String, Integer> entry = sortedWords.get(i);
            SpamWords.add(entry.getKey());
        }

    }
    // Classifies all emails using SpamWords list +  features
    public void ClassifyEmails(ArrayList<Email> emails) {
        for (Email e : emails) {
            double score = 0;
            String[] words = e.getrawText().toLowerCase().split("\\s+");

            // Feature 1: Spam word hits (unique matches only)
            // Count how many unique spam words appear, not total occurrences
            // This prevents one repeated word from inflating the score
            Set<String> seenSpamHits = new HashSet<>();
            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                if (SpamWords.contains(word) && !seenSpamHits.contains(word)) {
                    seenSpamHits.add(word);
                    score += 1;
                }
            }

            //Feature 2: URL count
            if (e.UrlCount >= 3) score += 3;
            else if (e.UrlCount >= 1) score += 2;

            // Feature 3: Number density
            if (e.getWordCount() > 0) {
                double numberDensity = (double) e.getNumberCount() / e.getWordCount();
                if (numberDensity > 0.3) score += 2;
                else if (numberDensity > 0.15) score += 1;
            }



            // Feature 4: Very short emails
            if (e.getWordCount() < 20) score += 1;

            // Feature 5: Low unique word ratio indicates repetitive spam
            if (e.getWordCount() > 0) {
                double uniqueRatio = (double) e.getUniqueWordCount() / e.getWordCount();
                if (uniqueRatio < 0.15) score += 2;
                else if (uniqueRatio < 0.3) score += 1;
            }


            //Threshold
            if (score >= 6) {
                e.setSpamGuess(1);
            } else {
                e.setSpamGuess(0);
            }
        }
    }

    // Compares SpamGuess vs TrueSpam across all emails and prints results
    public void TestAccuracy(ArrayList<Email> emails) {
        int correct = 0;
        int truePositive = 0;   // guessed spam, actually spam
        int falsePositive = 0;  // guessed spam, actually not spam
        int trueNegative = 0;   // guessed not spam, actually not spam
        int falseNegative = 0;  // guessed not spam, actually spam

        for (Email e : emails) {
            boolean guessedSpam = e.getSpamGuess() == 1;
            boolean actuallySpam = e.getTrueSpam() == 1;

            if (guessedSpam && actuallySpam) {
                truePositive++;
                correct++;
            } else if (!guessedSpam && !actuallySpam) {
                trueNegative++;
                correct++;
            } else if (guessedSpam && !actuallySpam)  {
                falsePositive++;
            } else if (!guessedSpam && actuallySpam)  {
                falseNegative++;
            }
        }

        double accuracy = (double) correct / emails.size() * 100;

        System.out.println("=== Classifier Results ===");
        System.out.printf("Total Emails:    %d%n", emails.size());
        System.out.printf("Correct:         %d%n", correct);
        System.out.printf("Accuracy:        %.2f%%%n", accuracy);
        System.out.println("--------------------------");
        System.out.printf("True Positives:  %d  (spam caught)%n", truePositive);
        System.out.printf("True Negatives:  %d  (ham correctly ignored)%n", trueNegative);
        System.out.printf("False Positives: %d  (ham marked as spam)%n", falsePositive);
        System.out.printf("False Negatives: %d  (spam that slipped through)%n", falseNegative);
    }

    public static void main(String[] args) throws FileNotFoundException {

        Classifier c = new Classifier();
        c.SpamWords(c.SpamEmails, c.NotSpamEmails);
        c.ClassifyEmails(c.Emails);
        c.TestAccuracy(c.Emails);

    }
}
