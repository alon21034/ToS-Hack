#include <iostream>
#include <fstream>
#include <string>

using namespace std;

int main(int argc, char** argv) {

	ifstream inFile("step");
	ofstream outFile("test.sh");

	// start
	outFile << "sendevent /dev/input/event" << argv[1] << " 0003 $((0x0039)) $((0x00000154))" << endl;
	outFile << "sendevent /dev/input/event" << argv[1] << " 0003 $((0x0030)) $((0x00000008))" << endl;

	int W, H;
	cin >> W >> H;

	cout << W << " " << H << endl;

	int x1, y1;
	while(inFile >> y1 >> x1) {
		cout << y1 << x1 << endl;
		outFile << "sendevent /dev/input/event" << argv[1] << " 0003 $((0x0035)) $((0x" << hex << W/12 + W*x1/6 << "))" << endl;
		outFile << "sendevent /dev/input/event" << argv[1] << " 0003 $((0x0036)) $((0x" << hex << H - H/16 -W/12 - (4-y1) * W/6 << "))" << endl;
		outFile << "sendevent /dev/input/event" << argv[1] << " 0000 $((0x0000)) $((0x00000000))" << endl;

		// outFile << "sendevent /dev/input/event0 0003 $((0x0035)) $((0x" << hex << 323 << "))" << endl;
		// outFile << "sendevent /dev/input/event0 0003 $((0x0036)) $((0x" << hex << 1223 << "))" << endl;
		// outFile << "sendevent /dev/input/event0 0000 $((0x0000)) $((0x00000000))" << endl;
	}



	// end
	outFile << "sendevent /dev/input/event" << argv[1] << " 0003 $((0x0039)) $((0xffffffff))" << endl;
	outFile << "sendevent /dev/input/event" << argv[1] << " 0000 $((0x0000)) $((0x00000000))" << endl;

}