from postgres:latest

ENV POSTGRES_USER quarkus
ENV POSTGRES_PASSWORD quarkus
ENV POSTGRES_DB user-api

COPY ./queries/. /docker-entrypoint-initdb.d/