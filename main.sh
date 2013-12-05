echo "Hello!!"

echo "Get touch screen handler"
adb shell cat /proc/bus/input/devices > handlerList

eventnum=`grep touchscreen -A 5 handlerList | grep 'event[0-9]\+' -o | grep '[0-9]\+' -o`
adb shell getevent -lp /dev/input/event$eventnum > tmp

x=`grep ABS_MT_POSITION_X tmp | grep 'max [0-9]\+' -o | grep '[0-9]\+' -o`
y=`grep ABS_MT_POSITION_Y tmp | grep 'max [0-9]\+' -o | grep '[0-9]\+' -o`

adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png

java -cp ./Tos-Hack/bin Main screen.png

./data/algorithm -v < board > step

echo "$x $y" | ./data/generateTrace $eventnum

adb push ./test.sh  /sdcard/test.sh
adb shell sh /sdcard/test.sh


