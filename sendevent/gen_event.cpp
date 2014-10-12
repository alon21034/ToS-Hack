#include <iostream>
#include <fstream>
#include <stdint.h>
#include <cstring>

using namespace std;

struct __attribute__((aligned(1),packed)) input_event {
  uint32_t time_a;
  uint32_t time_b;

  uint16_t type;
  uint16_t code;
  int32_t value;
};

inline void sendevent(ofstream& fout, uint16_t type, uint16_t code, int32_t value) {
  input_event event;
  memset(&event, 0, sizeof(event));
  event.type = type;
  event.code = code;
  event.value = value;

  fout.write((char*) &event, sizeof(event));
}

int main(int argc, char* argv[]) {
  ifstream fin("step");
  ofstream fout("events.out", ios::out | ios::binary);

  // start
  sendevent(fout, 3, 0x39, 0x00000154);
  sendevent(fout, 3, 0x30, 0x00000008);
  sendevent(fout, 3, 0x30, 0x00000008);

  int W, H;
  cin >> W >> H;

  for (int x, y; fin >> y >> x; ) {
    sendevent(fout, 3, 0x35, W / 12 + W * x / 6);
    sendevent(fout, 3, 0x36, H - H / 16 - W / 12 - (4 - y) * W / 6);
    sendevent(fout, 0, 0, 0);
  }

  // end
  sendevent(fout, 3, 0x39, 0xffffffff);
  sendevent(fout, 0, 0x00, 0x00000000);
  sendevent(fout, 0, 0x00, 0x00000000);
  return 0;
}

