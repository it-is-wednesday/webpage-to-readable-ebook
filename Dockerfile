FROM docker.io/library/clojure:temurin-19-lein-bullseye
COPY . /app
WORKDIR /app
RUN apt-get update && apt-get install -y calibre pandoc wkhtmltopdf
RUN lein deps
RUN mv "$(lein ring uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar
CMD ["java", "-jar", "app-standalone.jar"]
