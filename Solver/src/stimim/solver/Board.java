package stimim.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import stimim.solver.GemUtil.BaseGem;

public class Board {
  public static final int NROW = 5;
  public static final int NCOL = 6;

  private final int[] data = new int[NROW * NCOL];

  public int get(int r, int c) {
    return data[r * NCOL + c];
  }

  public Board set(int r, int c, int g) {
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
    int[] counter = new int[GemUtil.N_COLOR];

    for (int g : data) {
      if (GemUtil.isUnknownOrNothing(g)) {
        continue;
      }
      counter[GemUtil.getBaseGem(g).ordinal()]++;
    }

    int acc = 0;
    for (int v : counter) {
      acc += v / 3;
    }
    return acc;
  }

  public int computeComboAndRemove(Damages damages) {
    int acc = 0;
    int count;

    while ((count = remove_connects(damages)) > 0) {
      acc += count;
    }
    return acc;
  }

  public int computeCombo(Damages damages) {
    return dup().computeComboAndRemove(damages);
  }

  private int remove_connects(Damages damages) {
    int count = 0;
    boolean[] to_remove = new boolean[data.length];

    // mark to_remove
    for (int r = 0; r < NROW; ++r) {
      for (int c = 0; c < NCOL; ++c) {
        int g = get(r, c);

        if (GemUtil.isUnknownOrNothing(g)) {
          continue;
        }

        if (c + 2 < NCOL) {
          if (GemUtil.getBaseGem(g) == GemUtil.getBaseGem(get(r, c + 1)) &&
              GemUtil.getBaseGem(g) == GemUtil.getBaseGem(get(r, c + 2))) {
            to_remove[r * NCOL + c] = true;
            to_remove[r * NCOL + c + 1] = true;
            to_remove[r * NCOL + c + 2] = true;
          }
        }
        if (r + 2 < NROW) {
          if (GemUtil.getBaseGem(g) == GemUtil.getBaseGem(get(r + 1, c)) &&
              GemUtil.getBaseGem(g) == GemUtil.getBaseGem(get(r + 2, c))) {
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
        int g = get(r, c);
        if (GemUtil.isUnknownOrNothing(g)) {
          continue;
        }
        BaseGem gem = GemUtil.getBaseGem(g);
        if (to_remove[r * NCOL + c]) {
          List<Integer> queue = new ArrayList<Integer>();
          queue.add(r * NCOL + c);
          count++;
          if (damages != null) {
            damages.addCombo(1);
            damages.get(g).combo++;
          }

          int numRemoved = 0;
          for (int i = 0; i < queue.size(); ++i) {
            int v = queue.get(i);
            int _r = v / NCOL;
            int _c = v % NCOL;

            if (GemUtil.isUnknownOrNothing(data[v])) {
              continue;
            }
            if (damages != null) {
              int x = data[v];
              Damages.RemovedGem removedGem = damages.get(x);
              if (GemUtil.isEnhanced(x)) {
                removedGem.enhanced++;
              } else {
                removedGem.normal++;
              }
            }

            data[v] = GemUtil.NOTHING;
            numRemoved++;

            if (in_range(_r - 1, _c) && GemUtil.getBaseGem(get(_r - 1, _c)) == gem
                && to_remove[to_index(_r - 1, _c)]) {
              queue.add(to_index(_r - 1, _c));
            }
            if (in_range(_r + 1, _c) && GemUtil.getBaseGem(get(_r + 1, _c)) == gem
                && to_remove[to_index(_r + 1, _c)]) {
              queue.add(to_index(_r + 1, _c));
            }
            if (in_range(_r, _c - 1) && GemUtil.getBaseGem(get(_r, _c - 1)) == gem
                && to_remove[to_index(_r, _c - 1)]) {
              queue.add(to_index(_r, _c - 1));
            }
            if (in_range(_r, _c + 1) && GemUtil.getBaseGem(get(_r, _c + 1)) == gem
                && to_remove[to_index(_r, _c + 1)]) {
              queue.add(to_index(_r, _c + 1));
            }
          }
          if (damages != null && numRemoved >= 5) {
            Damages.RemovedGem removedGem = damages.get(g);
            removedGem.isMultiAttack = true;
          }
        }
      }
    }

    // drop
    for (int c = 0; c < NCOL; ++c) {
      int i = NROW - 1;
      for (int r = NROW - 1; r >= 0; --r) {
        if (!GemUtil.isNothing(get(r, c))) {
          set(i, c, get(r, c));
          i--;
        }
      }
      for (; i >= 0; --i) {
        set(i, c, GemUtil.NOTHING);
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
      chars[i] = GemUtil.toChar(data[i]);
    }

    return (new String(chars)).intern();
  }

  public void print(Logger logger, Level level) {
    String msg = "\n-----------\n";
    for (int r = 0; r < NROW; ++r) {
      for (int c = 0; c < NCOL; ++c) {
        if (!GemUtil.isNothing(data[to_index(r, c)])) {
          msg += String.format("%c ", GemUtil.toChar(data[to_index(r, c)]));
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
