package Lab1;

import java.math.BigInteger;
import java.util.*;

/**
 * A Generator based Linear congruential generator. It's described for example here: https://en.wikipedia.org/wiki/Linear_congruential_generator.
 */
public class LCG extends Generator {
    private BigInteger a = new BigInteger(String.valueOf(672257317069504227L));
    private BigInteger modulus = new BigInteger(String.valueOf(9223372036854775783L));
    private BigInteger c = new BigInteger(String.valueOf(7382843889490547368L));
    private boolean firstCall = true;

    /**
     * Constructs a LCG generator with given parameters. It's created just in case, but I don't use it.
     *
     * @param seed    An seed used to generator. It makes, that outputs differ.
     * @param a       Multiplier in generator.
     * @param modulus Congruential base of the calculations.
     * @param c       Incrementer
     * @throws Exception Thrown if values are incorrect (ex. negative, greater than modulus).
     */
    public LCG(long seed, long a, long modulus, long c) throws Exception {
        if (modulus <= 0)
            throw new Exception("Modulus must be greater than 0!");
        if (0 >= a || a >= modulus)
            throw new Exception("Multiplier must be in range (0, m)!");
        if (0 > c || c >= modulus)
            throw new Exception("Increment must be in range [0, m)!");
        if (0 > seed || seed >= modulus)
            throw new Exception("First argument must be in range [0, m)!");
        this.seed = seed;
        this.a = new BigInteger(String.valueOf(a));
        this.modulus = new BigInteger(String.valueOf(modulus));
        this.c = new BigInteger(String.valueOf(c));

        values = new ArrayList<>();
        values.add(new BigInteger(String.valueOf(seed)));
    }


    /**
     * Constructs a LCG generator with default seed, what is equal to Time value expressed in milliseconds.
     */
    public LCG() {
        this.seed = new Date().getTime();

        values = new ArrayList<>();
        values.add(BigInteger.valueOf(seed));
    }

    @Override
    public BigInteger nextValue() {
        if (firstCall) {
            firstCall = false;
        } else {
            BigInteger val1 = values.get(values.size() - 1);
            BigInteger val = this.a.multiply(val1).add(this.c).mod(this.modulus);
            values.add(val);
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

        tuple.modulus = getGCD(zeroes);
        tuple = getUnknownMultiplier(list, tuple);


        int size = list.size();
        int iter = 1;

        while (iter < size) {
            BigInteger prev = list.get(iter - 1);
            BigInteger curr = list.get(iter);

            BigInteger counted = (prev.multiply(tuple.a).add(tuple.c)).mod(tuple.modulus);

            if (counted.compareTo(curr) != 0)
                return false;
            iter++;
        }
        return true;
    }


    /**
     * A method used to get a greatest common divider of given BigInteger values.
     *
     * @param list list of integers.
     * @return A greatest common divider.
     * @throws Exception Thrown, when given only one value in list.
     */
    private static BigInteger getGCD(List<BigInteger> list) throws Exception {
        int size = list.size();
        if (size == 1) throw new Exception("List too short!");

        BigInteger gcd = list.get(0);

        for (int i = 1; i < size; i++) {
            gcd = gcd.gcd(list.get(i));
        }
        return gcd;
    }

    /**
     * A method used to get a greatest common divider of given two BigInteger values.
     *
     * @param a One of integers.
     * @param b Second of integers.
     * @return Value of greatest common divider.
     */
    private static BigInteger getGCD(long a, long b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        return b1.gcd(b2);
    }

    /**
     * A helper method used to find Tuple [a, c, modulus] of LCG, when c is unknown.
     *
     * @param list  A list of values of LCG.
     * @param tuple An object of Tuple, used to store all of them.
     * @return A tuple updated by incrementer.
     * @throws Exception Thrown, when list is too short.
     */
    private static MockTuple getUnknownIncrement(List<BigInteger> list, MockTuple tuple) throws Exception {
        if (list.size() < 2) throw new Exception("Given too less data generated. I need at least 2 elements!");

//        s1 = s0*m + c   (mod n)
//        c  = s1 - s0*m  (mod n)

        tuple.c = list.get(1).subtract(list.get(0).multiply(tuple.a)).mod(tuple.modulus);
        return tuple;
    }

    /**
     * A helper method used to find Tuple [a, c, modulus] of LCG, when a is unknown.
     *
     * @param list  A list of values of LCG.
     * @param tuple An object of Tuple, used to store all of them.
     * @return A tuple updated by multiplier and incrementer.
     * @throws Exception Thrown, when list is too short.
     */
    private static MockTuple getUnknownMultiplier(List<BigInteger> list, MockTuple tuple) throws Exception {
        if (list.size() < 3) throw new Exception("Given too less data generated. I need at least 3 elements!");

        BigInteger invMod = modInv((list.get(1).subtract(list.get(0))).mod(tuple.modulus), tuple.modulus);

        if (invMod.compareTo(BigInteger.valueOf(-1)) == 0) {
            throw new Exception("Not found inverted modulus!");
        }
        tuple.a = ((list.get(2).subtract(list.get(1))).multiply(invMod)).mod(tuple.modulus);

        return getUnknownIncrement(list, tuple);
    }


    /**
     * A helper method used to count the Extended Euclidean Algorithm.
     *
     * @param a First of values.
     * @param b Second of values.
     * @return Three values used to determine the Extended Euclidean Algorithm.
     */
    private static EGCDTuple getEGCD(BigInteger a, BigInteger b) {
        EGCDTuple egcd = new EGCDTuple();
        if (a.compareTo(BigInteger.valueOf(0)) == 0) {
            egcd.g = b;
            egcd.x = BigInteger.valueOf(0);
            egcd.y = BigInteger.valueOf(1);
            return egcd;
        }

        egcd = getEGCD(b.mod(a), a);
        EGCDTuple egcd1 = new EGCDTuple();
        egcd1.g = egcd.g;
        egcd1.x = egcd.y.subtract(b.divide(a).multiply(egcd.x));
        egcd1.y = egcd.x;
        return egcd1;
    }

    /**
     * A helper method used to determine the Inverted value in modulus multiplication operation.
     *
     * @param b Given value to invert.
     * @param n Modulus base of the operation.
     * @return Inverted value.
     */
    private static BigInteger modInv(BigInteger b, BigInteger n) {
        EGCDTuple egcd = getEGCD(b, n);

        if (egcd.g.compareTo(BigInteger.valueOf(1)) == 0)
            return egcd.x.mod(n);
        else
            return BigInteger.valueOf(-1);
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

}

/**
 * A helper class used to store values of Extended Euclidean Algorithm.
 */
class EGCDTuple {
    BigInteger g;
    BigInteger x;
    BigInteger y;
}

/**
 * A helper class used to store values of LCG.
 */
class MockTuple {
    BigInteger a;
    BigInteger modulus;
    BigInteger c;
}