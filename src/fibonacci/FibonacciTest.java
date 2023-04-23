package fibonacci;
import java.lang.reflect.Array;
import java.util.Arrays;

public class FibonacciTest {
    public static void main(String[] args) {
        int[] results = Fibonacci.compute(10, Fibonacci.Method.RECURSIVE);
        int[] results2 = Fibonacci.compute(10, Fibonacci.Method.ITERATIVE);
        int[] results3 = Fibonacci.compute(10, Fibonacci.Method.DP);
        System.out.println(Arrays.toString(results));
        System.out.println(Arrays.toString(results2));
        System.out.println(Arrays.toString(results3));
    }
}
