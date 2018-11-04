FROM clojure:tools-deps

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Install deps
COPY deps.edn /usr/src/app/
RUN clj -Sforce < /dev/null

# Copy source files
COPY . /usr/src/app

# Create uberjar
RUN clj -A:depstar -m hf.depstar.uberjar server.jar

# Use non-root user to run the container
RUN useradd -ms /bin/bash myuser
USER myuser

# Default command to run when container starts
CMD java -Xmx400m -cp server.jar clojure.main -m example.server

# NOTE -Xmx400m ensures that memory doesn't exceed 512m free tier
# limit

# No need to expose any ports here since Heroku ignores EXPOSE anyways.
