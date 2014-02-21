package alon.parser;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Parser {

	public static final String TRAINING_FILE_NAME = "training";
	public static final String TESTING_FILE_NAME = "testing";
	public static final String OUTPUT_FILE_NAME = "output";
	
	public static final int FEATURE_DIM = 27;
	public static final int GEM_NUMBER = 30;
	private static final int SCALED_SIZE = 40;

	private static final int[] TRAINING_FILES = {5, 10, 11};
	
	public Parser() {
		try {
			train();
			test();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void train() throws IOException {

		int[][] features = loadImages(getInputStringArray(TRAINING_FILES, "screen_data/", ".png"));
		int[] ys = loadAnswers(getInputStringArray(TRAINING_FILES, "screen_data/", "_result"));
		
		generateSVMfile(ys, features, TRAINING_FILE_NAME);
		
		String[] trainArgs = {"-c","2", TRAINING_FILE_NAME};  
		svm_train.main(trainArgs);
	}
	
	private void test() throws IOException {
		
		int[][] features = loadImages("screen_data/5.png");
		int[] ys = loadAnswers("screen_data/5_result");
		
		generateSVMfile(ys, features, TESTING_FILE_NAME);
		
		String[] testArgs = {TESTING_FILE_NAME, TRAINING_FILE_NAME + ".model", OUTPUT_FILE_NAME};
		svm_predict.main(testArgs);
	}
	
	private void generateSVMfile(int[] y, int[][] features, String filename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(filename);
		
		int index = 0;
		for (int[] xs : features) {
			out.print(y[index++]);
			out.print(" ");
			out.println(arrayToSparse(xs));
		}
		
		out.close();
	}
	
	private int[] loadColorAnswer() {
		int[] ret = new int[GEM_NUMBER];
		
		return ret;
	}
	
	private int[] loadEnhanceAnswer() {
		int[] ret = new int[GEM_NUMBER];
		
		return ret;
	}
	
	private int[] loadWitherAnswer() {
		int[] ret = new int[GEM_NUMBER];
				
		return ret;
	}
	
	private int[] loadAnswers(String... strs) throws IOException {
		int num = strs.length;
		int[][] ret = new int[num][GEM_NUMBER];
		
		int index = 0;
		for (String str : strs) {
			System.out.println("reading: " + str);
			Scanner in = new Scanner(new FileReader(str));
			for (int i = 0 ; i < GEM_NUMBER ; ++i) {
				ret[index][i] = in.nextInt();
			}
			index++;
		}
		return flatten(ret);
	}
	
	private int[][] loadImages(String... strs) throws IOException {
		int[][] ret = new int[strs.length * GEM_NUMBER][FEATURE_DIM];
		
		int index = 0;
		for (int count = 0 ; count < strs.length ; ++count) {
			System.out.println("reading: " + strs[count]);
			BufferedImage img = ImageIO.read(new File(strs[count]));
			// clip origin image
			
			int WIDTH = img.getWidth();
			int HEIGHT = img.getHeight();
			System.out.println("width:" + WIDTH + "   height:" + HEIGHT);
			
			int p = (int)WIDTH / 6; // width one slot.
			// p(0,0) to p(5, 4)
			// p(0,0) = (p, 1280 - 80 -9*p)
			// p(a, b) = (p+2p, 1200 - (9-2*b)*p)

			for (int i = 0 ; i < 6 ; i++) {
				for (int j = 0 ; j < 5 ; j++) {
					int x =  (WIDTH*i)/6;
					int y = (int) (HEIGHT - HEIGHT*0.078125 - (5-j) * WIDTH/6);

					BufferedImage subImage = img.getSubimage(x+p/4, y+p/4, p/2, p/2);
					ImageIO.write(subImage, "png", new File("tmp.png"));
					int[] features = retrieveFeature(subImage);
					for (int k = 0 ; k < FEATURE_DIM ; ++k) {
						ret[index][k] = features[k];
					}
					index++;
				}
			}
			
		}
		
		return ret;
	}
	
	private int[] retrieveFeature(BufferedImage image) {
		int[] ret = new int[FEATURE_DIM];
		int[] rs = new int[SCALED_SIZE*SCALED_SIZE];
		int[] gs = new int[SCALED_SIZE*SCALED_SIZE];
		int[] bs = new int[SCALED_SIZE*SCALED_SIZE];
		
		image = getScaledImage(image, SCALED_SIZE, SCALED_SIZE);
		int[] colors = image.getRGB(0, 0, SCALED_SIZE, SCALED_SIZE, null, 0, SCALED_SIZE);
		for (int i = 0 ; i < colors.length ; ++i) {
			rs[i] = getR(colors[i]);
			gs[i] = getG(colors[i]);
			bs[i] = getB(colors[i]);
		}
		
		int count = 0;
		for (int i = 10 ; i < 40 ; i+=10) {
			for (int j = 10 ; j < 40 ; j+=10) {
				
				int r = 0, g = 0, b = 0;
				
				for (int m = -3 ; m < 4 ; m++) {
					for (int n = -3 ; n < 4 ; n++) {
						int index = (i+m)*SCALED_SIZE + (j+n); 
						r += rs[index];
						g += gs[index];
						b += bs[index];
					}
				}
				
				ret[count++] = r;
				ret[count++] = g;
				ret[count++] = b;
			}
		}
		
		return ret;
	}
	
	private String arrayToSparse(int[] arr) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0 ; i < arr.length ; ++i) {
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
	    for(int i = 0; i < data.length; i++) {
	    	
	        for(int j = 0; j < data[i].length; j++){
	        	ret[index++] = data[i][j];
	        }
	    }
	    
	    return ret;
	}
	
	public static BufferedImage getScaledImage(BufferedImage image, int width, int height) {
	    int imageWidth  = image.getWidth();
	    int imageHeight = image.getHeight();

	    double scaleX = (double)width/imageWidth;
	    double scaleY = (double)height/imageHeight;
	    AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
	    AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

	    return bilinearScaleOp.filter(
	        image,
	        new BufferedImage(width, height, image.getType()));
	}
	
	public static int getR(int color) {
		return (color&0x00fc0000) >> 16;
	}

	public static int getG(int color) {
		return (color&0x0000fc00) >> 8;
	}

	public static int getB(int color) {
		return (color&0x000000fc) >> 0;
	}
	
	private String[] getInputStringArray(int[] arr, String preStr, String postStr) {
		String[] ret = new String[arr.length];
		
		int count = 0;
		for (int n : arr) {
			ret[count++] = preStr + n + postStr; 
		}
		return ret;
	}
}
