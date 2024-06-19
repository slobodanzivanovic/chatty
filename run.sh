#!/bin/bash

start_server() {
  javac -cp "./bin:$(mvn dependency:build-classpath | grep -v '^\[')" -d ./bin src/main/java/com/slobodan/server/*.java
  java -cp "./bin:$(mvn dependency:build-classpath | grep -v '^\[')" com.slobodan.server.ChattyServer
}

start_client() {
  javac -cp "./bin:$(mvn dependency:build-classpath | grep -v '^\[')" -d ./bin src/main/java/com/slobodan/client/*.java
  java -cp "./bin:$(mvn dependency:build-classpath | grep -v '^\[')" com.slobodan.client.ChattyClient
}

if [ "$1" == "server" ]; then
  start_server
elif [ "$1" == "client" ]; then
  start_client
else
  echo "Usage: $0 (server|client)"
  exit 1
fi