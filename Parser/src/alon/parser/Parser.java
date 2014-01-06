package alon.parser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Parser {
	
	private BufferedImage img;
	
	public Parser(String imagePath) {
		
		try {
	        img = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
        	System.out.print("read image error\n");
	        e.printStackTrace();
        }
		
	}
	
}
