FROM jelastic/maven:3.9.5-openjdk-21 AS build
WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app/target/Wellness-0.0.1-SNAPSHOT.jar"]



## not working still
#FROM jelastic/maven:3.9.5-openjdk-21 AS build
#WORKDIR /app
#COPY pom.xml .
#RUN mvn dependency:go-offline -B
#COPY src ./src
#RUN mvn package -DskipTests -Dmaven.test.skip=true
#
#FROM openjdk:21-jre-slim
#WORKDIR /app
#COPY --from=build /app/target/Wellness-0.0.1-SNAPSHOT.jar /app/Wellness.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","Wellness.jar"]

