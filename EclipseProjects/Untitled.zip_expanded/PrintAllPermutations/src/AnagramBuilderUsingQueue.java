import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class AnagramBuilderUsingQueue {

    public static List<String> breadthFirstAnagrams(String word) {
        // Convert word to individual letters
        char[] letters = word.toCharArray();

        // We're going to build a queue that will start off with each letter.
        // Then we'll loop through the queue doing the following:
        // 1. Poll off the "stub" at the front of the queue
        // 2. Figure out what letters are not yet used in the stub
        // 3. For each letter not yet used in the stub, append it to the end of the stub and push that to the end of the queue
        // Do this until we reach the first word that is as long as the original word.

        Queue<String> queue = new LinkedList<>();
        for (char letter : letters) {
            queue.add(String.valueOf(letter));
        }

        int queueLength = queue.peek().length();
//        System.out.println(queueLength);
        int wordLength = word.length();

        while (queueLength != wordLength) {
            String stub = queue.poll();

            // Grab the full list of letters and then remove a single instance of each letter that is already used.
            List<String> newLetters = new ArrayList<>();
            for (char letter : letters) {
                newLetters.add(String.valueOf(letter));
            }
            for (char letter : stub.toCharArray()) {
                newLetters.remove(String.valueOf(letter));
            }

            // Now we have a stub and a list of letters that can be appended to the stub. Append each letter and
            // put the new stubs back on the queue
            for (String letter : newLetters) {
                queue.add(stub + letter);
            }

            if (!queue.isEmpty()) {
                queueLength = queue.peek().length();
            }
        }

        // Use a set to ensure uniqueness and convert back to a list
        return new ArrayList<>(new HashSet<>(queue));
    }

    public static void main(String[] args) {
        String word = "aaa";
        List<String> anagrams = breadthFirstAnagrams(word);

        System.out.println("All anagrams of " + word + ":");
        for (String anagram : anagrams) {
            System.out.println(anagram);
        }
    }
}
