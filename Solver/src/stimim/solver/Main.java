package stimim.solver;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    int maxStep = 25;
    int queueSize = 1000000;
    int ratioNum = 2;
    int ratioDen = 3;
    int desiredStep = 20;
    /**
     * parse args
     */
    for (String arg : args) {
      System.out.println(arg);
      if (arg.startsWith("--max_step=")) {
        maxStep = Integer.valueOf(arg.substring("--max_step=".length()));
      } else if (arg.startsWith("--queue_size=")) {
        queueSize = Integer.valueOf(arg.substring("--queue_size=".length()));
      } else if (arg.startsWith("--ratio_num=")) {
        ratioNum = Integer.valueOf(arg.substring("--ratio_num=".length()));
      } else if (arg.startsWith("--ratio_den=")) {
        ratioDen = Integer.valueOf(arg.substring("--ratio_den=".length()));
      } else if (arg.startsWith("--desired_step=")) {
        desiredStep = Integer.valueOf(arg.substring("--desired_step=".length()));
      }
    }

    Scanner scanner = new Scanner(System.in);

    Board board = new Board();

    for (int r = 0; r < Board.NROW; ++r) {
      for (int c = 0; c < Board.NCOL; ++c) {
        board.set(r, c, scanner.nextInt());
      }
    }

    scanner.close();

    Solver solver = new Solver();

    solver.solve(board, maxStep, desiredStep, true, queueSize, (double) ratioNum / ratioDen);
  }
}
