FROM clojure:tools-deps

COPY --from=node:20-slim /usr/local/bin /usr/local/bin
COPY --from=node:20-slim /usr/local/lib/node_modules /usr/local/lib/node_modules

COPY . /usr/src/app
WORKDIR /usr/src/app

RUN npm i

RUN npx shadow-cljs release app

RUN npx tailwindcss -i global.css -o resources/public/assets/css/output.css --minify

RUN clj -T:build uber

CMD [ "java", "-jar", "target/app-standalone.jar" ]
