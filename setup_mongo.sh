#!/bin/bash

docker network create mongo-cluster

docker network create mongo-cluster
docker run --name mongo1 -d --net mongo-cluster -p 9042:9042 mongo:4.4.4 mongod --replSet docker-rs --port 9042
docker run --name mongo2 -d --net mongo-cluster -p 9142:9142 mongo:4.4.4 mongod --replSet docker-rs --port 9142
docker run --name mongo3 -d --net mongo-cluster -p 9242:9242 mongo:4.4.4 mongod --replSet docker-rs --port 9242

sleep 5

docker container exec -i mongo1 mongo --port 9042 << EOF
config = {"_id" : "docker-rs", "members" : [{"_id" : 0,"host" : "mongo1:9042"},{"_id" : 1,"host" : "mongo2:9142"},{"_id" : 2,"host" : "mongo3:9242"}]}
rs.initiate(config)
rs.status()
EOF

sleep 5

docker container exec -i mongo1 mongo --port 9042 << EOF
use admin
db.createUser({user: "admin",pwd: "admin",roles: [ { role: "root", db: "admin" }, "root" ]})
EOF 

