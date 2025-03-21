#!/bin/bash

CLUSTER_ID=/tmp/cluster-id-dir/clusterId

if [ ! -f "$CLUSTER_ID" ]; then
  echo "Generating Kafka cluster ID"
  kafka-storage random-uuid > $CLUSTER_ID
  echo "Generated Kafka cluster ID: $(cat $CLUSTER_ID)"
else
  echo "Using existing Kafka cluster ID: $(cat $CLUSTER_ID)"
fi