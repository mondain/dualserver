# TCP/UDP Server

## Description

Simple TCP/UDP server that listens on port 8080 and echos back the received message. This is intended to be used as a simple test server for network programming to demonstrate the use of TCP and UDP sockets on the same port at the same time.

## Usage

Build and run the server:

```bash
mvn clean package
java -jar target/tcp-udp-server-1.0-SNAPSHOT.jar
```

That will start the server listening on port 8080 for both TCP and UDP connections; it will echo back any message received.

## Testing

TCP test with netcat

```bash
nc localhost 8080
```

UDP test with netcat

```bash
nc -u localhost 8080
```
