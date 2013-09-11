#!/bin/bash

set -e

(
    flock -n -e 200
    /usr/bin/java \
      -jar /root/parag/AapMediaMonitoring/rss/target/rss-crawler-1.0-SNAPSHOT.jar \
      -f /root/parag/AapMediaMonitoring/rss/feedsfile.txt \
      -o /root/crawl-raw \
      >/var/log/rss-crawl.$(date +%Y-%m-%d-%H-%m-%s).log 2>&1
) 200>/var/run/rss-crawl.lock


