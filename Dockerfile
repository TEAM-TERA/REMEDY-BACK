FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    ffmpeg \
    curl \
    && rm -rf /var/lib/apt/lists/*

# yt-dlp 설치 (Debian PEP 668 회피)
RUN pip3 install --no-cache-dir yt-dlp --break-system-packages

WORKDIR /app

COPY build/libs/Remedy-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]