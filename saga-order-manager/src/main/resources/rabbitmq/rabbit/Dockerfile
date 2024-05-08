FROM rabbitmq:3.8-management
# Copy script for setting up RabbitMQ with predefined queue, exchange, and bindings
COPY setup-rabbitmq.sh /usr/local/bin/
# Make the script executable
RUN chmod +x /usr/local/bin/setup-rabbitmq.sh
# Run the setup script when the container starts
#CMD ["setup-rabbitmq.sh"]