#!/usr/bin/env bash
mkdir -p /tmp/elk-data
mkdir -p /tmp/logstash
cp -u ./logstash-input.conf /tmp/logstash/03-logstash-input.conf
cp -u ./logstash-output.conf /tmp/logstash/30-output.conf
docker run -d --name sprio-elk -v "/tmp/logstash:/etc/logstash/conf.d:ro" -v "/tmp/elk-data:/var/lib/elasticsearch" -p 5601:5601 -p 10042:10042/udp sebp/elk:es232_l232_k450
