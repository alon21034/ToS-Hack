cd data
make
cd ..

echo 'compile parser...'
javac -cp Parser/libsvm.jar -d Parser/bin -sourcepath Parser/src Parser/src/alon/parser/Main.java

echo 'compile solver...'
javac -d Solver/bin/ -sourcepath Solver/src Solver/src/stimim/solver/Main.java 

echo 'compile gui...'
javac -d ToS-Hack-GUI/bin -sourcepath ToS-Hack-GUI/src ToS-Hack-GUI/src/alon/gui/Main.java

