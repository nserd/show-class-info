FROM alpine:3.13.6
RUN apk --no-cache add openjdk11-jre-headless --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community
COPY ./target/ShowClassInfo.jar /etc/classinfo/ShowClassInfo.jar
WORKDIR /classinfo
ENTRYPOINT ["java", "-jar", "ShowClassInfo.jar"]
