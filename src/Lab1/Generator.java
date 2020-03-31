package Lab1;

import java.util.List;

public abstract class Generator {

    protected List<Long> values;
    abstract long nextValue();

    abstract boolean isGenerated(List<Long> list) throws Exception;

    abstract List<Long> generateList(int size);

    static boolean isGenerated (Generator generator) {

        return false;
    }
}
