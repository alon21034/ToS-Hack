echo "Hello!!"

adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png

java -jar Main.jar screen.png

./data/algorithm  < board > step
./data/generateTrace

adb push ./test.sh  /sdcard/test.sh
adb shell sh /sdcard/test.sh


