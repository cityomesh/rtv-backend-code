FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY target/rtvnewsnetwork.jar rtvnewsnetwork.jar

EXPOSE 4000
ENTRYPOINT ["java","-jar" ,"rtvnewsnetwork.jar"]
