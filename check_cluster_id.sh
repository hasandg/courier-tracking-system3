#!/bin/bash

echo "Checking for Kafka cluster ID"

# Wait for the cluster ID to be generated
while [ ! -f /tmp/cluster-id-dir/clusterId ]; do
  echo "Waiting for cluster ID to be generated..."
  sleep 1
done

echo "Using Kafka cluster ID: $(cat /tmp/cluster-id-dir/clusterId)"