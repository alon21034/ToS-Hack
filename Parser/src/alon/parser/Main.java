package alon.parser;

public class Main {

	public static void main(String[] params) {

		boolean isTrainging = false;
		String filePath = "image_features";

		for (String arg : params) {
			// System.out.println(arg);
			if (arg.startsWith("--training")) {
				isTrainging = true;
			} else {
				filePath = arg;
			}
		}

		Parser parser = new Parser();

		if (isTrainging) {
			parser.train();
		} else {
			parser.test(filePath);
			parser.generateOutputFile("output");
		}
	}
}
