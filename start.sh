echo "Start ToS"

echo "Where do you want to go?"
echo "a/b/c/d/e"

read user_input

adb shell getevent -lp /dev/input/event$eventnum > tmp
WIDTH=`grep ABS_MT_POSITION_X tmp | grep 'max [0-9]\+' -o | grep '[0-9]\+' -o`
HEIGHT=`grep ABS_MT_POSITION_Y tmp | grep 'max [0-9]\+' -o | grep '[0-9]\+' -o`

echo "$((0.138))"

case "$user_input" in
  a) 
    sh click.sh $((0.138 * $WIDTH)) $((0.37 * $HEIGHT))
    ;;
esac
