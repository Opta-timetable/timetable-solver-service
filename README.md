Timetable solver service API for Optaplanner Version 2.0
========================================================

Expose Optaplanner CurriculumCourse 'example' as a REST webservice. The input to this service is an 'unsolved XML' file and the output is a 'solved XML'.

### Setup
1. Clone this repo to your local
2. Open pom.xml as a new project in IntelliJ Idea
3. Allow Maven Integration to sort out all the dependencies

### Local Deployment
4. If not available already, download and unzip [Wildfly 9.0.1 Final] (http://wildfly.org/downloads/)
5. Run wildfly-9.0.1.Final/bin/standalone.sh with Java 8+
6. `cd timetable-solver-service` and Run `mvn clean package`
7. Deploy timetablesolverservice WAR file from the target folder to WildFly. Refer documentation [here] (https://docs.jboss.org/author/display/WFLY9/Getting+Started+Guide).

### APIs
* **POST localhost:8080/<specID>** 
Upload an unsolved XML file for a specification
* **GET localhost:8080/<specID>**
Pick solution for a specification
* **DELETE localhost:8080/<specID>**
Delete solved and unsolved specification files
* **POST localhost:8080/<specID>/solution**
Initialize solver and start Solving
* **GET localhost:8080/<specID>/solution**
Get Current solution status
* **DELETE localhost:8080/<specID>/solution**
Terminate current solution and remove solver

### Note 1
The repository contains a Postman collection of these requests for reference. It also contains a sample unsolvedXML file 
for testing purposes.

### Note 2
The application is hosted in: http://timetablesolverservice-kollavarsham.rhcloud.com


