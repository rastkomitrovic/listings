# Listings service

Listings backend service with Elasticsearch and Kafka integration.

# Database prerequisites

Within this service you can find the docker-compose.yaml file which can be used
to start the local Elasticsearch, Kibana and Kafka services. Elasticsearch and Kafka
are required for the service to run while Kibana is used as GUI to look at the data
which will be stored in the "listings" index that this service will create on startup
if it doesn't exist. To access Kibana open your browser and go to http://localhost:5601.

The docker-compose.yaml file is run using the "docker-compose up" command within the
CLI. If you want to stop the service we should run the "docker-compose down" command.
This command will stop the service but will keep all the data within the elasticsearch.
If you want this data deleted also you should run the "docker-compose down -v" command.

# Postman collection

Within this service you will find the Listings.postman_collection.json file which can
be imported into Postman and you will see 4 request that you can make:

1. Get all paging - Gets all the listings from the Elasticsearch database with pagination.
2. Search all paging - Searches all the listings from the Elasticsearch database by the given field values.
3. Produce listing event - Produces the listing event to the "listings" topic to create, update or delete a listing.
4. Generate random listings - Generates 40 random listings for easy convenience for your testing purposes.

# Swagger

To access the Swagger documentation of the available API request you can make
open your browser and go to http://localhost:9090/swagger-ui/index.html. It can also
be used to test the API if you dont want to use the postman.

# General info

For the demonstration purposes both the producer and consumer for the listings topic are located
in this service. In the real world they would be located within the different services.
The listings topic is automatically created upon service startup if it doesn't exist.
The same goes for the listings index in the Elasticsearch database.

The service is covered with the JUnit tests for now.

The purpose of this service is to produce vehicle listings as events to the
listings topic. In the listing event we have a mode which can be CREATE, UPDATE or DELETE
which indicates whether to create, update or delete a listing. These listings can then
be queried using the provided API endpoints.

# Runing the service

For the development environment I used IntelliJ IDEA. To build and run the project locally you need
to have java 21 and maven installed. To build the project you can run the "mvn clean install" command.
To run it you can use the "mvn spring-boot:run" command.

# Implementation notes

The project follows the standard structure of REST Controller -> Service -> Repository.
The controller, service and repository are implemented on the domain level. This means that if
we want to add a new domain object in the future, for example users, we would have separate controller,
service and repository for that domain object. Additionally, we can create a mapper service for the domain object
as I did to help in conversions from one type of object(DTO, DAO or Event) to other.

The caching abstraction here that is used is from the spring-boot-starter-cache dependency.
The way it currently works is that above any method that we have the @Cacheable annotation
Spring will remember the result of the method and tie it to the specific key we define inside
this annotation. The key is usually defined based on the method parameters. The current implementation
caches the results in memory which is of course bad for production environment. The good thing
about this caching abstraction is that it allows us to easily configure it to use Redis for example.

The ListingsTopicConsumer is the consumer on listings Kafka topic. It reads the messages from the topic
as raw string values(in JSON format) and then converts them to Java objects using Jackson. Then,
based on the mode of the event it either creates, updates or deletes the listing while also doing some
additional validations before persisting the data.

The ListingsTopicProducer is the producer on the listings Kafka topic. It converts the ListingEvent object
to the raw string(in JSON format) using Jackson and then sends it to the listings topic where it will be
consumed by the ListingsTopicConsumer. The ListingEvent is created within the DefaultListingService service
from the ListingEventDto object that we received from the API request.