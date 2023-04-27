package fibonacci;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class FibonacciTest {
    public static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();
//    public static final int DATA_SIZE = 10_000_000;
    public static final int WARMUP = 5;
    public static final int TOTAL_TESTS = 10;
    public static long[] fibIterative(int n){
        // Compute the Fibonacci sequence sequentially
        long[] fib = new long[n + 1];
        fib[1] = 1;
        for (int i = 2; i <= n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
        return fib;
    }
    public static void main(String[] args) {

        int n = 30;
        System.out.println("Starting baseline recursive computation...");

        for (int i = 0; i < WARMUP; i++) {
            Fibonacci.compute(n, Fibonacci.Method.RECURSIVE);
        }

        long start = System.nanoTime();
        long[] result = new long[n+1];
        long[] result2 = new long[n+1];

        for (int i = 0; i < TOTAL_TESTS; i++) {
            result = Fibonacci.compute(n, Fibonacci.Method.RECURSIVE);
        }

        long stop = System.nanoTime();

        System.out.println("That took " + (stop - start) / 1_000_000 + "ms");

        System.out.println("\nStarting parallel computation with " + POOL_SIZE + " processors...");

        ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);

        for (int i = 0; i < WARMUP; i++) {
            FibonacciTask ft = new FibonacciTask(n);
            result2 = pool.invoke(ft);
        }
        System.out.println("Warmup finished\n");

        start = System.nanoTime();

        for (int i = 0; i < TOTAL_TESTS; i++) {
            FibonacciTask ft = new FibonacciTask(n);
            result2 = pool.invoke(ft);
        }

        stop = System.nanoTime();

//        System.out.println(Arrays.toString(result));
//        System.out.println(Arrays.toString(result2));
        System.out.println(Arrays.equals(result, result2)?"passed":"failed");

        System.out.println("That took " + (stop - start) / 1_000_000 + "ms");
    }
}
