#!/bin/sh

cluster_id_file_path="/tmp/cluster-id-dir/clusterId"
interval=3  # wait interval in seconds

#[-e FILE]	True if FILE exists.
#[-s FILE]	True if FILE exists and has a size greater than zero.

while [ ! -e "$cluster_id_file_path" ] || [ ! -s "$cluster_id_file_path" ]; do
  echo "Waiting for cluster id to be created!"
  sleep $interval
done

echo "Cluster id: $(cat $cluster_id_file_path)"
# KRaft required step: Format the storage directory with a new cluster ID
echo -e "\nkafka-storage format --ignore-formatted -t $(cat $cluster_id_file_path) -c /etc/kafka/kafka.properties" >> /etc/confluent/docker/ensure