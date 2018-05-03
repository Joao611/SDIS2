#!/bin/bash

echo "Compiling...!"
javac -d bin/ -cp src src/program/Peer.java

echo "Creating peers!"
gnome-terminal -x java -cp bin program.Peer 9000
for (( c=1; c<3; c++ ))
do  
	gnome-terminal -x java -cp bin program.Peer 900$c localhost 9000
done