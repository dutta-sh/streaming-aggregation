# streaming-aggregation

1. The code needs Java 8 to compile and run.
2. It is built using spring boot and uses a bundled tomcat server running on port 8080.
3. The in memory solution to persist data and retrieve statistics executes in O(1) for space and time.
4. The solution is threadsafe
5. The API processes data that is upto 60 seconds old and discards anything older or from the future
6. Junit test cases have been provided for each service class and has a code coverage of 100%
7. The service method returning the statistics at any point in time, returns a clone of the actual data, so any modification done by the caller is failsafe.
8. The project can be built using mvn clean package.
9. The build generates a single uber jar which contains all dependencies.
10. The jar is built in the target folder by the name: n26-coding-challenge.jar
11. The jar can be executed using: java -jar n26-coding-challenge.jar
12. The workspace in github contains the source code and the jar
13. The code has been written using Intellij IDEA
14. REST calls have been tested using Chrome browser (GET) and Postman (GET and POST)
15. Since java.util.Date converts long tsp to show as Date with timezone on client systems, as long as we are using the raw long tsp to compute, it doesn't matter whatever timezone the system is on.

To ensure the webservice is running, a healthcheck on the context root can be done as:
http://localhost:8080/
