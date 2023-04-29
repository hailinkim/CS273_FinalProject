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
        int[] smallPrimes = getSmallPrimes(); // Get small primes up to square root of max value
        int nPrimes = primes.length;
        AtomicInteger count = new AtomicInteger(0); // AtomicInteger to handle thread-safe incrementation of count variable

        int minSize = Math.min(nPrimes, smallPrimes.length);
        // Copy small primes into output array and update count variable using AtomicInteger
        for (int i = 0; i < minSize; i++) {
            primes[count.getAndIncrement()] = smallPrimes[i];
        }

        if (nPrimes == minSize) { // If all primes have been found, return
            return;
        }

        boolean[] isPrime = new boolean[ROOT_MAX]; // Array to store primes up to square root of max value
        int blockSize = ROOT_MAX / Runtime.getRuntime().availableProcessors(); // Determine size of prime blocks to be processed
        List<Callable<Void>> tasks = new ArrayList<>(); // Create list of Callable tasks to be executed by threads

//        System.out.println(blockSize);
        // Divide up prime blocks to be processed by threads and add them as Callable tasks
        for (long curBlock = ROOT_MAX; curBlock < MAX_VALUE; curBlock += blockSize) {
            long start = curBlock;
//            long end = Math.min(curBlock + blockSize, MAX_VALUE);

            tasks.add(() -> {
                BitSet localIsPrime = new BitSet(blockSize); // Create local array to store primes for current block
                primeBlock(localIsPrime, smallPrimes, (int) (start)); //start-ROOT_MAX // Determine primes for current block using primeBlock() method
                for (int i = 0; i < localIsPrime.size() && count.get() < nPrimes; i++) { //start + i < end
                    if (localIsPrime.get(i)) { // If a prime is found, update output array using AtomicInteger
                        primes[count.getAndIncrement()] = (int) (start + i);
                    }
                }
                return null;
            });
        }
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Create ExecutorService to manage threads

        try {
            executor.invokeAll(tasks); // Execute all Callable tasks using threads managed by ExecutorService
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown(); // Shutdown ExecutorService
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
}