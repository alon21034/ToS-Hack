package stimim.solver;

import java.util.Scanner;

import stimim.solver.Board.Gem;

public class Main {
	public static void main(String[] args) {
	  Scanner scanner = new Scanner(System.in);

	  Board board = new Board();
	  
	  for (int r = 0; r < Board.NROW; ++ r) {
	    for (int c = 0; c < Board.NCOL; ++ c) {
	      board.set(r, c, Gem.values()[scanner.nextInt()]);
	    }
	  }
	  
	  scanner.close();
	  
	  Solver solver = new Solver();
	  
	  solver.solve(board, 25, 20, true, 1000000, 2.0/3.0);
	}
}
