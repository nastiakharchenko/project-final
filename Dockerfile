FROM openjdk:17
WORKDIR /workspace/app

COPY target/jira-1.0.jar app-jira.jar
COPY resources ./resources

EXPOSE 8080

CMD ["java", "-jar", "app-jira.jar"]