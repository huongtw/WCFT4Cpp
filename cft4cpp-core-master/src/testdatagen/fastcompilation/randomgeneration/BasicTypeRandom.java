package testdatagen.fastcompilation.randomgeneration;

import java.util.Random;

/**
 * Generate value random
 * 
 * @author ducanhnguyen
 *
 */
public class BasicTypeRandom {
	static final int SMALL_LOWER_BOUND = 0;
	static final int SMALL_UPPER_BOUND = 4;

	public static int generateInt(int minBound, int maxBound) {
		if (minBound <= 0 && maxBound >= 0) {
			return new Random().nextInt(maxBound - minBound + 1) + minBound;
		} else if (minBound >= 0) {
			return new Random().nextInt(maxBound - minBound + 1) - minBound;
		} else if (minBound <= 0 && maxBound <= 0) {
			return new Random().nextInt(-1 * minBound - (-1) * maxBound + 1) + minBound;
		} else
			return minBound;
	}

	public static double generateFloat(int minBound, int maxBound) {
		Random rand = new Random();
		int randomNum = generateInt(minBound, maxBound);
		int decimal = rand.nextInt(100);
		String output = randomNum + "." + decimal;
		return Double.parseDouble(output);
	}

	public static double generateSmallFloat(int minBound, int maxBound) {
		Random rand = new Random();
		int randomNum = generateInt(minBound, maxBound);
		int decimal = rand.nextInt(100);
		String output = randomNum + "." + decimal;
		return Double.parseDouble(output);
	}

	public static int generateSmallInt(int minBound, int maxBound) {
		if (maxBound <= SMALL_LOWER_BOUND) {
			// nothing to do
		} else if (minBound <= SMALL_LOWER_BOUND && SMALL_UPPER_BOUND <= maxBound) {
			minBound = SMALL_LOWER_BOUND;
			maxBound = SMALL_UPPER_BOUND;
		} else if (minBound <= SMALL_LOWER_BOUND && SMALL_UPPER_BOUND >= maxBound) {
			minBound = SMALL_LOWER_BOUND;
		} else if (minBound >= SMALL_LOWER_BOUND && SMALL_UPPER_BOUND <= maxBound) {
			maxBound = SMALL_UPPER_BOUND;
		}

		if (minBound <= 0 && maxBound >= 0) {
			return new Random().nextInt(maxBound - minBound + 1) - minBound;
		} else if (minBound >= 0) {
			return new Random().nextInt(maxBound - minBound + 1) + minBound;
		} else if (minBound <= 0 && maxBound <= 0) {
			return new Random().nextInt(-1 * minBound - (-1) * maxBound + 1) + minBound;
		} else
			return minBound;
	}

	public static void main(String[] args) {
		System.out.println(BasicTypeRandom.generateInt(-10, 100));
		System.out.println(BasicTypeRandom.generateInt(1, 100));
		System.out.println(BasicTypeRandom.generateInt(-12, -10));
	}
}
