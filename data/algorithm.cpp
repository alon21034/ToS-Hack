#include <iostream>
#include <fstream>
#include <string>

using namespace std;

class Board {
public:
	Board(ifstream* inFile, ofstream* outFile) {
		board = new int*[5];
		for (int i = 0 ; i < 5 ; i++) {
			board[i] = new int[6];
			for (int j = 0 ; j < 6 ; j++) {
				*inFile >> board[i][j];
			}
		}

		point = new int[2];

		out = outFile;
		*out << "0 0" << endl;
	}

	void setStartPoint() {
		point[0] = 0;
		point[1] = 0;
	}

	void print() {
		for (int i = 0 ; i < 5 ; i++) {
			for (int j = 0 ; j < 6 ; j++ ){
				cout << board[i][j] << " ";
			}
			cout << endl;
		}
		cout << "total: " << cnt << endl;
	}

	void move(int d) {
		move(point[0], point[1], d);
	}	

	void sort(int n) {
		for (int j = 0 ; j < 6 ; j++) {
			for (int i = 0 ; i < 5 ; i++) {
				if(board[n][i] > board[n][i+1]) {
					exange(i,i+1, n);
				}
			}
		}
	}



private:
	int** board;

	ofstream* out;
	int* point;
	int cnt;

	void exange(int x1, int x2, int n) {
		// int tmp = board[n][x1];
		// board[n][x1] = board[n][x2];
		// board[n][x2] = tmp;

		while(x1!=point[0]) {
			move(x1>point[0]?3:2);
		}


		//cout << "sw:" << x1 << n <<"&" << x2 << n << endl;

		move(1);
		move(3);
		move(4);
		//print();
	}

	void move(int x, int y, int d) {
		switch(d) {
			case 0: move(x, y, x, y-1); break;
			case 1: move(x, y, x, y+1); break;
			case 2: move(x, y, x-1, y); break;
			case 3: move(x, y, x+1, y); break;
			case 4: move(x, y, x-1, y-1); break;
		}
	}

	void move(int fx, int fy, int tx, int ty) {
		int tmp = board[fy][fx];
		board[fy][fx] = board[ty][tx];
		board[ty][tx] = tmp;

		point[0] = tx;
		point[1] = ty;
		cout << fx << fy << "->" << tx << ty << endl;
		*out << tx << " " << ty << " " << endl;
		cnt++;
		//print();
	}
};

int main() {

	ifstream inFile("board");
	ofstream outFile("step");

	Board board(&inFile, &outFile);

	board.print();
	
	board.move(1);
	board.move(1);
	board.move(1);

	board.sort(4);
	board.move(0);
	board.sort(3);
	board.move(0);
	board.sort(2);
	board.move(0);
	board.sort(1);
	board.print();


}