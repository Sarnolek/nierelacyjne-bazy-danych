FROM mongo:6.0

COPY mongod.conf /etc/mongod.conf
COPY keyFile /etc/mongo/keyFile

RUN chown 999:999 /etc/mongo/keyFile && \
    chmod 400 /etc/mongo/keyFile && \
    chown 999:999 /etc/mongod.conf