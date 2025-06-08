FROM ubuntu:latest
LABEL authors="ohnando"

ENTRYPOINT ["top", "-b"]