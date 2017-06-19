FROM ubuntu:16.04
RUN apt-get update -y && apt-get install -y openjdk-9-jre
COPY target/chat-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar /app/chatserver.jar
EXPOSE 5000
CMD java -jar /app/chatserver.jar