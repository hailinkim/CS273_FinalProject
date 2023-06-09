package prime;

import static prime.Primes.baselinePrimes;

public class ParallelPrimes {
    // replace this string with your team name
    public static final String TEAM_NAME = "The AMA's";

    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);
    public static final int MAX_SMALL_PRIME = 1 << 20;

    // Use the sieve of Eratosthenes to compute all prime numbers up to max.
    // The largest allowed value of max is MAX_SMALL_PRIME.
    private static int[] getSmallPrimesUpTo(int max) {
        // check that the value max is in bounds, and throw an exception if not
//        if (max > MAX_SMALL_PRIME) {
//            throw new RuntimeException("The value " + max + "exceeds the maximum small prime value (" + MAX_SMALL_PRIME + ")");
//        }

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

    // Compute a block of prime values between start and start + isPrime.length.
    // Specifically, after calling this method
    // isPrime[i] will be true if and only if start + i is a prime number,
    // assuming smallPrimes contains all prime numbers up to sqrt(start + isPrime.length).
    private static void primeBlock(boolean[] isPrime, int[] smallPrimes, int start) {

        // initialize isPrime to be all true
        for (int i = 0; i < isPrime.length; i++) {
            isPrime[i] = true;
        }

        for (int p : smallPrimes) {
            // find the next number >= start that is a multiple of p
            int i = (start % p == 0) ? start : p * (1 + start / p);
            i -= start;

            while (i < isPrime.length) {
                isPrime[i] = false;
                i += p;
            }
        }
    }

    public static void optimizedPrimes(int[] primes) {
        System.out.println("starting optimizedPrimes method.");

        int numThreads = Runtime.getRuntime().availableProcessors(); // this is equal to the number of processors available to my computer
        int tasksPerThread = MAX_VALUE / numThreads;
        // compute small prime values from 0 to tasksPerThread*1.
        int[] smallPrimes = getSmallPrimesUpTo((int) Math.sqrt(tasksPerThread)); // this calls getSmallPrimesUpTo(sqrt of ROOT_MAX), which computes primes via baseline SoE implementation.

        int nPrimes = primes.length;

        // write small primes to primes
        int count = 0;
        int minSize = Math.min(nPrimes, smallPrimes.length); // which is smaller: primes.length, or smallPrimes.length
        for (; count < minSize; count++) {
            primes[count] = smallPrimes[count];
        }

        // check if we've already filled primes, and return if so
        if (nPrimes == minSize) { // if nPrimes was smaller than smallPrimes.length, does that mean primes has already been filled?
            System.out.println("yikes- primes are already filled. returning.");
            return;
        }

        // Apply the sieve of Eratosthenes to find primes.
        // This procedure partitions the sieving task up into several blocks,
        // where each block isPrime stores boolean values associated with
        // ROOT_MAX consecutive numbers. Note that
        // partitioning the problem in this way is necessary because
        // we cannot create a boolean array of size MAX_VALUE.


        boolean[] isPrime = new boolean[tasksPerThread];

        // declare an array to store all the threads. numThreads was computed earlier.
        PrimeThread[] threads = new PrimeThread[numThreads];

        long startIndex = (long) Math.sqrt(tasksPerThread); // prime number from 0 to tasksPerThread*1 has been found already using getSmallPrimesUpTo(tasksPerThread*1).
        for (int i = 0; i < numThreads; i++) { // initialize numThreads number of threads
            if (i == numThreads - 1) { // if this is the last thread, it gets the leftover tasks, from tasksPerThread*i up to primes.length (aka nPrimes).
                tasksPerThread = Primes.MAX_VALUE - tasksPerThread * i;
            }
            // create and initialize this i-th thread.
            threads[i] = new PrimeThread(i, tasksPerThread, startIndex, smallPrimes, primes, isPrime, count, nPrimes);

            System.out.println("Created thread # " + i + ". Start Index is " + startIndex + ", and its numTasks is "+ tasksPerThread);

            startIndex += tasksPerThread;
            // initialize each thread with the number of tasks it must conduct, the index for it to write results to, and the shared array.
        }
        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ignored) {
                // don't care if t was interrupted
            }
        }
        // The code below should be the last line of code in optimized Primes:
        baselinePrimes(primes); // baselinePrimes quickly returns if we've already filled primes.
    } // end of optimizedPrimes
}
