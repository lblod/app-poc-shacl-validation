FROM maven:3-openjdk-11
COPY . .
RUN mvn clean install -DskipTests
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dlog4j2.formatMsgNoLookups=true -jar /target/shacl-validation.jar"]
