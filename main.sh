echo "Hello!!"

echo "Get touch screen handler"
eventnum=`sh script/get_event_number.sh`

adb shell getevent -lp /dev/input/event$eventnum > tmp
x=`grep ABS_MT_POSITION_X tmp | grep 'max [0-9]\+' -o | grep '[0-9]\+' -o`
y=`grep ABS_MT_POSITION_Y tmp | grep 'max [0-9]\+' -o | grep '[0-9]\+' -o`

for (( i = 0; i < $1; i++ )); do
  echo "start"

  adb shell screencap -p /sdcard/screen.png
  adb pull /sdcard/screen.png

  java -cp Parser/bin/:Parser/libsvm.jar alon.parser.Main screen.png

  cat output

  java -Xmx2g -cp ./Solver/bin stimim.solver.Main < output > step

  echo "$x $y" | ./sendevent/gen_event
  adb push ./events.out /sdcard/events.out
  size=`wc -c ./events.out | grep '[0-9]\+' -o`
  adb shell "sh /sdcard/sendevent.sh $eventnum $size"

  echo "$i done"

  rm output* features*

  for (( j = 0 ; j < 17 ; j++ )); do
    sleep 1
    echo 'wait'
  done
  #read -p "Please input yes/YES to stop this program: " yn
done

