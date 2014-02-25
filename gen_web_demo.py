#!/usr/bin/env python
# -*- coding: UTF-8 -*-

def read_pair(line):
  return [int(x) for x in line.split()]

# 5 6 7
# 3 @ 4
# 0 1 2
def dir_to_int(dx, dy):
  if dy < 0:
    return 6 + dx
  elif dy == 0:
    if dx < 0:
      return 3
    else:
      return 4
  else:
    return 1 + dx

def get_solver_result(board):
  import subprocess
  lines = ""
  output = ""
  for line in board:
    lines += line + "\n"
  solver = subprocess.Popen(["java", "-jar", "Solver.jar"], stdin=subprocess.PIPE, stdout=subprocess.PIPE)
  output += solver.communicate(lines)[0]
  while solver.poll():
    output += solver.communicate()[0]
  return output

def to_web_layout(board):
  values = [int(x) for x in "\n".join(board).split()]
  values = map(lambda v: str(v & 0xF), values)
  return "".join(values)

board = [raw_input() for i in xrange(5)]

result = get_solver_result(board);

lines = result.split('\n');
n = int(lines[0])
start = read_pair(lines[1])
route = "%d%d," % (start[1], (start[0] + 5))
prev = start

for i in xrange(1, n):
  cur = read_pair(lines[1 + i])
  dy = cur[0] - prev[0]
  dx = cur[1] - prev[1]
  route += str(dir_to_int(dx, dy))
  prev = cur

print "http://serizawa.web5.jp/puzzdra_theory_maker/index.html?layout=%s&route=%s" % \
    (to_web_layout(board), route)
