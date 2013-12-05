#include <iostream>
#include <vector>
#include <iomanip>
#include <set>

#define NROW 5
#define NCOL 6
#define TO_REMOVE 0x80
#define VALUE 0x07


using namespace std;

struct Board {
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
    for (int i = 0; i < 5; ++ i) {
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
        // horizontal
        if (c + 2 < NCOL) {
          if ((self == (get(r, c + 1) & VALUE)) && self == (get(r, c + 2) & VALUE)) {
            if ((get(r, c + 0) & TO_REMOVE) == 0 &&
                (get(r, c + 1) & TO_REMOVE) == 0 &&
                (get(r, c + 2) & TO_REMOVE) == 0 ) {
              count ++;
            }
            get(r, c + 0) |= TO_REMOVE;
            get(r, c + 1) |= TO_REMOVE;
            get(r, c + 2) |= TO_REMOVE;
          }
        }
        // vertical
        if (r + 2 < NROW) {
          if ((self == (get(r + 1, c) & VALUE)) && self == (get(r + 2, c) & VALUE)) {
            if ((get(r + 0, c) & TO_REMOVE) == 0 &&
                (get(r + 1, c) & TO_REMOVE) == 0 &&
                (get(r + 2, c) & TO_REMOVE) == 0 ) {
              count ++;
            }
            get(r + 0, c) |= TO_REMOVE;
            get(r + 1, c) |= TO_REMOVE;
            get(r + 2, c) |= TO_REMOVE;
          }
        }
      }
    }

    // remove
    for (int r = 0; r < NROW; ++ r) {
      for (int c = 0; c < NCOL; ++ c) {
        int self = get(r, c);
        if (self == -1) continue;
        if (self & TO_REMOVE) {
          get(r, c) = -1;
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

  int MAX_STEP;
  int QUE_LIMIT;
  int PRUNE_RATIO;

  typedef vector<pair<int, int> > Result;

  Board board;

  Solver() {

  }

  Solver& read() {
    cin >> MAX_STEP >> QUE_LIMIT >> PRUNE_RATIO;

    for (int r = 0; r < NROW; ++ r) {
      for (int c = 0; c < NCOL; ++ c) {
        int v;
        cin >> v;
        board(r, c) = v;
      }
    }

    // cout << board.compute_combo() << endl;
    //
    cout << "max possible combo: " << board.combo_upper_bound() << endl;
  }

  int solve(Result& result) {
    return try_make_combo(result, MAX_STEP, QUE_LIMIT);
  }

  int try_make_combo(Result& result, int max_step, int que_limit) {
    vector<Step> que;
    que.clear();

    set<pair<string, pair<int, int> > > visited;

    for (int r = 0; r < NROW; ++ r) {
      for (int c = 0; c < NCOL; ++ c) {
        que.push_back({0, board.dup(), {r, c}, -1});
      }
    }

    int combo_upper_bound = board.combo_upper_bound();
    int max_combo = 0;
    int current_max_step = 0;
    for (int i = 0; i < que.size(); ++ i) {
      if (current_max_step < que[i].steps) {
        current_max_step = que[i].steps;
        cout << current_max_step << endl;
      }

      // cout << i << ' ' << max_combo << ' ' << combo_upper_bound << endl;
      // Step& s = que[i];

      int combo = que[i].board.compute_combo();
      if (combo > max_combo) {
        cout << "max_combo = " << combo << endl;
        max_combo = combo;

        result.clear();
        for (int v = i; v != -1; v = que[v].from) {
          result.push_back(que[v].cursor);
        }

        for (int i = result.size() - 1; i >= 0; -- i) {
          cout << result[i].first << ' ' << result[i].second << endl;
        }

        if (combo == combo_upper_bound) {
          break;
        }
      }

      if (que[i].steps == max_step || que.size() > que_limit || combo * PRUNE_RATIO < max_combo) {
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

          if (visited.find({b.hash(), {nr, nc}}) == visited.end()) {
            que.push_back({que[i].steps + 1, b, {nr, nc}, i});
            visited.insert({b.hash(), {nr, nc}});
          }
        }
      }
    }

    cout << "queue size: " << que.size() << endl;
    cout << "visited node: " << visited.size() << endl;
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
  solver.read();

  Solver::Result result;

  int max_combo = solver.solve(result);

  //cout << "max combo found: " << max_combo << endl;

  for (int i = result.size() - 1; i >= 0; -- i) {
    cout << result[i].first << ' ' << result[i].second << endl;
  }
  return 0;
}

