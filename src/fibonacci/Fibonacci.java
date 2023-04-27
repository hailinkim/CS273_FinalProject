package fibonacci;
public class Fibonacci {
    public enum Method {
        RECURSIVE,
        ITERATIVE,
        DP //dynamic programming
    }
    // A method to compute the Fibonacci sequence using the specified method
    public static long[] compute(int n, Method m) {
        switch (m) {
            case RECURSIVE:
                return computeRecursive(n);
//            case ITERATIVE:
//                return computeIterative(n);
//            case DP:
//                return computeDynamicProgramming(n);
            default:
                throw new IllegalArgumentException("Invalid method: " + m);
        }
    }
    // A recursive method to compute the Fibonacci sequence
    private static long[] computeRecursive(int n) {
        long[] fib = new long[n+1];
        for (int i = 0; i < n+1; i++) {
            fib[i] = fibonacciRecursive(i);
        }
        return fib;
    }

    private static long fibonacciRecursive(int n) {
        if (n <= 1) {
            return n;
        } else {
            return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
        }
    }

    // An iterative method to compute the Fibonacci sequence
    private static int[] computeIterative(int n) {
        int[] fib = new int[n];
        if (n >= 1) {
            fib[0] = 0;
        }
        if (n >= 2) {
            fib[1] = 1;
        }
        for (int i = 2; i < n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
        return fib;
    }

    // A dynamic programming method to compute the Fibonacci sequence
    private static int[] computeDynamicProgramming(int n) {
        int[] fib = new int[n];
        if (n >= 1) {
            fib[0] = 0;
        }
        if (n >= 2) {
            fib[1] = 1;
        }
        for (int i = 2; i < n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
        return fib;
    }
}
