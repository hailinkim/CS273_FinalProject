package prime.experiment.v1;// A class for testing the correctness and performance of your
// implementation of the prime finding task.
// ***DO NOT MODIFY THIS FILE***

//import prime.ParallelPrimes;
//import prime.Primes;

import java.util.Arrays;

public class PrimeTester {
    public static final int WARMUP_ITERATIONS = 1;
    public static final int TEST_ITERATIONS = 1;

    public static void main (String[] args) {
        System.out.println("Computing primes up to " + Primes.MAX_VALUE);
        int[] knownPrimes = new int[Primes.N_PRIMES ]; //Primes.N_PRIMES = 2147483647
        int[] testPrimes = new int[Primes.N_PRIMES ];

        // find known primes using the baseline procedure
        Primes.baselinePrimes(knownPrimes);
        System.out.println("i'm here!");

        // run warmup before timing
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            ParallelPrimes.optimizedPrimes(testPrimes);
        }
        System.out.println("warmup finished");

        // run main iterations
        long start = System.nanoTime();

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            System.out.println("testing optimizedPrimes");
            ParallelPrimes.optimizedPrimes(testPrimes);
        }

        long elapsedMS = (System.nanoTime() - start) / 1_000_000;

        // check correctness

        System.out.println("Team: " + ParallelPrimes.TEAM_NAME);

        for (int i = 0; i < knownPrimes.length; i++) {
            if (knownPrimes[i] != testPrimes[i]) {
                System.out.println("correctness test failed\n" +
                        "i = " + i + "\n" +
                        "knownPrimes[i] = " + knownPrimes[i] + "\n" +
                        "testPrimes[i] = " + testPrimes[i]);
                return;
            }
        }

        System.out.println("correctness test passed\n" +
                "elapsed time: " + elapsedMS + "ms");
    }
}
