package alon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainFrame implements ActionListener {

	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 400;

	public JButton button;

	public MainFrame() {
		JFrame frame = new JFrame();
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		button = new JButton("button");
		button.setSize(500, 50);
		button.setLocation(50, 300);
		button.addActionListener(this);

		frame.getContentPane().add(button);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String result = null;
		try {
			Process ps = Runtime.getRuntime().exec("sh main.sh 1");
			InputStream is = ps.getInputStream();
			
			BufferedReader in =
			        new BufferedReader(new InputStreamReader(is));
			    String inputLine;
			    while ((inputLine = in.readLine()) != null) {
			        System.out.println(inputLine);
			        result += inputLine;
			    }
			    in.close();
			    System.out.println("finish!!");
		} catch (IOException e1) {
			System.out.print(e1);
		}
	}
}
