all : gen_event install

.PHONY: install

install:
	adb push ./sendevent.sh /sdcard/sendevent.sh

gen_event: gen_event.cpp
	g++ -o $@ $^
