package prime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ParallelPrimes {
    public static final String TEAM_NAME = "The AMA's";
    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);
    public static final int MAX_SMALL_PRIME = 1 << 20;
    public static int[] getSmallPrimesUpTo(int max) {
        // check that the value max is in bounds, and throw an exception if not
        if (max > MAX_SMALL_PRIME) {
            throw new RuntimeException("The value " + max + "exceeds the maximum small prime value (" + MAX_SMALL_PRIME + ")");
        }

        // isPrime[i] will be true if and only if i is prime.
        // Initially set isPrime[i] to true for all i >= 2.
        boolean[] isPrime = new boolean[max];

        for (int i = 2; i < max; i++) {
            isPrime[i] = true;
        }

        // Apply the sieve of Eratosthenes to find primes.
        // The procedure iterates over values i = 2, 3,.... Math.sqrt(max).
        // If isPrime[i] == true, then i is a prime.
        // When a prime value i is found, set isPrime[j] = false for all multiples j of i.
        // The procedure terminates once we've examined all values i up to Math.sqrt(max).
        int rootMax = (int) Math.sqrt(max);
        for (int i = 2; i < rootMax; i++) {
            if (isPrime[i]) {
                for (int j = 2 * i; j < max; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        // Count the number of primes we've found, and put them
        // sequentially in an appropriately sized array.
        int count = trueCount(isPrime);

        int[] primes = new int[count];
        int pIndex = 0;

        for (int i = 2; i < max; i++) {
            if (isPrime[i]) {
                primes[pIndex] = i;
                pIndex++;
            }
        }

        return primes;
    }

    // Count the number of true values in an array of boolean values, arr
    public static int trueCount(boolean[] arr) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i])
                count++;
        }

        return count;
    }

    // Returns an array of all prime numbers up to ROOT_MAX
    public static int[] getSmallPrimes() {
        return getSmallPrimesUpTo(ROOT_MAX);
    }

    // Compute a block of prime values between start and start +
    // isPrime.length. Specifically, after calling this method
    // isPrime[i] will be true if and only if start + i is a prime
    // number, assuming smallPrimes contains all prime numbers of to
    // sqrt(start + isPrime.length).
    private static void primeBlock(BitSet isPrime, int[] smallPrimes, int start) {

        // initialize isPrime to be all true
        isPrime.set(0, isPrime.size()-1);

        for (int p : smallPrimes) {
            // find the next number >= start that is a multiple of p
            int i = (start % p == 0) ? start : p * (1 + start / p);
            i -= start;

            while (i < isPrime.size()) {
                isPrime.clear(i);
                i += p;
            }
        }
    }
    /*
    * TO-DO
    * 1. add the catch block for the awaitterminate() and check if it works on angelica's macbook and hpc
    * 2. fiddle around with chunk sizes e.g. ROOTMAX
    * 3. changing boolean array (T/F) into an array of bits(0/1) - need to update primeBlock as well
    * 4. change the atomic integer count into an int
    * 5. maybe even more optimization?
    *   - @Ahanu optimize getSmallPrimes()
    *   - @Angelica optimize primeBlock()
    * */
    public static void optimizedPrimes(int[] primes) {
        int[] smallPrimes = getSmallPrimes();
        int nPrimes = primes.length;

        int count = 0;
        int minSize = Math.min(nPrimes, smallPrimes.length);
        for (; count < minSize; count++) {
            primes[count] = smallPrimes[count];
        }

        if (nPrimes == minSize) {
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<BitSet>> tasks = new ArrayList<>();
        int blockSize = ROOT_MAX;

        for (long curBlock = ROOT_MAX; curBlock < MAX_VALUE; curBlock += blockSize) {
            int start = (int) curBlock;
            Callable<BitSet> task = () -> {
                BitSet isPrime = new BitSet(blockSize);
                isPrime.set(0, isPrime.size() - 1);
                for (int p : smallPrimes) {
                    // find the next number >= start that is a multiple of p
                    int i = (start % p == 0) ? start : p * (1 + start / p);
                    i -= start;

                    while (i < isPrime.size()) {
                        isPrime.clear(i);
                        i += p;
                    }
                }
                return isPrime;
            };
            tasks.add(task);
        }
        List<Future<BitSet>> futures;
        try {
            futures = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long curBlock = ROOT_MAX;
        for (Future<BitSet> future : futures) {
            try {
                BitSet blockPrime = future.get();
                for (int i = 0; i < blockPrime.size() && count < nPrimes; i++) {
                    if(blockPrime.get(i))
                        primes[count++] = (int) (curBlock + i);
                }
                curBlock+=blockSize;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        executor.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                // Cancel currently executing tasks forcefully
                executor.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ex) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}