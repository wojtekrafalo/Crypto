import Lab1.LCG;
import Lab1.SolutionClass;

public class Main {
    public static void main(String[] args) {
        try {
            LCG generator = new LCG(1, 2, 10, 1);

            String s = generator.generateString(10);
            System.out.println(s);
            System.out.println(generator.generateString(15));

            if (LCG.isLCB("1_2_3_4_5_1_2_3_4_5_1_2_3_4_5", SolutionClass.Cycle))
                System.out.println("Should have returned true.");

            if (LCG.isLCB("1_2_4_8_16_32_0_0_0_0", SolutionClass.Cycle))
                System.out.println("Should have returned true.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}