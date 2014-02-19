package alon.parser.utils;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
	public static final int FEATURE_DIM = 1200;
	
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

		int[][] features = loadImages("screen_data/5.png", "screen_data/10.png");
		int[] ys = loadAnswers("screen_data/5_result", "screen_data/10_result");
		
		generateSVMfile(ys, features, TRAINING_FILE_NAME);
		
		String[] trainArgs = {"-c","3", TRAINING_FILE_NAME};  
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
	
	private int[] loadAnswers(String... strs) throws IOException {
		int num = strs.length;
		int[][] ret = new int[num][30];
		
		int index = 0;
		for (String str : strs) {
			System.out.println("reading: " + str);
			Scanner in = new Scanner(new FileReader(str));
			for (int i = 0 ; i < 30 ; ++i) {
				ret[index][i] = in.nextInt();
			}
			index++;
		}
		return flatten(ret);
	}
	
	private int[][] loadImages(String... strs) throws IOException {
		int[][] ret = new int[strs.length * 30][FEATURE_DIM];
		
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
		double factor = 20.0f / image.getWidth();
		image = scaleImage(image, image.getType(), 20, 20, factor, factor);
		int[] colors = image.getRGB(0, 0, 20, 20, null, 0, 20);
		for (int i = 0 ; i < colors.length ; ++i) {
			ret[3 * i] = getR(colors[i]);
			ret[3 * i + 1] = getG(colors[i]);
			ret[3 * i + 2] = getB(colors[i]);
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
	
	public static BufferedImage scaleImage(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
	    BufferedImage dbi = null;
	    if(sbi != null) {
	        dbi = new BufferedImage(dWidth, dHeight, imageType);
	        Graphics2D g = dbi.createGraphics();
	        AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
	        g.drawRenderedImage(sbi, at);
	    }
	    return dbi;
	}
	
	public int getR(int color) {
		return (color&0x00fc0000) >> 18;
	}

	public int getG(int color) {
		return (color&0x0000fc00) >> 10;
	}

	public int getB(int color) {
		return (color&0x000000fc) >> 2;
	}
}
