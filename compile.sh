cd data
make
cd ..

javac -d Tos-Hack/bin/ -sourcepath Tos-Hack/src Tos-Hack/src/Main.java

javac -d Solver/bin/ -sourcepath Solver/src Solver/src/stimim/solver/Main.java 
