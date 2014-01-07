echo "Hello!!"

echo "Get touch screen handler"
eventnum=`sh script/get_event_number.sh`

adb shell getevent -lp /dev/input/event$eventnum > tmp
x=`grep ABS_MT_POSITION_X tmp | grep 'max [0-9]\+' -o | grep '[0-9]\+' -o`
y=`grep ABS_MT_POSITION_Y tmp | grep 'max [0-9]\+' -o | grep '[0-9]\+' -o`

adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png

for (( i = 0; i < $1; i++ )); do
	echo "start"
	
	adb shell screencap -p /sdcard/screen.png
	adb pull /sdcard/screen.png

	java -cp ./Tos-Hack/bin Main screen.png

	./data/algorithm -v < board > step

	echo "$x $y" | ./data/generateTrace $eventnum

	adb push ./test.sh  /sdcard/test.sh
	adb shell sh /sdcard/test.sh

	echo "$i done"

	echo "wait..."

	sleep 20
done


