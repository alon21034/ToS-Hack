a=`adb get-state | grep device -o`

if [ "$a"=="device" ] ;then
	exit 0
else 
	echo "cannot find device"
	exit 1
fi