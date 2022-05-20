package com.anime.arena.tools;

/**
 * Created by gregorykelsie on 2018-11-25.
 */

public class TextFormater {
    /**
     * Return a formatted description String that has new lines in the correct
     * places so it doesn't overflow out of the text box.
     * @param description The description before formatting.
     * @return A String of the formatted description with new lines so it doesn't overflow out
     * of the text box.
     */
    public static String formatText(String description) {
        return formatText(description, 40.0);
    }

    public static String formatText(String description, double maxLengthValue) {
        String[] splitDescription = description.split(" ");
        String newDescription = "";
        double lineCurrentValue = 0;
        for (int i = 0; i < splitDescription.length; i++) {
            String [] splitNewLine = splitDescription[i].split("\n");
            String word = splitNewLine[0];
            if ((lineCurrentValue + 1 + getWordLengthValue(word)) <= maxLengthValue) {
                newDescription = newDescription + word + " ";
                lineCurrentValue = lineCurrentValue + 1 + getWordLengthValue(word);
            } else {
                newDescription = newDescription + "\n" + word + " ";
                lineCurrentValue = getWordLengthValue(word) + 1; //+1 for the space
            }
            for (int j = 1; j < splitNewLine.length; j++) {
                newDescription = newDescription + "\n" + splitNewLine[j] + " ";
                lineCurrentValue = getWordLengthValue(splitNewLine[j]) + 1;
            }
        }
        return newDescription;
    }

    public static double getWordLengthValue(String word) {
        double value = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == 'i' || word.charAt(i) == 'l' || word.charAt(i) == '!' ||
                    word.charAt(i) == '.') {
                value += 0.5;
            } else {
                value++;
            }
        }
        return value;
    }

    public static int getMoveXPosition(String word, int startingX) {
        double wordLengthValue = getWordLengthValue(word) - 1;
        return startingX - (int)Math.round(9 * wordLengthValue);
    }
}