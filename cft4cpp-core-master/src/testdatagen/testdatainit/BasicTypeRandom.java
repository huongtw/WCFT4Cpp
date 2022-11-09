package testdatagen.testdatainit;

import java.util.Random;

/**
 * Generate value random
 *
 * @author ducanhnguyen
 */
public class BasicTypeRandom {

    public static int generateInt(int minBound, int maxBound) {
        Random rand = new Random();
        int randomNum = minBound + rand.nextInt(maxBound + 1 - minBound);
        return randomNum;
    }

    public static int generateInt(String minBound, String maxBound) {
        Random rand = new Random();
        int randomNum = Integer.parseInt(minBound)
                + rand.nextInt(Integer.parseInt(maxBound) - Integer.parseInt(minBound));
        return randomNum;
    }

    public static double generateFloat(int minBound, int maxBound) {
        Random rand = new Random();
        int randomNum = minBound + rand.nextInt(maxBound - minBound);
        int decimal = rand.nextInt(100);
        String output = randomNum + "." + decimal;
        return Double.parseDouble(output);
    }

}
