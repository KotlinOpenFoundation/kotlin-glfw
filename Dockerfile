FROM eclipse-temurin:22-noble

RUN apt-get -y update && apt-get install -y --no-install-recommends \
    build-essential \
    gcc-multilib \
    cmake \
    pkg-config \
    libwayland-dev \
    libxkbcommon-dev \
    libxext-dev \
    libxi-dev \
    libxcursor-dev \
    libxinerama-dev \
    libxrandr-dev \
    xorg-dev \
    xvfb \
    git \
    && rm -rf /var/lib/apt/lists/*

VOLUME /workspace
WORKDIR /workspace

CMD [ "/workspace/gradlew", "build" ]
