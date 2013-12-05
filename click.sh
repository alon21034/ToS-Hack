echo "sendevent /dev/input/event$1 0003 57 60937" > click_tmp
echo "sendevent /dev/input/event$1 0003 48 84" >> click_tmp

echo "sendevent /dev/input/event$1 0003 53 $2" >> click_tmp
echo "sendevent /dev/input/event$1 0003 54 $3" >> click_tmp

echo "sendevent /dev/input/event$1 0000 0 0" >> click_tmp
echo "sendevent /dev/input/event$1 0003 57 -1" >> click_tmp
echo "sendevent /dev/input/event$1 0000 0 0" >> click_tmp

adb push click_tmp /sdcard/click.sh
adb shell sh click.sh

rm click_tmp