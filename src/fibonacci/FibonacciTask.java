package fibonacci;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ConcurrentHashMap;
public class FibonacciTask extends RecursiveTask<long[]> {
    private static final int THRESHOLD = 10;
    private static final ConcurrentHashMap<Integer, long[]> cache = new ConcurrentHashMap<>();

    private int n;

    public FibonacciTask(int n) {
        this.n = n;
    }

    public long[] fibIterative(int n){
        // Compute the Fibonacci sequence sequentially
        long[] fib = new long[n + 1];
        fib[1] = 1;
        for (int i = 2; i <= n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
        // Save the result in the cache
        cache.put(n, fib);
        return fib;
    }
    @Override
    protected long[] compute() {
        if (n <= THRESHOLD) {
            return fibIterative(n);
        }
        else {
            // Check if the result is already in the cache
            long[] cachedFib = cache.get(n);
            if (cachedFib != null) {
                return cachedFib;
            }

            // Split the task into two subtasks
            FibonacciTask fib1 = new FibonacciTask(n - 1);
            FibonacciTask fib2 = new FibonacciTask(n - 2);

            // Fork the first subtask and compute the second subtask
            fib1.fork();
            long[] fib2Result = fib2.compute();

            // Join the first subtask and merge the results
            long[] fib1Result = fib1.join();

            long[] result = new long[n + 1];
            System.arraycopy(fib1Result, 0, result, 0, fib1Result.length);
            for (int i = fib1Result.length; i < fib2Result.length; i++) {
                result[i] = fib2Result[i];
            }
//            System.out.println(Arrays.toString(result));

            // Compute the remaining Fibonacci numbers
            for (int i = THRESHOLD + 1; i < result.length; i++) {
                result[i] = result[i - 1] + result[i - 2];
            }

            // Save the result in the cache
            cache.put(n, result);
            return result;
        }
    }
}