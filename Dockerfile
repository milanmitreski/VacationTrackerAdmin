FROM openjdk:21-oracle
COPY build/libs/vacationtracker-0.0.1-SNAPSHOT.jar vacationtracker.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "vacationtracker.jar"]