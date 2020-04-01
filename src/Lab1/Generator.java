package Lab1;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public abstract class Generator {
    protected long seed;

    protected List<BigInteger> values;
    public abstract BigInteger nextValue();

    public abstract boolean isGenerated(List<BigInteger> list) throws Exception;

    public List<BigInteger> generateList(int size) {
        LinkedList<BigInteger> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            list.add(this.nextValue());
        }
        return list;
    }

    /**
     * Check for LCG and GLIBC. If neither, return false.
     * @param generator
     * @return
     */
    public static boolean isGenerated (Generator generator) {




        return false;
    }
}
