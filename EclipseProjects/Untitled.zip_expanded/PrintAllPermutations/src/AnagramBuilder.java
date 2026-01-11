import java.util.ArrayList;
import java.util.List;

public class AnagramBuilder {

    public static List<String> buildAnagrams(String s) {
        if (s.length() == 1) {
            // Base case when the length of s = 1
            List<String> baseCase = new ArrayList<>();
            baseCase.add(s);
            return baseCase;
        }

        List<String> result = new ArrayList<>();
        for (int ind = 0; ind < s.length(); ind++) {
            // call it recursively for the remaining string
            List<String> returnList = buildAnagrams(s.substring(0, ind) + s.substring(ind + 1));

            // add the current character to the beginning of the returned list
            for (String x : returnList) {
                result.add(s.charAt(ind) + x);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String input = "abc";
        List<String> anagrams = buildAnagrams(input);

        System.out.println("All anagrams of " + input + ":");
        for (String anagram : anagrams) {
            System.out.println(anagram);
        }
    }
}
