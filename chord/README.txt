Para compilar o programa:

make
OU
javac -d bin/ -cp "derby.jar:src" src/program/Peer.java 

Para correr o primeiro peer:

java -cp "derby.jar:bin" program.Peer <port>

Para correr os peers seguintes:

java -cp "derby.jar:bin" program.Peer <port> <ip_1peer> port_1peer>

