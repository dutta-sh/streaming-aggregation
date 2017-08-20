# n26-coding-challenge

The code needs Java 8 to compile and run.
The solution can be executed as a jar from command line. 
It is built using spring boot and uses a bundled tomcat server.
The HTTP Calls run on port 8080.

To ensure the webservice is running, a healthcheck on the context root can be done as:
http://localhost:8080/

1. The in memory solution to persist data and retrieve statistics executes in O(1) for space and time.
2. The solution is threadsafe
3. The API processes data that is upto 60 seconds old and discards anything older or from the future
4. Junit test cases have been provided for each service class and has a code coverage of 100%
5. The service method returning the statistics at any point in time, returns a clone of the actual data, so any modification done by the caller is failsafe.
6. The project can be built using mvn clean package. 
7. The build generates a single uber jar which contains all dependencies.
8. The jar is built in the target folder by the name: n26-coding-challenge.jar
9. The jar can be executed using: java -jar n26-coding-challenge.jar
10. The workspace in github contains the source code and the jar
11. The code has been written using Intellij IDEA
12. REST calls have been tested using Chrome browser (GET) and Postman (GET and POST)
