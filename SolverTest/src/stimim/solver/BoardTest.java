package stimim.solver;

import junit.framework.Assert;

import org.junit.Test;


public class BoardTest {
  private static final int G = 2;
  private static final int W = 1;
  private static final int F = 0;
  private static final int _ = -W;

  @Test
  public void testComputeCombo() {
    Board board = new Board();
    Damages damages = new Damages();

    int[] data = new int[] {
        _, G, W, F, _, _,
        _, G, W, W, _, _,
        _, W, F, F, F, 6,
        _, G, W, W, 6, 6,
        6, 6, 6, 6, 6, 6,
    };

    setBoard(board, data);

    Assert.assertEquals(3, board.computeCombo(damages));
    Assert.assertEquals(3, damages.getCombo());
    Assert.assertEquals(3, damages.get(F).normal);
    Assert.assertEquals(5, damages.get(W).normal);
    Assert.assertEquals(true, damages.get(W).isMultiAttack);
    Assert.assertEquals(3, damages.get(G).normal);
  }

  @Test
  public void testComputeComboAndRemove() {
    Board board = new Board();

    int[] data = new int[] {
        _, G, _, _, _, 6,
        _, G, W, _, _, 6,
        _, W, W, W, _, 6,
        _, G, W, W, F, F,
        F, F, F, F, F, F,
    };

    setBoard(board, data);

    Assert.assertEquals(3, board.computeComboAndRemove(null));
    Assert.assertEquals("___________?_____?_____?___wff", board.toString());
  }

  @Test
  public void testToString() {
    Board board = new Board();

    int[] data = new int[] {
        _, G, _, _, _, _,
        _, G, W, _, _, _,
        _, W, W, W, _, _,
        _, G, W, _, _, _,
        _, _, _, _, _, _,
    };

    setBoard(board, data);

    Assert.assertEquals("_g_____gw____www___gw_________", board.toString());

  }

  private void setBoard(Board board, int[] data) {
    for (int r = 0; r < Board.NROW; ++r) {
      for (int c = 0; c < Board.NCOL; ++c) {
        board.set(r, c, data[r * Board.NCOL + c]);
      }
    }
  }

}
