package prime;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.Callable;


public class PrimeTask implements Callable<boolean[]> {

    private final int[] smallPrimes;
    private final int start;
    private final int blockSize;

    public PrimeTask(int[] smallPrimes, int start, int blockSize) {
        this.smallPrimes = smallPrimes;
        this.start = start;
        this.blockSize = blockSize;
    }

    @Override
    public boolean[] call() {
//        int blockSize = end - start;
        boolean[] isPrime = new boolean[blockSize];
        Arrays.fill(isPrime, true);
//        BitSet primeFlags = new BitSet(blockSize);
        // initialize isPrime to be all true
//        primeFlags.set(0, primeFlags.size()-1);

        for (int p : smallPrimes) {
            // find the next number >= start that is a multiple of p
            int i = (start % p == 0) ? start : p * (1 + start / p);
            i -= start;

            while (i < isPrime.length) {
                isPrime[i] = false;
//                primeFlags.clear(i);
                i += p;
            }
        }
//        for (int i = 0; i < blockSize; i++) {
//            isPrime[i] = primeFlags.get(i);
//        }
        return isPrime;
    }
}
