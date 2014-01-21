package alon.parser;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class Parser {

	public Parser() {
		try {
			imread("1.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	svm_parameter _param;
	svm_problem _prob;
	String _model_file;

	protected void loadData(boolean is_training) {

		int[][] train_x = new int[30][675];
		int[]	train_y = new int[30];
		
		_prob = new svm_problem();

		_prob.l = 30;
		_prob.x = new svm_node[_prob.l][];
		for (int i = 0; i < _prob.l; i++)
			_prob.x[i] = train_x[i]; // �x�s�C��node���V�q
		_prob.y = new double[_prob.l];
		for (int i = 0; i < _prob.l; i++)
			_prob.y[i] = train_y[i];

		System.out.println("Done!!");
	}

	protected void training() {
		loadData(true); // �o��I�sloadData()�A�ϥ�true�ѼƬO�]���btraining���q
		// �z�LloadData�A�N��Ʈw������x�s�b�����ܼ�_prob�̭�

		System.out.print("Training...");
		String _model_file = "svm_model.txt"; // ���wSVM model�x�s���ɮצW��

		try {
			svm_model model = svm.svm_train(_prob, _param); // �V�mSVM model
			System.out.println("Done!!");
			svm.svm_save_model(_model_file, model); // �N�V�m���G�g�J�ɮ�
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void testing() {
		loadData(false); // Ū���ѤU��300����ơA�ഫ��SVM���D(�s�b_prob��)

		svm_model model;
		int correct = 0, total = 0;
		try {
			model = svm.svm_load_model(_model_file); // ���Jmodel

			for (int i = 0; i < _prob.l; i++) { // ��problem �̪��C��SVM node
				double v;
				svm_node[] x = _prob.x[i]; // ���Xsvm node
				v = svm.svm_predict(model, x); // ��node�����w����
				// �o�ɹw�����|�̷�model�Pnode�����V�q��T�A���͹w�����ƭ�(-1��1)
				total++;
				if (v == _prob.y[i])
					correct++; // �p�G�򥿽T���פ@�ˡA�h���T�ƥ[�@
			}

			double accuracy = (double) correct / total * 100;
			System.out.println("Accuracy = " + accuracy + "% (" + correct + "/"
					+ total + ")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void imread(String filename) throws IOException {
		BufferedImage img = ImageIO.read(new File(filename));

		System.out.print(img.getWidth() + "  " + img.getHeight());
		
		int w = img.getWidth();
		int h = img.getHeight();

		int gem_width = (int) (0.0938f * h);
		int start_h = (int) (0.453f * h);

		
		BufferedImage pic1 = img.getSubimage(0, start_h, w, 5*gem_width);
		
		for (int i = 0 ; i < 5 ; i++) {
			for (int j = 0 ; j < 6 ; j++) {
				getFeatures(gem_width, pic1, i, j);
			}
		}
	}

	private int[] getFeatures(int gem_width, BufferedImage pic1, int i, int j) throws IOException {
		
		int[] ret = new int[15*15*3];
		
		BufferedImage pic = pic1.getSubimage(j*gem_width, i*gem_width, gem_width, gem_width);
		pic = pic.getSubimage(gem_width/4, gem_width/4, gem_width/2, gem_width/2);
		pic = getScaledImage(pic, 15, 15);
		
		
		int index = 0;
		for (int x = 0 ; x < 15 ; x++) {
			for (int y = 0 ; y < 15 ; y++) {
				int color = pic.getRGB(x, y);
				ret[index] = getR(color);
				ret[index+1] = getG(color);
				ret[index+2] = getB(color);
				index+=3;
			}
		}
		return ret;
	}

	public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
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
