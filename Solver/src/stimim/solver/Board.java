package stimim.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Board {
  public static final int NROW = 5;
  public static final int NCOL = 6;

  private final Gem[] data = new Gem[NROW * NCOL];

  public Gem get(int r, int c) {
    return data[r * NCOL + c];
  }

  public Board set(int r, int c, Gem g) {
    data[r * NCOL + c] = g;
    return this;
  }

  public Board dup() {
    Board ret = new Board();
    for (int i = 0; i < data.length; ++i) {
      ret.data[i] = data[i];
    }
    return ret;
  }

  public int computeComboUpperBound() {
    int[] counter = new int[Gem.NCOLORS];

    for (Gem g : data) {
      if (g.ordinal() < Gem.NCOLORS) {
        counter[g.ordinal()]++;
      }
    }

    int acc = 0;
    for (int v : counter) {
      acc += v / 3;
    }
    return acc;
  }

  public int computeComboAndRemove() {
    int acc = 0;
    int count;

    while ((count = remove_connects()) > 0) {
      acc += count;
    }
    return acc;
  }

  public int computeCombo() {
    return dup().computeComboAndRemove();
  }

  private int remove_connects() {
    int count = 0;
    boolean[] to_remove = new boolean[data.length];

    // mark to_remove
    for (int r = 0; r < NROW; ++r) {
      for (int c = 0; c < NCOL; ++c) {
        Gem g = get(r, c);

        if (g == Gem.NOTHING) {
          continue;
        }

        if (c + 2 < NCOL) {
          if (g == get(r, c + 1) && g == get(r, c + 2)) {
            to_remove[r * NCOL + c] = true;
            to_remove[r * NCOL + c + 1] = true;
            to_remove[r * NCOL + c + 2] = true;
          }
        }
        if (r + 2 < NROW) {
          if (g == get(r + 1, c) && g == get(r + 2, c)) {
            to_remove[r * NCOL + c] = true;
            to_remove[(r + 1) * NCOL + c] = true;
            to_remove[(r + 2) * NCOL + c] = true;
          }
        }
      }
    }

    // count and remove
    for (int r = 0; r < NROW; ++r) {
      for (int c = 0; c < NCOL; ++c) {
        Gem g = get(r, c);
        if (g == Gem.NOTHING) {
          continue;
        }
        if (to_remove[r * NCOL + c]) {
          List<Integer> queue = new ArrayList<Integer>();
          queue.add(r * NCOL + c);
          count++;

          for (int i = 0; i < queue.size(); ++i) {
            int v = queue.get(i);
            int _r = v / NCOL;
            int _c = v % NCOL;

            data[v] = Gem.NOTHING;

            if (in_range(_r - 1, _c) && get(_r - 1, _c) == g && to_remove[to_index(_r - 1, _c)]) {
              queue.add(to_index(_r - 1, _c));
            }
            if (in_range(_r + 1, _c) && get(_r + 1, _c) == g && to_remove[to_index(_r + 1, _c)]) {
              queue.add(to_index(_r + 1, _c));
            }
            if (in_range(_r, _c - 1) && get(_r, _c - 1) == g && to_remove[to_index(_r, _c - 1)]) {
              queue.add(to_index(_r, _c - 1));
            }
            if (in_range(_r, _c + 1) && get(_r, _c + 1) == g && to_remove[to_index(_r, _c + 1)]) {
              queue.add(to_index(_r, _c + 1));
            }
          }
        }
      }
    }

    // drop
    for (int c = 0; c < NCOL; ++c) {
      int i = NROW - 1;
      for (int r = NROW - 1; r >= 0; --r) {
        if (get(r, c) != Gem.NOTHING) {
          set(i, c, get(r, c));
          i--;
        }
      }
      for (; i >= 0; --i) {
        set(i, c, Gem.NOTHING);
      }
    }

    return count;
  }

  private int to_index(int r, int c) {
    return r * NCOL + c;
  }

  boolean in_range(int r, int c) {
    return r >= 0 && r < NROW && c >= 0 && c < NCOL;
  }

  @Override
  public String toString() {
    char[] chars = new char[data.length];

    for (int i = 0; i < data.length; ++i) {
      chars[i] = (char) (data[i].ordinal() + '0');
    }

    return (new String(chars)).intern();
  }

  public enum Gem {
    FIRE,
    WATER,
    GRASS,
    LIGHT,
    DARK,
    HEART,
    NOTHING;

    public static int NCOLORS = 6;
  }

  public void print(Logger logger, Level level) {
    String msg = "\n-----------\n";
    for (int r = 0; r < NROW; ++r) {
      for (int c = 0; c < NCOL; ++c) {
        if (data[to_index(r, c)] != Gem.NOTHING) {
          msg += String.format("%d ", data[to_index(r, c)].ordinal());
        } else {
          msg += "  ";
        }
      }
      msg += "\n";
    }
    msg += "-----------";
    logger.log(level, msg);
  }
}
