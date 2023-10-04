FROM openjdk:17.0.8-jdk
VOLUME /tmp
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]