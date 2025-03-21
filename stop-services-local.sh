#!/bin/bash

echo "Stopping all microservices..."
pkill -f 'java -jar .*\.jar'

echo "Services stopped." 