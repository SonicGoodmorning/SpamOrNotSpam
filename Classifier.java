import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Classifier {
/*

- Loop through and parse the CSV, each iteration of the loop will create a email object
- For each created email object from the loop its iD, rawText, and TrueSpam fields will be created when looped through
- iD is so we can track which exact email we are checking and if it passes its tests
- rawText is for our spam classification, which we will use to find the characteristics of spam emails (Will be mentioned later)
- TrueSpam tells us if its actually spam or not so then we can compare later with our SpamGuess.
- Based on if its TrueSpam it will be placed in the NotSpamEmail/SpamEmail ArrayList<>, for later use.
- After all of this the Classifier class will run methods which we find to be characteristics of spam emails
- After finding these characteristics we will also look for spam words which will be added to our SpamWords ArrayList<>
- SpamWords ArrayList<> will then be used once again to check the emails again to see if it uses the likely words from spam emails.
- After all of this we will then TestAccuracy by sending in our ArrayList<> of Emails which hold the TrueSpam and GuessSpam fields and then compare
- Then returning a accuracy rate of all of this
*/



    public List<Email> Emails = new ArrayList<>();

    public List<Email> SpamEmails = new ArrayList<>();
    public List<Email> NotSpamEmails = new ArrayList<>();
    
    public List<String> SpamWords = new ArrayList<>();
    public List<String> StopWords = new ArrayList<>();

    public Classifier() throws FileNotFoundException {
        Scanner scan = new Scanner(new File("spam_or_not_spam.csv"));
        scan.nextLine();

        int id = 0;
        while(scan.hasNextLine()){
            String email = scan.nextLine();
            Email e = new Email(email, id++);
            Emails.add(e);

            if (e.getTrueSpam() == 1)
                SpamEmails.add(e);
            else
                NotSpamEmails.add(e);
        }

    }

    public static void main(String[] args) throws FileNotFoundException {

        Classifier c = new Classifier();
    }
}
