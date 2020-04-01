package Lab1;

import java.math.BigInteger;
import java.util.*;

public class GLIBC extends Generator {
    private static final BigInteger modulo = new BigInteger("2147483647");
    private static final BigInteger multiplier = new BigInteger("16807");
    private static final int firstBound = 31;
    private static final int smallDiff = 3;
    private static final int secondBound = 34;
    private static final int thirdBound = 344;
//    private long seed;

    /**
     * @param seed An seed used to generator. It makes, that outputs differ.
     * @throws Exception When arguments are negative.
     */
    public GLIBC(long seed) throws Exception {
        if (seed < 0) throw new Exception("String of negative seed cannot be generated.");
        this.seed = seed;
    }

    /**
     * Constructs a generator with default size of string and default seed.
     */
    public GLIBC() {
        this.seed = new Date().getTime();
//        this.seed = (new Random().nextLong());
        values = new LinkedList<>();

        values.add(0, new BigInteger(String.valueOf(this.seed)));

        for (int i = 1; i < firstBound; i++) {
            values.add(i, multiplier.multiply(values.get(i - 1)).mod(modulo));
        }

        for (int i = firstBound; i < secondBound; i++) {
            BigInteger bi = new BigInteger(String.valueOf(values.get(i - firstBound)));
//            Long bi = values.get(i - firstBound);
            values.add(i, bi);
        }

        for (int i = secondBound; i < thirdBound; i++) {
            BigInteger bi = new BigInteger(String.valueOf(values.get(i - firstBound).add(values.get(i - smallDiff))));
//            Long bi = values.get(i - firstBound) + values.get(i - smallDiff);
            values.add(i, bi);
        }
    }

    @Override
    public BigInteger nextValue() {
        int size = values.size();
        BigInteger b1 = values.get(size - firstBound);
        BigInteger b2 = values.get(size - smallDiff);
        BigInteger b3 = b1.add(b2);
        values.add(size, b3);

//        return (b3 >> 1);
        return (b3.shiftRight(1));
    }

    @Override
    public boolean isGenerated(List<BigInteger> list) throws Exception {

        int size = list.size();
        if (size < firstBound) throw new Exception("Given generated data set is too small.");
        int iter = firstBound;

        while (iter < size) {

            BigInteger lg0 = list.get(iter - smallDiff).multiply(new BigInteger(String.valueOf(2)));
            BigInteger lg1 = list.get(iter - smallDiff).multiply(new BigInteger(String.valueOf(2))).add(new BigInteger(String.valueOf(1)));

            BigInteger ls0 = list.get(iter - firstBound).multiply(new BigInteger(String.valueOf(2)));
            BigInteger ls1 = list.get(iter - firstBound).multiply(new BigInteger(String.valueOf(2))).add(new BigInteger(String.valueOf(1)));

            BigInteger curr = list.get(iter);

//            boolean check1 = (curr == ((lg0 + ls0) >> 1));
//            boolean check2 = (curr == ((lg0 + ls1) >> 1));
//            boolean check3 = (curr == ((lg1 + ls0) >> 1));
//            boolean check4 = (curr == ((lg1 + ls1) >> 1));

            BigInteger res1 = lg0.add(ls0).shiftRight(1);
            boolean check1 = (curr.equals(res1));

            BigInteger res2 = lg0.add(ls1).shiftRight(1);
            boolean check2 = (curr.equals(res2));

            BigInteger res3 = lg1.add(ls0).shiftRight(1);
            boolean check3 = (curr.equals(res3));

            BigInteger res4 = lg1.add(ls1).shiftRight(1);
            boolean check4 = (curr.equals(res4));

            boolean any = check1 || check2 || check3 || check4;
            if (!any)
                return false;
            iter++;
        }

        return true;
    }
}