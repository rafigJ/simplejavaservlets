FROM postgres:16.1-alpine3.19 as dumper

COPY init_db.sql ./docker-entrypoint-initdb.d/

RUN ["sed", "-i", "s/exec \"$@\"/echo \"skipping...\"/", "/usr/local/bin/docker-entrypoint.sh"]

ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV PGDATA=/data

RUN ["/usr/local/bin/docker-entrypoint.sh", "postgres"]


FROM postgres:16.1-alpine3.19

COPY --from=dumper /data $PGDATA

