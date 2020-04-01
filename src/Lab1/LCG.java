package Lab1;

import java.math.BigInteger;
import java.util.*;

public class LCG extends Generator {
    //    x0 == seed
    private long a;
    private long modulo;
    private long c;
    private boolean firstCall = true;
//    private long currentBit;
//    final static long max = 1_000_000;

    public LCG(long seed, long a, long modulo, long c) throws Exception {
        if (modulo <= 0)
            throw new Exception("Modulo must be greater than 0!");
        if (0 >= a || a >= modulo)
            throw new Exception("Multiplier must be in range (0, m)!");
        if (0 > c || c >= modulo)
            throw new Exception("Increment must be in range [0, m)!");
        if (0 > seed || seed >= modulo)
            throw new Exception("First argument must be in range [0, m)!");
        this.seed = seed;
        this.a = a;
        this.modulo = modulo;
        this.c = c;
        values = new ArrayList<>();
        values.add(new BigInteger(String.valueOf(seed)));
    }

    public LCG() {
        this.seed = new Date().getTime();
//        this.seed = (new Random().nextLong());

        this.a = 672257317069504227L;
        this.modulo = 9223372036854775783L;
        this.c = 7382843889490547368L;
        values = new ArrayList<>();
        values.add(new BigInteger(String.valueOf(seed)));
    }

    @Override
    public BigInteger nextValue() {
        if (firstCall) {
            firstCall = false;
        } else {
            BigInteger valA = new BigInteger(String.valueOf(this.a));
            BigInteger val1 = values.get(values.size() - 1);
            BigInteger valC = new BigInteger(String.valueOf(this.c));
            BigInteger valM = new BigInteger(String.valueOf(this.modulo));
            BigInteger val = valA.multiply(val1).add(valC).mod(valM);
//            BigInteger val = new BigInteger(String.valueOf((this.a * values.get(values.size() - 1).longValue() + this.c) % modulo));
            values.add(val);
//            this.currentBit = (this.a * currentBit + this.c) % modulo;
        }
        return values.get(values.size() - 1);
    }

    @Override
    public boolean isGenerated(List<BigInteger> list) throws Exception {
        MockTuple tuple = new MockTuple();

        List<BigInteger> ts = new LinkedList<>();

        for (int i = 1; i < list.size(); i++) {
            ts.add(new BigInteger(String.valueOf(list.get(i).subtract(list.get(i - 1)))));
        }


        List<BigInteger> zeroes = new LinkedList<>();
        for (int i = 2; i < ts.size(); i++) {
            BigInteger s1 = ts.get(i).multiply(ts.get(i - 2));
            BigInteger s2 = ts.get(i - 1).multiply(ts.get(i - 1));
            zeroes.add(new BigInteger(String.valueOf(s1.subtract(s2))));
        }

        tuple.modulo = getGCD(zeroes).longValue();

        tuple = this.getUnknownMultiplier(list, tuple);

        return true;
    }

//    def crack_unknown_modulus(states):
//      diffs = [s1 - s0 for s0, s1 in zip(states, states[1:])]
//      zeroes = [t2*t0 - t1*t1 for t0, t1, t2 in zip(diffs, diffs[1:], diffs[2:])]
//      modulus = abs(reduce(gcd, zeroes))
//      return crack_unknown_multiplier(states, modulus)


    private static BigInteger getGCD(List<BigInteger> list) throws Exception {
        int size = list.size();
        if (size == 1) throw new Exception("Try too small!");

        BigInteger gcd = list.get(0);

        for (int i=1; i<size; i++) {
            gcd = gcd.gcd(list.get(i));
        }
        return gcd;
    }

    private static BigInteger getGCD(long a, long b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        return b1.gcd(b2);
    }

    MockTuple getUnknownIncrement(List<BigInteger> list, MockTuple tuple) throws Exception {
        if (list.size() < 2) throw new Exception("Given too less data generated. I need at least 2 elements!");

//        s1 = s0*m + c   (mod n)
//        c  = s1 - s0*m  (mod n)
        tuple.c = (list.get(1).longValue() - list.get(0).longValue() * tuple.a) % tuple.modulo;
        return tuple;
    }

    MockTuple getUnknownMultiplier(List<BigInteger> list, MockTuple tuple) throws Exception {
        if (list.size() < 3) throw new Exception("Given too less data generated. I need at least 3 elements!");

        long invMod = this.modInv(list.get(1).longValue() - list.get(0).longValue(), tuple.modulo);

        if (invMod == -1) {
            throw new Exception("Not found inverted modulo!");
        }
        tuple.a = (list.get(2).longValue() - list.get(1).longValue()) * invMod % tuple.modulo;

        return this.getUnknownIncrement(list, tuple);
    }


    EGCDTuple getEGCD(long a, long b) {
        EGCDTuple egcd = new EGCDTuple();
        if (a == 0) {
            egcd.g = b;
            egcd.x = 0;
            egcd.y = 1;
            return egcd;
        }

        egcd = getEGCD(b % a, a);
        EGCDTuple egcd1 = new EGCDTuple();
        egcd1.g = egcd.g;
        egcd1.x = egcd.y - b / a * egcd.x;
        egcd1.y = egcd.x;
        return egcd1;
    }

    long modInv(long b, long n) {
        EGCDTuple egcd = this.getEGCD(b, n);

        if (egcd.g == 1)
            return egcd.x % n;
        else
            return -1;
    }


//    def egcd(a, b):
//            if a == 0:
//            return (b, 0, 1)
//            else:
//    g, x, y = egcd(b % a, a)
//        return (g, y - (b // a) * x, x)
//
//    def modinv(b, n):
//    g, x, _ = egcd(b, n)
//    if g == 1:
//            return x % n

    class EGCDTuple {
        long g;
        long x;
        long y;
    }

    class MockTuple {
        long x0 = -1;
        long a;
        long modulo;
        long c;
    }
























    /*
     * Following methods looks for a cycle in the string.
     * It can be proved, than to any cycle can be assigned at least one set of arguments to the Lab1.LCG generator,
     * used to generate such string.
     * What about if string is too short to loop itself?
     * We can resolve an equations set to check if it's generated by Lab1.LCG, but it's too difficult.
     * When c==0, string can get at some point 0, and then is looped by 0's.
     * @param str String to be checked. Elements of an array must be separated by '_' sign.
     * @return True if given string is generated by the Lab1.LCG.
     * @throws Exception if string is empty.
     */
    /*
    private static boolean isLCG1(String str) throws Exception {
        int[] numbers = Arrays.stream(str.split("_")).mapToInt(Integer::parseInt).toArray();
        int size = numbers.length;

        if (size == 0) throw new Exception("Given string is too short!");

        for (int sizeOfWindow=1; sizeOfWindow<size; sizeOfWindow++) {
            int[] window = new int[sizeOfWindow];
            boolean isWindowCycle = true;

//            copy elements from beginning of the array to the window.
            System.arraycopy(numbers, 0, window, 0, sizeOfWindow);

            int windowIterator = 0;
            while (isWindowCycle) {
                for (int glass=0; glass<sizeOfWindow; glass++) {
                    int indexInString = windowIterator * sizeOfWindow + glass;
                    if (indexInString >= size){
                        return true;
                    }
                    if (numbers[indexInString] != window[glass]) {
                        if (numbers[indexInString] == 0 && areZeros(str, indexInString) )
                            return true;
                        isWindowCycle = false;
                        break;
                    }
                }
                windowIterator++;
            }
        }
        return false;
    }*/

}