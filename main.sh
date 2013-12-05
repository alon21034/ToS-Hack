echo "Hello!!"

adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png

java -jar Main.jar screen.png

./algorithm
./data/generateTrace
./data/traceToScript

adb push ./data/test.sh  /sdcard/test.sh
adb shell sh /sdcard/test.sh

