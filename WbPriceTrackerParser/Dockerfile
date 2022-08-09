FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /parser
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install -DskipTests=true

FROM eclipse-temurin:17-jre-jammy
WORKDIR /parser
EXPOSE 8085
COPY --from=builder /parser/target/*.jar /parser/*.jar
ENTRYPOINT ["java", "-jar", "/parser/*.jar" ]