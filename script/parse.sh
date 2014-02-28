adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png

java -cp Parser/bin/:Parser/libsvm.jar alon.parser.Main screen.png

cat output