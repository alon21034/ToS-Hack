package alon.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;

public class Main extends JFrame {

	private JPanel contentPane;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("Exit");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnNewButton.setBounds(327, 243, 117, 29);
		contentPane.add(btnNewButton);
		
		JPanel panel = new JPanel();
		panel.setBounds(214, 6, 230, 208);
		contentPane.add(panel);
		
		JRadioButton singleAtkRadioButton = new JRadioButton("Single attack");
		singleAtkRadioButton.setSelected(true);
		buttonGroup.add(singleAtkRadioButton);
		singleAtkRadioButton.setBounds(6, 6, 141, 23);
		contentPane.add(singleAtkRadioButton);
		
		JRadioButton multiAtkRadioButton_1 = new JRadioButton("Multi attack");
		buttonGroup.add(multiAtkRadioButton_1);
		multiAtkRadioButton_1.setBounds(6, 41, 141, 23);
		contentPane.add(multiAtkRadioButton_1);
		
		JButton startButton = new JButton("Start");
		startButton.setBounds(6, 243, 117, 29);
		contentPane.add(startButton);
	}
}
