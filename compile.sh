make -C data
make -C sendevent

echo 'compile parser...'
javac -cp Parser/libsvm.jar -d Parser/bin -sourcepath Parser/src Parser/src/alon/parser/Main.java

echo 'compile solver...'
javac -d Solver/bin/ -sourcepath Solver/src Solver/src/stimim/solver/Main.java

