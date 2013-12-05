adb shell cat /proc/bus/input/devices > handlerList
eventnum=`grep touchscreen -A 5 handlerList | grep 'event[0-9]\+' -o | grep '[0-9]\+' -o`

echo $eventnum