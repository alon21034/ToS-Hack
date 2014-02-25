package alon.parser;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Parser {

	public static final String TRAINING_FILE_NAME = "training";
	public static final String TESTING_FILE_NAME = "testing";
	public static final String OUTPUT_FILE_NAME = "output";
	public static final String FEATURE_FILE_NAME = "features";

	public static final int FEATURE_DIM = 27;
	public static final int GEM_NUMBER = 30;
	private static final int SCALED_SIZE = 40;

	private static final int FLAG_COLOR = 0;
	private static final int FLAG_ENHANCE = 1;
	private static final int FLAG_WITHER = 2;
	private static final int FLAG_PUZZLE = 3;

	private static final int[] TRAINING_FILES = { 1, 2, 3, 16, 17, 18, 19, 21,
			22, 25 };
	private static final int[] TESTING_FILES = { 5, 10, 11, 12, 20, 24 };

	public Parser() {

	}

	public void train() {

		System.out.println("start training...");

		float[][] features;
		try {
			features = loadImages(getInputStringArray(TRAINING_FILES,
					"Parser/screen_data/", ".png"));
			int[] ys0 = loadColorAnswer(getInputStringArray(TRAINING_FILES,
					"Parser/screen_data/", "_result"));
			int[] ys1 = loadEnhanceAnswer(getInputStringArray(TRAINING_FILES,
					"Parser/screen_data/", "_result"));
			int[] ys2 = loadWitherAnswer(getInputStringArray(TRAINING_FILES,
					"Parser/screen_data/", "_result"));

			int[] y = new int[ys0.length];
			for (int i = 0; i < y.length; i++)
				y[i] = ys0[i] + ys1[i] * 7;

			generateSVMfile(ys0, features, TRAINING_FILE_NAME + FLAG_COLOR);
			generateSVMfile(ys1, features, TRAINING_FILE_NAME + FLAG_ENHANCE);
			generateSVMfile(ys2, features, TRAINING_FILE_NAME + FLAG_WITHER);

			String[] trainArgs0 = { "-c", "15", "-t", "2", "-g", "0.5", "-q",
					TRAINING_FILE_NAME + FLAG_COLOR };
			svm_train.main(trainArgs0);
			String[] trainArgs1 = { "-c", "15", "-t", "0", "-g", "0.5", "-q",
					TRAINING_FILE_NAME + FLAG_ENHANCE };
			svm_train.main(trainArgs1);
			String[] trainArgs2 = { "-c", "10", "-t", "0", "-g", "0.5", "-q",
					TRAINING_FILE_NAME + FLAG_WITHER };
			svm_train.main(trainArgs2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void test() {

		float[][] features;
		try {
			features = loadImages(getInputStringArray(TESTING_FILES,
					"Parser/screen_data/", ".png"));
			int[] ys0 = loadColorAnswer(getInputStringArray(TESTING_FILES,
					"Parser/screen_data/", "_result"));
			int[] ys1 = loadEnhanceAnswer(getInputStringArray(TESTING_FILES,
					"Parser/screen_data/", "_result"));
			int[] ys2 = loadWitherAnswer(getInputStringArray(TESTING_FILES,
					"Parser/screen_data/", "_result"));

			int[] y = new int[ys0.length];
			for (int i = 0; i < y.length; i++)
				y[i] = ys0[i] + ys1[i] * 7;

			generateSVMfile(ys0, features, TESTING_FILE_NAME + FLAG_COLOR);
			generateSVMfile(ys1, features, TESTING_FILE_NAME + FLAG_ENHANCE);
			generateSVMfile(ys2, features, TESTING_FILE_NAME + FLAG_WITHER);

			String[] testArgs0 = { TESTING_FILE_NAME + FLAG_COLOR,
					TRAINING_FILE_NAME + FLAG_COLOR + ".model",
					OUTPUT_FILE_NAME + FLAG_COLOR };
			svm_predict.main(testArgs0);
			String[] testArgs1 = { TESTING_FILE_NAME + FLAG_ENHANCE,
					TRAINING_FILE_NAME + FLAG_ENHANCE + ".model",
					OUTPUT_FILE_NAME + FLAG_ENHANCE };
			svm_predict.main(testArgs1);
			String[] testArgs2 = { TESTING_FILE_NAME + FLAG_WITHER,
					TRAINING_FILE_NAME + FLAG_WITHER + ".model",
					OUTPUT_FILE_NAME + FLAG_WITHER };
			svm_predict.main(testArgs2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void test(String imagePath) {
		float[][] features;
		try {
			features = loadImages(imagePath);
			int[] ys0 = new int[GEM_NUMBER];
			int[] ys1 = new int[GEM_NUMBER];
			int[] ys2 = new int[GEM_NUMBER];

			generateSVMfile(ys0, features, FEATURE_FILE_NAME + FLAG_COLOR);
			generateSVMfile(ys1, features, FEATURE_FILE_NAME + FLAG_ENHANCE);
			generateSVMfile(ys2, features, FEATURE_FILE_NAME + FLAG_WITHER);

			String[] testArgs0 = { "-q", FEATURE_FILE_NAME + FLAG_COLOR,
					TRAINING_FILE_NAME + FLAG_COLOR + ".model",
					OUTPUT_FILE_NAME + FLAG_COLOR };
			svm_predict.main(testArgs0);
			String[] testArgs1 = { "-q", FEATURE_FILE_NAME + FLAG_ENHANCE,
					TRAINING_FILE_NAME + FLAG_ENHANCE + ".model",
					OUTPUT_FILE_NAME + FLAG_ENHANCE };
			svm_predict.main(testArgs1);
			String[] testArgs2 = { "-q", FEATURE_FILE_NAME + FLAG_WITHER,
					TRAINING_FILE_NAME + FLAG_WITHER + ".model",
					OUTPUT_FILE_NAME + FLAG_WITHER };
			svm_predict.main(testArgs2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateOutputFile(String str) {
		int[][] result = new int[3][GEM_NUMBER];

		try {
			Scanner scanner = new Scanner(new File(OUTPUT_FILE_NAME
					+ FLAG_COLOR));
			for (int i = 0; i < GEM_NUMBER; ++i)
				result[FLAG_COLOR][i] = (int) scanner.nextFloat();
			scanner = new Scanner(new File(OUTPUT_FILE_NAME + FLAG_ENHANCE));
			for (int i = 0; i < GEM_NUMBER; ++i)
				result[FLAG_ENHANCE][i] = (int) scanner.nextFloat();
			scanner = new Scanner(new File(OUTPUT_FILE_NAME + FLAG_WITHER));
			for (int i = 0; i < GEM_NUMBER; ++i)
				result[FLAG_WITHER][i] = (int) scanner.nextFloat();

			PrintWriter out = new PrintWriter(str);
			for (int i = 0; i < GEM_NUMBER; ++i) {
				out.print(result[FLAG_COLOR][i] | result[FLAG_WITHER][i] << 4
						| result[FLAG_ENHANCE][i] << 8);
				out.print(" ");
				if (i % 6 == 5)
					out.println();
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void generateSVMfile(int[] y, float[][] features, String filename)
			throws FileNotFoundException {
		PrintWriter out = new PrintWriter(filename);

		int index = 0;
		for (float[] xs : features) {
			out.print(y[index++]);
			out.print(" ");
			out.println(arrayToSparse(xs));
		}

		out.close();
	}

	private int[] loadColorAnswer(String... strs) throws IOException {
		return loadAnswers(FLAG_COLOR, strs);
	}

	private int[] loadEnhanceAnswer(String... strs) throws IOException {
		return loadAnswers(FLAG_ENHANCE, strs);
	}

	private int[] loadWitherAnswer(String... strs) throws IOException {
		return loadAnswers(FLAG_WITHER, strs);
	}

	private int[] loadAnswers(int flag, String... strs) throws IOException {
		int num = strs.length;
		int[][] ret = new int[num][GEM_NUMBER];

		int index = 0;
		for (String str : strs) {
			System.out.println("reading: " + str);
			Scanner in = new Scanner(new FileReader(str));
			for (int i = 0; i < GEM_NUMBER * flag; ++i) {
				in.nextInt();
			}

			for (int i = 0; i < GEM_NUMBER; ++i) {
				ret[index][i] = in.nextInt();
			}
			index++;
		}
		return flatten(ret);
	}

	private float[][] loadImages(String... strs) throws IOException {
		float[][] ret = new float[strs.length * GEM_NUMBER][FEATURE_DIM];

		int index = 0;
		for (int count = 0; count < strs.length; ++count) {
			System.out.println("reading: " + strs[count]);
			BufferedImage img = ImageIO.read(new File(strs[count]));
			// clip origin image

			int WIDTH = img.getWidth();
			int HEIGHT = img.getHeight();
			// System.out.println("width:" + WIDTH + "   height:" + HEIGHT);

			int p = (int) WIDTH / 6; // width one slot.
			// p(0,0) to p(5, 4)
			// p(0,0) = (p, 1280 - 80 -9*p)
			// p(a, b) = (p+2p, 1200 - (9-2*b)*p)

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 6; j++) {
					int x = (WIDTH * j) / 6;
					int y = (int) (HEIGHT - HEIGHT * 0.078125 - (5 - i) * WIDTH
							/ 6);

					BufferedImage subImage = img.getSubimage(x + p / 4, y + p
							/ 4, p / 2, p / 2);
					float[] features = retrieveFeature(subImage);
					for (int k = 0; k < FEATURE_DIM; ++k) {
						ret[index][k] = features[k];
					}
					index++;
				}
			}

		}

		return ret;
	}

	private float[] retrieveFeature(BufferedImage image) {
		float[] ret = new float[FEATURE_DIM];
		int[] rs = new int[SCALED_SIZE * SCALED_SIZE];
		int[] gs = new int[SCALED_SIZE * SCALED_SIZE];
		int[] bs = new int[SCALED_SIZE * SCALED_SIZE];

		image = getScaledImage(image, SCALED_SIZE, SCALED_SIZE);
		int[] colors = image.getRGB(0, 0, SCALED_SIZE, SCALED_SIZE, null, 0,
				SCALED_SIZE);
		for (int i = 0; i < colors.length; ++i) {
			rs[i] = getR(colors[i]);
			gs[i] = getG(colors[i]);
			bs[i] = getB(colors[i]);
		}

		int count = 0;
		for (int i = 10; i < 40; i += 10) {
			for (int j = 10; j < 40; j += 10) {

				int r = 0, g = 0, b = 0;

				for (int m = -3; m < 4; m++) {
					for (int n = -3; n < 4; n++) {
						int index = (i + m) + (j + n) * SCALED_SIZE;
						r += rs[index];
						g += gs[index];
						b += bs[index];
					}
				}

				ret[count++] = (float) r / 49 / 256;
				ret[count++] = (float) g / 49 / 256;
				ret[count++] = (float) b / 49 / 256;
			}
		}

		return ret;
	}

	private String arrayToSparse(float[] arr) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < arr.length; ++i) {
			if (arr[i] != 0) {
				sb.append(i);
				sb.append(":");
				sb.append(arr[i]);
				sb.append(" ");
			}
		}

		return sb.toString();
	}

	public static int[] flatten(int[][] data) {

		int[] ret = new int[data.length * data[0].length];
		int index = 0;
		for (int i = 0; i < data.length; i++) {

			for (int j = 0; j < data[i].length; j++) {
				ret[index++] = data[i][j];
			}
		}

		return ret;
	}

	public static BufferedImage getScaledImage(BufferedImage image, int width,
			int height) {
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double) width / imageWidth;
		double scaleY = (double) height / imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(
				scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(
				scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		return bilinearScaleOp.filter(image, new BufferedImage(width, height,
				image.getType()));
	}

	public static int getR(int color) {
		return (color & 0x00ff0000) >> 16;
	}

	public static int getG(int color) {
		return (color & 0x0000ff00) >> 8;
	}

	public static int getB(int color) {
		return (color & 0x000000ff) >> 0;
	}

	private String[] getInputStringArray(int[] arr, String preStr,
			String postStr) {
		String[] ret = new String[arr.length];

		int count = 0;
		for (int n : arr) {
			ret[count++] = preStr + n + postStr;
		}
		return ret;
	}
}
