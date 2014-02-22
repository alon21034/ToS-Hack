#include <unistd.h>
#include <errno.h>
#include <stdio.h>
#include <fcntl.h>
#include <stdint.h>

struct __attribute__((aligned(1),packed)) input_event {
  uint32_t time_a;
  uint32_t time_b;

  uint16_t type;
  uint16_t code;
  int32_t value;
};

int main(int argc, char* argv[]) {
  int dev;
  int script;
  struct input_event event;

  /* sendevent dev script */
  if (argc < 3) {
    fprintf(stderr, "USAGE: sendevent dev script\n");
    return 1;
  }

  dev = open(argv[1], O_RDWR);
  if (dev < 0) {
    fprintf(stderr, "could not open %s, %s", argv[1], strerror(errno));
    return 1;
  }

  script = open(argv[2], O_RDONLY);
  if (script < 0) {
    fprintf(stderr, "could not open %s, %s", argv[2], strerror(errno));
    return 1;
  }

  while (sizeof(struct input_event) == read(script, &event, sizeof(event))) {
    write(dev, &event, sizeof(event));
    fsync(dev);
    usleep(10000);
  }

  close(dev);
  close(script);

  return 0;
}

