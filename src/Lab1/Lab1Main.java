package Lab1;

/**
 * Class used to test first Lab list.
 */
public class Lab1Main {
    /**
     * If you want to test first Lab list, just run this method. I predicted a bit of the cases.
     * @param args You don't need this argument.
     */
    public static void main(String[] args) {
        try {
            LCG generator = new LCG(1, 2, 10, 1);

            String s1 = generator.generateString(10);
            System.out.println(s1);
            System.out.println(generator.generateString(15));

            if (LCG.isLCB("1_2_3_4_5_1_2_3_4_5_1_2_3_4_5", SolutionClass.Cycle))
                System.out.println("Should have returned true.");

            if (LCG.isLCB("1_2_4_8_16_32_0_0_0_0", SolutionClass.Cycle))
                System.out.println("Should have returned true.");

            GLIBC generator2 = new GLIBC(1000, 1);
            String s2 = generator2.generateString();
            System.out.println(s2);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}