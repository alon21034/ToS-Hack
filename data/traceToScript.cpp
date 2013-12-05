#include <iostream>
#include <fstream>
#include <string>

using namespace std;

int main(int argc, char** argv) {
	ifstream inFile(argv[1]);

	ofstream outFile(argv[2]);

	string str1, str2, str3;
	while(inFile >> str1 >> str2 >> str3) {
		outFile << "sendevent /dev/input/event0 " << str1 << " $((0x" << str2 << ")) " << "$((0x" << str3 << "))" << endl;
	}
}