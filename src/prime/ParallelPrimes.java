package prime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

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

        // isPrime[i] will be true if and only if it is prime.
        // Initially set isPrime[i] to true for all i >= 2.
        BitSet isPrime = new BitSet(max);
        isPrime.set(2,max, true);

        // Apply the sieve of Eratosthenes to find primes.
        // The procedure iterates over values i = 2, 3,.... Math.sqrt(max).
        // If isPrime[i] == true, then i is a prime.
        // When a prime value i is found, set isPrime[j] = false for all multiples j of i.
        // The procedure terminates once we've examined all values i up to Math.sqrt(max).

        for (int i = 2; i < Math.sqrt(max); i++) {
            if (isPrime.get(i)) {
                for (int j = 2 * i; j < max; j += i) {
                    isPrime.clear(j);
                }
            }
        }
        return isPrime.stream().toArray();
    }

    public static void optimizedPrimes(int[] primes) {
        int[] smallPrimes = getSmallPrimesUpTo(ROOT_MAX);
        int nPrimes = primes.length;
        int minSize = Math.min(nPrimes, smallPrimes.length);
        int count = minSize;
        System.arraycopy(smallPrimes, 0, primes, 0, minSize);

        if (nPrimes == minSize) {
            return;
        }

        ExecutorService executor = Executors.newWorkStealingPool();
        List<Future<int[]>> futures = new ArrayList<>();
        int blockSize = ROOT_MAX*15;
        for (long curBlock = ROOT_MAX; curBlock < MAX_VALUE; curBlock += blockSize) {
            if(curBlock + blockSize > MAX_VALUE)
                blockSize = (int) (MAX_VALUE - curBlock + 1);
            futures.add(executor.submit(new PrimeTask(smallPrimes, (int) curBlock, blockSize)));
        }

        for(Future<int[]> future:futures){
            try{
                int[] blockPrime = future.get();
                System.arraycopy(blockPrime, 0, primes, count, blockPrime.length);
                count += blockPrime.length;
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        executor.shutdown();
    }
}
class PrimeTask implements Callable<int[]> {
    int[] smallPrimes;
    int start;
    int blockSize;
    public PrimeTask(int[] smallPrimes, int start, int blockSize) {
        this.smallPrimes = smallPrimes;
        this.start = start;
        this.blockSize = blockSize;
    }
    @Override
    public int[] call() {
        BitSet isPrime = primeBlock(smallPrimes, start, blockSize);
        return Arrays.stream(isPrime.stream().toArray()).map(i -> i + (int) start).toArray();
    }
    private static BitSet primeBlock(int[] smallPrimes, int start, int blockSize) {
        BitSet isPrime = new BitSet(blockSize);
        isPrime.set(0, blockSize, true);
        for (int p : smallPrimes) {
            // find the next number >= start that is a multiple of p
            int i = (start % p == 0) ? start : p * (1 + start / p);
            i -= start;

            while (i < blockSize) {
                isPrime.clear(i);
                i += p;
            }
        }
        return isPrime;
    }
}