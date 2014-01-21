package stimim.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Solver {
  private static final Logger logger = Logger.getLogger(Solver.class.getName());
  private static final Level level = Level.INFO;

  public int solve(Board initBoard, int maxMove, int desiredMove, boolean canMoveDiagonal,
      int maxCreated, double pruneRatio) {
    logger.setLevel(Level.ALL);
    int comboUpperBound = initBoard.computeComboUpperBound();
    Step.MAX_COMBO = comboUpperBound;

    final Comparator<Step> comparator =
        new Comparator<Step>() {
          @Override
          public int compare(Step a, Step b) {
            return -Double.compare(a.score, b.score);
          }
        };
    PriorityQueue<Step> queue = new PriorityQueue<Step>(Board.NCOL * Board.NROW, comparator);

    for (int r = 0; r < Board.NROW; ++r) {
      for (int c = 0; c < Board.NCOL; ++c) {
        queue.add(new Step(initBoard, null, r, c));
      }
    }

    Step best = null;
    Map<String, Step> caches = new HashMap<String, Step>();
    int nCreated = 0;
    int[] maxComboForNumMoves = new int[maxMove + 1];
    int atThisBest = 0;

    logger.log(level, String.format("ComboUpperBound = %d\n", comboUpperBound));
    while (!queue.isEmpty()) {
      Step s = queue.poll();
      if (best == null || s.damage > best.damage ||
          (s.damage == best.damage && s.moves < best.moves)) {
        logger.log(level,
            String.format("Find %f damage, %d combos in %d moves\n", s.damage, s.combo, s.moves));
        best = s;
        atThisBest = 0;
      } else if (best.combo == comboUpperBound) {
        if (s.combo == best.combo && s.damage < best.damage) {
          atThisBest++;
        }
        if (best.moves <= desiredMove || atThisBest > 100) {
          break;
        }
      }

      if (maxComboForNumMoves[s.moves] < s.combo) {
        maxComboForNumMoves[s.moves] = s.combo;
      }

      if (s.moves == maxMove || nCreated >= maxCreated) {
        continue;
      }

      if (maxComboForNumMoves[s.moves] > 2 &&
          s.combo < maxComboForNumMoves[s.moves] * pruneRatio) {
        continue;
      }

      int r = s.r;
      int c = s.c;
      Board board = s.board;

      if (s.prev != null) {
        int pr = s.prev.r;
        int pc = s.prev.c;

        if (GemUtil.isWeathered(board.get(r, c)) || GemUtil.isWeathered(board.get(pr, pc))) {
          // we can't move anymore
          continue;
        }
      }

      for (int dr = -1; dr <= 1; ++dr) {
        for (int dc = -1; dc <= 1; ++dc) {
          if (dr == 0 && dc == 0) {
            continue;
          }
          if (!canMoveDiagonal && dr != 0 && dc != 0) {
            continue;
          }

          int nr = r + dr;
          int nc = c + dc;
          if (!board.in_range(nr, nc)) {
            continue;
          }
          Board nBoard = board.dup();

          nBoard.set(r, c, board.get(nr, nc));
          nBoard.set(nr, nc, board.get(r, c));

          Step nStep = new Step(nBoard, s, nr, nc);

          String string = nStep.toString();

          Step cache = caches.get(string);
          if (cache == null || cache.moves > nStep.moves) {
            nCreated++;
            caches.put(string, nStep);
            queue.add(nStep);
          }
        }
      }
    }

    List<Position> steps = genSteps(best);
    System.out.println(steps.size());
    for (Position p : steps) {
      System.out.println(String.format("%d %d", p.r, p.c));
    }

    logger.log(level, String.format("nCreated = %d\n", nCreated));
    logger.log(level, String.format("combos: %d (%d)\n", best.combo, comboUpperBound));
    showFinalResult(best.board.dup());
    return best.combo;
  }

  private void showFinalResult(Board board) {
    board.print(logger, level);

    Damages damages = new Damages();
    board.computeComboAndRemove(damages);
    board.print(logger, level);

    logger.log(level, String.format("damage: %f\n", damages.toDouble()));
  }

  ArrayList<Position> genSteps(Step lastStep) {
    ArrayList<Position> result = new ArrayList<Position>();

    while (lastStep != null) {
      result.add(new Position(lastStep.r, lastStep.c));
      lastStep = lastStep.prev;
    }

    Collections.reverse(result);
    return result;
  }

  public static class Position {
    public final int r;
    public final int c;

    public Position(int r, int c) {
      this.r = r;
      this.c = c;
    }
  }

  public static class Step {
    public static int MAX_COMBO;
    public final Board board;
    public final int combo;
    public final Step prev;
    public final int r;
    public final int c;
    public final int moves;
    public final double score;
    public final double damage;

    Step(Board board, Step prev, int r, int c) {
      Damages damages = new Damages();
      this.board = board;
      this.combo = board.computeCombo(damages);
      this.prev = prev;
      this.r = r;
      this.c = c;
      this.moves = prev != null ? prev.moves + 1 : 1;
      this.damage = damages.toDouble();
      this.score = computeScore();
    }

    @Override
    public String toString() {
      return String.format("%s#%d#%d", board.toString(), r, c);
    }

    public double computeScore() {
      return Math.pow(combo, 2) * Math.pow(damage, 0.5) / Math.pow(moves, 5);
      //return Math.pow(combo, 2) * Math.pow(damage, 1) / Math.pow(moves, 3);
    }
  }
}
