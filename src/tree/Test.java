package tree;

/**
 * Created by mayezhou on 16/6/23.
 */
public class Test {
    public static <E extends Comparable<E>> E max(E o1, E o2) {
        if (o1.compareTo(o2) > 0) {
            return o1;
        } else {
            return o2;
        }
    }

    public static void main(String[] args) {
        System.out.println(Test.<String>max("a", "b"));
    }
}
