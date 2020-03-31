package Lab1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LCG extends Generator{
    long x0;
    long a;
    long modulo;
    long c;
    private boolean firstCall = true;
//    private long currentBit;
//    final static long max = 1_000_000;

    public LCG (long x0, long a, long modulo, long c) throws Exception {
        if (modulo <= 0)
            throw new Exception("Modulo must be greater than 0!");
        if (0 >= a || a >= modulo)
            throw new Exception("Multiplier must be in range (0, m)!");
        if (0 > c || c >= modulo)
            throw new Exception("Increment must be in range [0, m)!");
        if (0 > x0 || x0 >= modulo)
            throw new Exception("First argument must be in range [0, m)!");
        this.x0 = x0;
        this.a = a;
        this.modulo = modulo;
        this.c = c;
        values = new ArrayList<>();
        values.add(x0);
    }

    public LCG () {
        this.x0 = (new Random().nextLong());
        this.a = 672257317069504227L;
        this.modulo = 9223372036854775783L;
        this.c = 7382843889490547368L;
        values = new ArrayList<>();
        values.add(x0);
    }

    @Override
    long nextValue() {
        if (firstCall) {
            firstCall = false;
        } else {
            values.add( (this.a * values.get(values.size() - 1) + this.c) % modulo );
//            this.currentBit = (this.a * currentBit + this.c) % modulo;
        }
        return values.get(values.size() - 1);
    }

    @Override
    boolean isGenerated(List<Long> list) {

//        long inc = LCG.getUnknownIncrement(list);



        return false;
    }

    @Override
    List<Long> generateList(int size) {
        return null;
    }


    private static long gcdThing(long a, long b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.longValue();
    }

    MockTuple getUnknownIncrement(List<Long> list, MockTuple tuple) throws Exception {
        if (list.size() < 2) throw new Exception("Given too less data generated. I need at least 2 elements!");

//        s1 = s0*m + c   (mod n)
//        c  = s1 - s0*m  (mod n)
        tuple.c = (list.get(1) - list.get(0) * tuple.a) % tuple.modulo;
        return tuple;
    }

    MockTuple getUnknownMultiplier(List<Long> list, MockTuple tuple) throws Exception {
        if (list.size() < 3) throw new Exception("Given too less data generated. I need at least 3 elements!");

        long invMod = this.modInv(list.get(1) - list.get(0), tuple.modulo);

        if (invMod == -1) {
            throw new Exception("Not found inverted modulo!");
        }
        tuple.a = (list.get(2) - list.get(1)) * invMod % tuple.modulo;

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
        egcd1.x = egcd.y - b/a* egcd.x;
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















    public String generateString(int n) throws Exception {
        if (n <= 0)
            throw new Exception("String should be at least one letter long!");
        StringBuilder s = new StringBuilder();
        s.append(x0);
        s.append("_");

//        String str = "" + x0;

        long xn = this.x0;
        for (int i=1; i<n; i++) {
            xn = (this.a * xn + this.c) % modulo;
            s.append(xn);
            s.append("_");
        }
        return s.toString();
    }














//    Below is useless code.

    private static boolean areZeros (String str, int index){
        int[] numbers = Arrays.stream(str.split("_")).mapToInt(Integer::parseInt).toArray();

        int iter = index;
        while (iter < numbers.length) {
            if (numbers[iter] != 0)
                return false;
            iter++;
        }
        return true;
    }

    public static boolean isLCB(String str, SolutionClass sC) throws Exception{
        switch (sC) {
            case Cycle:
                return LCG.isLCG1(str);
            case Equation:
                return LCG.isLCG2(str);
            default:
                return false;
        }
    }

    /**
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
    }

    /**
     * This solution tried to find a tuple of variables: (x0, a, c, modulo) for given string.
     * But it doesn't work yet.
     * Three first elements of the input string are enough. After that we check the rest of the string.
     * To find such solution, we have to solve a set of equations.
     * @param str String to be checked. Elements of an array must be separated by '_' sign.
     * @return True if given string is generated by the Lab1.LCG.
     * @throws Exception if string is empty.
     */
    private static boolean isLCG2(String str) throws Exception {
        if (str.length() <= 3)
            throw new Exception("Given string is too short!");
        int[] numbers = Arrays.stream(str.split("_")).mapToInt(Integer::parseInt).toArray();

//        final int MAX = 1000;
        int MAX = -1;
        for (int num : numbers) {
            if (num > MAX) MAX = num;
        }
        int i=0, j=0, k=0, iterator=0;

        EquationClass eqClass = EquationClass.CONTRADICTORY;

//        [ a, c, m ]
        int [][] eqSolution = new int[MAX][3];
        int eqIterator = 0;

        int[] y = {numbers[0], numbers[1], numbers[2]};
        int[] w = {numbers[1], numbers[2], numbers[3]};

        while (iterator < MAX) {
            while (i < MAX) {
                while (j < MAX) {
                    while (k < MAX) {

                        int la = (-y[0]+y[1]) * (i-k) - (-y[0]+y[2]) * (i-j);
                        int ma = (w[0]+w[1]) * (i-k) - (w[0]-w[2]) * (i-j);

                        if (la == 0 && ma==0) eqClass = EquationClass.IDENTITY;
                        else if (la==0 && ma!=0) eqClass = EquationClass.CONTRADICTORY;
                        else if (!((la<0 && ma<0) || (la>0 && ma>0))) eqClass = EquationClass.CONTRADICTORY;
                        else if (la/ma * ma != la) eqClass = EquationClass.CONTRADICTORY;
                        else {
                            int a = la/ma;
                            int lm = w[1]-w[0] + y[0]*a - y[1]*a;
                            int mm = i-j;

                            if (lm == 0 && mm==0) eqClass = EquationClass.IDENTITY;
                            else if (lm==0 && mm!=0) eqClass = EquationClass.CONTRADICTORY;
                            else if (!((lm<0 && mm<0) || (lm>0 && mm>0))) eqClass = EquationClass.CONTRADICTORY;
                            else if (lm/mm * mm != lm) eqClass = EquationClass.CONTRADICTORY;
                            else {
                                int m = lm/mm;
                                int c = w[0] - y[0] + i*m;

                                if ((0 >= a || a >= m) || (0 > c || c >= m)) eqClass = EquationClass.CONTRADICTORY;
                                else {
//                                        [ a, c, m ]
                                    eqSolution[eqIterator][0] = a;
                                    eqSolution[eqIterator][0] = c;
                                    eqSolution[eqIterator][0] = m;
                                    eqClass = EquationClass.CORRECT;

                                    for (int l=1; l<numbers.length; l++) {
                                        if (numbers[l] != (a * numbers[l - 1] + c) % m) {
                                            throw new Exception("Solution doesn't match next elements!");
                                        }
                                    }

                                    eqIterator++;
                                }
                            }
                        }
                        if (eqClass == EquationClass.CORRECT)
                            break;
                        k++;
                    }
                    if (eqClass == EquationClass.CORRECT)
                        break;
                    j++;
                }
                if (eqClass == EquationClass.CORRECT)
                    break;
                i++;
            }
            if (eqClass == EquationClass.CORRECT)
                break;
            iterator++;
        }
        return (eqClass == EquationClass.CORRECT);
    }
}

enum EquationClass {
    IDENTITY,
    CONTRADICTORY,
    CORRECT
}