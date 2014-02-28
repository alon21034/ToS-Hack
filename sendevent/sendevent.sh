eventnum=$1
size=$2
blocksize=16

echo "eventnum: " $eventnum
echo "size: " $size

i=0
while [[ $(($i * $blocksize)) -lt $size ]]
do
  dd if=/sdcard/events.out of=/dev/input/event$eventnum bs=$blocksize count=3 skip=$i \
    > /dev/null 2>&1
  sleep 0.03
  i=$(($i + 3))
  echo $i
done
