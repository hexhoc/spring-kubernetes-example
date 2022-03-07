FROM docker.elastic.co/beats/filebeat:7.7.0
# Copy our custom configuration file
COPY config/filebeat.yml /usr/share/filebeat/filebeat.yml
USER root
RUN chmod go-w /usr/share/filebeat/filebeat.yml
