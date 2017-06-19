FROM ubuntu:16.04
RUN apt-get update -y && apt-get install -y openjdk-9-jre
COPY chat-server*.jar /app/chatserver.jar
EXPOSE 5000
CMD java -jar /app/chatserver.jar