#!/bin/bash

docker container rm -f $(docker container ps -a --filter name=mongo)

docker network rm $(docker network ls --filter name=mongo-cluster)

docker volume rm $(docker volume ls -q)
