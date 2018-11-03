FROM clojure:tools-deps
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY deps.edn /usr/src/app/
RUN clj -Sforce < /dev/null
COPY . /usr/src/app
CMD clj -m example.server
