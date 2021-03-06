#include <iostream>
#include <vector>
#include <iomanip>
#include <set>
#include <utility>
#include <cstdlib>

using namespace std;

int verbose = 0;

struct Board {
#define NROW 5
#define NCOL 6
#define TO_REMOVE 0x80
#define VALUE 0x07

  typedef vector<char> Data;

  Data data;

  Board(const Board& other) : data(NROW * NCOL) {
    for (int i = 0; i < data.size(); ++ i) {
      data[i] = other.data[i];
    }
  }

  Board() : data(NROW * NCOL) {

  }

  bool in_range(int r, int c) const {
    return 0 <= r && r < NROW && 0 <= c && c < NCOL;
  }

  char get_const(int r, int c) const {
    return data[r * NCOL + c];
  }

  char operator() (int r, int c) const {
    return get_const(r, c);
  }

  char& get(int r, int c) {
    return data[r * NCOL + c];
  }

  char& operator() (int r, int c) {
    return get(r, c);
  }

  Board dup() const {
    Board retval;
    for (int i = 0; i < data.size(); ++ i) {
      retval.data[i] = data[i];
    }
    return retval;
  }

  int combo_upper_bound() const {
    int count[7] = {0, 0, 0, 0, 0, 0, 0};

    for (int r = 0; r < NROW; ++ r) {
      for (int c = 0; c < NCOL; ++ c) {
        if (get_const(r, c) != -1) {
          count[get_const(r, c)] ++;
        }
      }
    }

    int acc = 0;
    for (int i = 0; i < 7; ++ i) {
      acc += count[i] / 3;
    }
    return acc;
  }

  void print() const {
    cout << "-----------------" << endl;
    for (int r = 0; r < NROW; ++ r) {
      for (int c = 0; c < NCOL; ++ c) {
        if (get_const(r, c) == -1) {
          cout << ' ' << ' ';
        } else {
          cout << (int) get_const(r, c) << ' ';
        }
      }
      cout << endl;
    }
    cout << "-----------------" << endl;
  }

  int compute_combo() const {
    Board b = dup();

    int acc = 0;
    int count;
    while ((count = b.remove_connects()) > 0) {
      acc += count;
    }
    return acc;
  }

  int remove_connects() {
    int count = 0;
    for (int r = 0; r < NROW; ++ r) {
      for (int c = 0; c < NCOL; ++ c) {
        int self = get(r, c);
        if (self == -1) {
          // nothing here
          continue;
        }

        self = self & VALUE;
        // horizontal
        if (c + 2 < NCOL) {
          if ((self == (get(r, c + 1) & VALUE)) && self == (get(r, c + 2) & VALUE)) {
            get(r, c + 0) |= TO_REMOVE;
            get(r, c + 1) |= TO_REMOVE;
            get(r, c + 2) |= TO_REMOVE;
          }
        }
        // vertical
        if (r + 2 < NROW) {
          if ((self == (get(r + 1, c) & VALUE)) && self == (get(r + 2, c) & VALUE)) {
            get(r + 0, c) |= TO_REMOVE;
            get(r + 1, c) |= TO_REMOVE;
            get(r + 2, c) |= TO_REMOVE;
          }
        }
      }
    }

    // count and remove
    for (int r = 0; r < NROW; ++ r) {
      for (int c = 0; c < NCOL; ++ c) {
        int self = get(r, c);
        if (self == -1) continue;
        if (self & TO_REMOVE) {
          count ++;

          vector<pair<int, int> > que;
          que.push_back(make_pair(r, c));

          for (int i = 0; i < que.size(); ++ i) {
            int y = que[i].first;
            int x = que[i].second;
            get(y, x) = -1;

            if (in_range(y - 1, x) && get(y - 1, x) == self) {
              que.push_back(make_pair(y - 1, x));
            }
            if (in_range(y + 1, x) && get(y + 1, x) == self) {
              que.push_back(make_pair(y + 1, x));
            }
            if (in_range(y, x - 1) && get(y, x - 1) == self) {
              que.push_back(make_pair(y, x - 1));
            }
            if (in_range(y, x + 1) && get(y, x + 1) == self) {
              que.push_back(make_pair(y, x + 1));
            }
          }
        }
      }
    }

    // drop
    for (int c = 0; c < NCOL; ++ c) {
      int i = NROW - 1;
      for (int r = NROW - 1; r >= 0; -- r) {
        if (get(r, c) != -1) {
          get(i, c) = get(r, c);
          i --;
        }
      }
      for (; i >= 0; -- i) {
        get(i, c) = -1;
      }
    }

    return count;
  }

  string hash() const {
    string result = "";
    for (int i = 0; i < data.size(); ++ i) {
      result.push_back(data[i] + '0');
    }
    return result;
  }
};

struct Solver {
  //static constexpr int NROW = 5;
  //static constexpr int NCOL = 6;

  int MAX_STEP;
  int QUE_LIMIT;
  int PRUNE_RATIO_NUM;
  int PRUNE_RATIO_DEN;

  typedef vector<pair<int, int> > Result;

  Board board;

  Solver() {

  }

  Solver& read(int argc, char* argv[]) {
    MAX_STEP = 30;
    QUE_LIMIT = 1000000;
    PRUNE_RATIO_NUM = 2;
    PRUNE_RATIO_DEN = 3;

    for (int i = 1; i < argc; ++ i) {
      if (argv[i][0] == '-') {
        switch (argv[i][1]) {
          case 's': // max step
            MAX_STEP = atoi(argv[i] + 2);
            break;
          case 'q': // queue limit
            QUE_LIMIT = atoi(argv[i] + 2);
            break;
          case 'n': // PRUNE_RATIO_NUM
            PRUNE_RATIO_NUM = atoi(argv[i] + 2);
            break;
          case 'd': // PRUNE_RATIO_DEN
            PRUNE_RATIO_DEN = atoi(argv[i] + 2);
            break;
          case 'v':
            verbose = 1;
            break;
        }
      }
    }

    for (int r = 0; r < NROW; ++ r) {
      for (int c = 0; c < NCOL; ++ c) {
        int v;
        cin >> v;
        board(r, c) = v;
      }
    }

    if (verbose) {
      clog << "max possible combo: " << board.combo_upper_bound() << endl;
    }
  }

  int solve(Result& result) {
    return try_make_combo(result, MAX_STEP, QUE_LIMIT);
  }

  int try_make_combo(Result& result, int max_step, int que_limit) {
    vector<Step> que;

    que.clear();
    for (int c = NCOL - 1; c >= 0; -- c) {
      for (int r = NROW - 1; r >= 0; -- r) {
        que.push_back(Step(0, board.dup(), make_pair(r, c), -1));
      }
    }

    int combo_upper_bound = board.combo_upper_bound();
    int max_combo = 0;
    int max_combo_local = 0;
    int current_max_step = 0;
    set<pair<string, pair<int, int> > > visited;
    visited.clear();
    for (int i = 0; i < que.size(); ++ i) {
      if (current_max_step < que[i].steps) {
        max_combo = max_combo_local;
        current_max_step = que[i].steps;
        if (verbose) {
          clog << current_max_step << endl;
        }
      }

      int combo = que[i].board.compute_combo();
      if (combo > max_combo_local) {
        if (verbose) {
          clog << "current max combo = " << combo << endl;
        }
        max_combo_local = combo;

        result.clear();
        for (int v = i; v != -1; v = que[v].from) {
          result.push_back(que[v].cursor);
        }

        if (combo == combo_upper_bound) {
          clog << "reach combo upper bound" << endl;
          break;
        }
      }

      if (que[i].steps == max_step ||
          que.size() > que_limit ||
          (combo * PRUNE_RATIO_DEN < max_combo_local * PRUNE_RATIO_NUM &&
              max_combo_local > 3)) {
        continue;
      }

      int bad_dr = 0;
      int bad_dc = 0;

      if (que[i].from != -1) {
        bad_dr = que[i].cursor.first - que[que[i].from].cursor.first;
        bad_dc = que[i].cursor.second - que[que[i].from].cursor.second;
      }

      for (int dr = -1; dr <= 1; ++ dr) {
        for (int dc = -1; dc <= 1; ++ dc) {
          if (dr == 0 && dc == 0) continue;

          if (dr == bad_dr && dc == bad_dc) continue;

          int nr = que[i].cursor.first + dr;
          int nc = que[i].cursor.second + dc;

          if (!(que[i].board.in_range(nr, nc))) continue;

          Board b(que[i].board);

          swap(b(que[i].cursor.first, que[i].cursor.second), b(nr, nc));

          if (visited.find(make_pair(b.hash(), make_pair(nr, nc))) == visited.end()) {
            que.push_back(Step(que[i].steps + 1, b, make_pair(nr, nc), i));
            visited.insert(make_pair(b.hash(), make_pair(nr, nc)));
            // clog << "add " << b.hash() << ' ' << nr << ' ' << nc << endl;
          }
        }
      }
    }

    max_combo = max_combo_local;
    if (verbose) {
      clog << "queue size: " << que.size() << endl;
      clog << "visited node: " << visited.size() << endl;
    }
    return max_combo;
  }

  // each step of queue
  struct Step {
    int steps; // how many moves till now
    Board board; // current configuration
    pair<int, int> cursor; // where is the cursor
    int from; // from which step (prevent going back and retrieve steps)

    Step(int s, const Board& b, pair<int, int> c, int f) :
      steps(s), board(b), cursor(c), from(f) {
    }
  };
};

int main(int argc, char* argv[]) {
  Solver solver;
  solver.read(argc, argv);

  Solver::Result result;

  int max_combo = solver.solve(result);

  if (verbose) {
    clog << "max combo found: " << max_combo << endl;
  }

  for (int i = result.size() - 1; i >= 0; -- i) {
    cout << result[i].first << ' ' << result[i].second << endl;
  }
  return 0;
}

