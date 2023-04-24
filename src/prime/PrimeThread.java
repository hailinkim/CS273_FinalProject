package prime;

public class PrimeThread extends Thread {
    // replace this string with your team name
    public static final String TEAM_NAME = "The AMA's";

    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);
    public static final int MAX_SMALL_PRIME = 1 << 20;

    private int threadID;
    private int numTasks;
    private long startIndex;
    private int[] smallPrimes;
    private int[] primes;
    private boolean[] isPrime;
    private int count,  nPrimes;

    public PrimeThread(int threadID, int tasksPerThread, long startIndex, int[] smallPrimes, int[] primes, boolean[] isPrime, int count, int nPrimes) {
        this.threadID = threadID;
        this.numTasks = tasksPerThread;
        this.startIndex = startIndex;
        this.smallPrimes = smallPrimes;
        this.primes = primes;
        this.isPrime = isPrime;
        this.count = count;
        this.nPrimes = nPrimes;
    }

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

    public void run() {
        for (long curBlock = startIndex; curBlock < startIndex + numTasks; curBlock += ROOT_MAX) {
            primeBlock(isPrime, smallPrimes, (int) curBlock);
            for (int i = 0; i < isPrime.length && count < nPrimes; i++) {
                if (isPrime[i]) {
                    primes[count++] = (int) curBlock + i;
                }
            }
        }
        System.out.println("thread id# " + threadID + " finished its work.");
    } // end of run()
}// end of PrimeThread.java
