EVENTNUM=$1
SIZE=$2
BLOCKSIZE=16
COUNT=3
DELAY=0.05

echo "eventnum: " $EVENTNUM
echo "size: " $SIZE

i=0
dd if=/sdcard/events.out of=/dev/input/event$EVENTNUM bs=$BLOCKSIZE count=2 skip=$i \
  > /dev/null 2>&1
i=2

while [[ $(($i * $BLOCKSIZE)) -lt $SIZE ]]
do
  dd if=/sdcard/events.out of=/dev/input/event$EVENTNUM bs=$BLOCKSIZE count=$COUNT skip=$i \
    > /dev/null 2>&1
  sleep $DELAY
  i=$(($i + $COUNT))
  echo $i
done
