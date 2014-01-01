package stimim.solver;

import junit.framework.Assert;

import org.junit.Test;

import stimim.solver.Board.Gem;

public class BoardTest {

  @Test
  public void testComputeCombo() {
    Board board = new Board();

    int[] data = new int[] {
        6, 2, 6, 6, 6, 6,
        6, 2, 1, 6, 6, 6,
        6, 1, 1, 1, 6, 6,
        6, 2, 1, 1, 6, 6,
        6, 6, 6, 6, 6, 6,
    };

    setBoard(board, data);

    Assert.assertEquals(2, board.computeCombo());
  }

  @Test
  public void testComputeComboAndRemove() {
    Board board = new Board();

    int[] data = new int[] {
        6, 2, 6, 6, 6, 6,
        6, 2, 1, 6, 6, 6,
        6, 1, 1, 1, 6, 6,
        6, 2, 1, 1, 0, 0,
        0, 0, 0, 0, 0, 0,
    };

    setBoard(board, data);

    Assert.assertEquals(3, board.computeComboAndRemove());
    Assert.assertEquals("666666666666666666666666666100", board.toString());
  }

  @Test
  public void testToString() {
    Board board = new Board();

    int[] data = new int[] {
        6, 2, 6, 6, 6, 6,
        6, 2, 1, 6, 6, 6,
        6, 1, 1, 1, 6, 6,
        6, 2, 1, 6, 6, 6,
        6, 6, 6, 6, 6, 6,
    };

    setBoard(board, data);

    Assert.assertEquals("626666621666611166621666666666", board.toString());

  }

  private void setBoard(Board board, int[] data) {
    for (int r = 0; r < Board.NROW; ++r) {
      for (int c = 0; c < Board.NCOL; ++c) {
        board.set(r, c, Gem.values()[data[r * Board.NCOL + c]]);
      }
    }
  }

}
