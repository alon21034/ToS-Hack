#include <iostream>
#include <fstream>
#include <string>

using namespace std;

int main() {

	ifstream inFile("../step");
	ofstream outFile("test.sh");

	// start
	outFile << "sendevent /dev/input/event0 0003 $((0x0039)) $((0x00000154))" << endl;
	outFile << "sendevent /dev/input/event0 0003 $((0x0030)) $((0x00000008))" << endl;


	int x1, y1;
	while(inFile >> x1 >> y1) {
		outFile << "sendevent /dev/input/event0 0003 $((0x0035)) $((0x" << hex << 110 + 213*x1 << "))" << endl;
		outFile << "sendevent /dev/input/event0 0003 $((0x0036)) $((0x" << hex << 1010 + 213*y1<< "))" << endl;
		outFile << "sendevent /dev/input/event0 0000 $((0x0000)) $((0x00000000))" << endl;

		// outFile << "sendevent /dev/input/event0 0003 $((0x0035)) $((0x" << hex << 323 << "))" << endl;
		// outFile << "sendevent /dev/input/event0 0003 $((0x0036)) $((0x" << hex << 1223 << "))" << endl;
		// outFile << "sendevent /dev/input/event0 0000 $((0x0000)) $((0x00000000))" << endl;
	}



	// end
	outFile << "sendevent /dev/input/event0 0003 $((0x0039)) $((0xffffffff))" << endl;
	outFile << "sendevent /dev/input/event0 0000 $((0x0000)) $((0x00000000))" << endl;

}