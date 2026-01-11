import java.util.Arrays;
import java.util.stream.Stream;

public class Java8StreamAPI2 {

    public static void main(String[] args) {

        String[][] data = new String[][]{{"a", "b"}, {"c", "d"}, {"e", "f"}};

        //Stream<String[]>
        Stream<String[]> temp = Arrays.stream(data);
//        System.out.println("------------------------------------------");
//        temp.forEach(System.out::println);
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//        temp.forEach(System.out::println);
//        System.out.println("++++++++++++++++++++++++++++++++++++++++++");

        //filter a stream of string[], and return a string[]?
        System.out.println("******************************************");
        Stream<String[]> stream = temp.filter(x -> "a".equals(x.toString()));

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        stream.forEach(System.out::println);
        System.out.println("##########################################");

    }

}