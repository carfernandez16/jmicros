FROM amazoncorretto

WORKDIR /usr/app1

COPY build/libs/jmicros-all-1.0-SNAPSHOT.jar /usr/app1/

ENTRYPOINT ["java", "-cp", "jmicros-all-1.0-SNAPSHOT.jar", "com.jmicros.rest.EmbeddedRESTServer"]