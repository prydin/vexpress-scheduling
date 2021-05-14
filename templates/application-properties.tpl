spring.rabbitmq.addresses=$RABBITMQ_IP
spring.rabbitmq.username=$RABBITMQ_USER
spring.rabbitmq.password=$RABBITMQ_PASSWORD
spring.rabbitmq.virtual-host=vexpress

# Tanzu Observability
management.metrics.export.wavefront.api-token=$TO_TOKEN
management.metrics.export.wavefront.uri=$TO_URL
wavefront.application.name=vexpress
wavefront.application.service=zipcode