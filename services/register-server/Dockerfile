FROM ubuntu:16.04
RUN apt-get update -y && apt-get install -y openjdk-9-jre
COPY target/register-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar /app/registerserver.jar
EXPOSE 5002
CMD java -jar /app/registerserver.jar
