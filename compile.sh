cd data
make
cd ..

javac -d Tos-Hack/bin/ -sourcepath Tos-Hack/src Tos-Hack/src/Main.java
javac -cp Parser/libsvm.jar -d Parser/bin -sourcepath Parser/src Parser/src/alon/parser/Main.java
javac -d Solver/bin/ -sourcepath Solver/src Solver/src/stimim/solver/Main.java 

java -cp Parser/bin/:Parser/libsvm.jar alon.parser.Main --training
