FROM ubuntu:16.04
RUN apt-get update -y && apt-get install -y openjdk-9-jre
COPY target/login-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar /app/loginserver.jar
EXPOSE 5001
CMD java -jar /app/loginserver.jar
