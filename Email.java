import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Email {
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

int Id;
String rawText;
int TrueSpam;
int SpamGuess;
int NumberCount;
int UrlCount;
int WordCount;

public Email(String rText, int id){

    setId(id);
    setrawText(rText);
    int trues = Integer.parseInt(String.valueOf(rawText.charAt(rawText.length()-1)));
    setTrueSpam(trues);

    setWordCount(rawText.split("\\s+").length);
    setNumberCount(rawText.replaceAll("[^0-9]", "").length());
    setUrlCount(rawText.split("URL").length - 1);


}

public String getrawText(){return rawText;}
public int getId(){return Id;}
public int getSpamGuess(){return SpamGuess;}
public int getTrueSpam(){return TrueSpam;}
public int getWordCount(){return WordCount;}
public int getNumberCount(){return NumberCount;}

public void setrawText(String s){rawText = s;}

public void setId(int i){Id = i;}
public void setSpamGuess(int i){SpamGuess = i;}
public void setTrueSpam(int i){TrueSpam = i;}
public void setUrlCount(int i){UrlCount = i;}
public void setWordCount(int i){WordCount = i;}
public void setNumberCount(int i){NumberCount = i;}

}
