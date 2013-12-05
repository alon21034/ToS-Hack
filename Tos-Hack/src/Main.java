import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;


public class Main {

	enum Type{
		Heart, SHeart,
		Light, SLight,
		Dark, SDark,
		Fire, SFire,
		Wood, SWood,
		Water, SWater,

		Unknown
		// need add unknown
	}

	final int[][] values = {
			{54, 19, 38}, {62, 44, 49}, // Heart
			{50, 37, 0}, {61, 50, 14}, // Light
			{47, 8, 52}, {60, 26, 61}, // Dark
			{50, 8, 2}, {61, 32, 21}, // Fire
			{6, 43, 8}, {10, 58, 12}, // Wood
			{15, 38, 51}, {33, 53, 62}  // Water
	};

	public static void main(String[] str) {
		if (str == null || str.length == 0) {
			Main main = new Main("screen.png");
		} else {
			Main main = new Main(str[0]);
		}
	}

	public Main(String path) {
		// read image.
		BufferedImage img = null;
		try {
	        img = ImageIO.read(new File(path));
        } catch (IOException e) {
	        e.printStackTrace();
        }

		int res[][] = new int[6][5];

		// show color
		int WIDTH = img.getWidth();
		int HEIGHT = img.getHeight();
		_("width:" + WIDTH + "   height:" + HEIGHT);

		float p = (float)WIDTH / 12; // half of one slot.
		// p(0,0) to p(5, 4)
		// p(0,0) = (p, 1280 - 80 -9*p)
		// p(a, b) = (p+2p, 1200 - (9-2*b)*p)
		for (int i = 0 ; i < 6 ; i++) {
			for (int j = 0 ; j < 5 ; j++) {
				// int w = (int)((1+2*i)*p);
				int w =  WIDTH/12 + WIDTH*i/6;
				int h = HEIGHT - HEIGHT/16 -WIDTH/12 - (4-j) * WIDTH/6;
				// int h = (int)(HEIGHT-HEIGHT/16 - (9-2*j)*p);
				
				int r=0, g=0, b=0;
				for (int m = -7 ; m < 8 ; m++) {
					for (int n = -7 ; n < 8 ; n++) {
						int c = img.getRGB((int) (w+m*p/15), (int) (h+n*p/15));
						r += getR(c);
						g += getG(c);
						b += getB(c);
					}
				}
				r/=225;
				g/=225;
				b/=225;

//				_("(" + i + " ," + j+ "): " + r + " " + g + " " + b);

				res[i][j] = (getType(new int[]{r,g,b}));
			}
		}

		PrintWriter out;
        try {
	        out = new PrintWriter("board");

	        for (int i = 0 ; i < 5 ; ++i) {
	        	for (int j = 0 ; j < 6 ; ++j) {
	        		out.print(TypeToChar(res[j][i]) + "  ");
	        		System.out.print(TypeToChar(res[j][i]) + "  ");
	        	}
	        	out.print("\n");
	        	_("");
	        }
	        out.close();
        } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }

	}

	public int getType(int[] input){

		int min = 0, mindiff = 1000;
		for (int i = 0 ; i < 12 ; i ++) {
			int[] ans = values[i];

			int diff = 0;
			for (int j = 0 ; j < 3 ; j++)
				diff += Math.abs(input[j] - ans[j]);

			if (diff < mindiff) {
				min = i;
				mindiff = diff;
			}
		}
		return min;
	}

	public void _(String str) {
		System.out.print(str+"\n");
	}

	public int getA(int color) {
		return (color&0xff000000) >> 24;
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

	public Type intToType(int n) {
		switch (n) {
		case 0: return Type.Heart;
		case 1: return Type.SHeart;
		case 2: return Type.Light;
		case 3: return Type.SLight;
		case 4: return Type.Dark;
		case 5: return Type.SDark;
		case 6: return Type.Fire;
		case 7: return Type.SFire;
		case 8: return Type.Wood;
		case 9: return Type.SWood;
		case 10: return Type.Water;
		case 11: return Type.SWater;
		default: return Type.Unknown;
		}
	}

	public char TypeToChar(int n){
		switch (n) {
		case 0: return '0';
		case 1: return '0';
		case 2: return '1';
		case 3: return '1';
		case 4: return '2';
		case 5: return '2';
		case 6: return '3';
		case 7: return '3';
		case 8: return '4';
		case 9: return '4';
		case 10: return '5';
		case 11: return '5';
		default: return '6';
		}
	}
}
