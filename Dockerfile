FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY Main.java .
RUN javac Main.java
COPY site ./site
ENV HOST=0.0.0.0 PORT=8000
EXPOSE 8000
USER 65532:65532
ENTRYPOINT ["java", "Main"]
