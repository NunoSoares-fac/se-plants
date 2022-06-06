#!/bin/bash
# run all servers

node test_server.js &
node server.js &
node index.js &
