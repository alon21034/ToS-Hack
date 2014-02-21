adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png

java -cp ./Tos-Hack/bin Main screen.png > total_diff

diff=`grep 'diff = [0-9]\+' total_diff | grep '[0-9]\+' -o`

#echo "diff = $diff"

if (( "$diff" < 400 )); then
  echo "1"
else
  echo "0"
fi