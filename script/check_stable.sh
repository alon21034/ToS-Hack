adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png

java -cp ./Tos-Hack/bin Main screen.png > board1

adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png

java -cp ./Tos-Hack/bin Main screen.png > board2

diff board1 board2