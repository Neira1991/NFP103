FROM --platform=linux/x86_64 openjdk:22-bullseye

RUN apt-get update \
 && apt-get install -y musl-dev \
 && ln -s /usr/lib/x86_64-linux-musl/libc.so /lib/libc.musl-x86_64.so.1

EXPOSE 8080
COPY ./entrypoint.sh /entrypoint.sh
COPY ./build/libs/app.jar /build/libs/app.jar

# This form is important to propagate SIGTERM to application
ENTRYPOINT ["/entrypoint.sh"]
