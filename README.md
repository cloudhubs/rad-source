# RAD Source

This project detects the REST clients (API calls) using static source code analysis for JAVA Spring Boot projects.

To get started clone the Github [repository](https://github.com/cloudhubs/rad-source).

```
$ git clone https://github.com/cloudhubs/rad-source.git
```

## Major Dependencies

- [Spring boot](https://spring.io/projects/spring-boot)
- [Java parser](https://github.com/javaparser/javaparser)
- [Lombok](https://projectlombok.org/)

## Core Components

1. **RestCallService:** Takes path of a single JAVA source file as input and detects all the rest calls along with their parent method, HTTP type, and return type.

## Run the Application

### Compile and run

```
$ git clone https://github.com/cloudhubs/rad-source.git
$ cd rad-source
$ mvn clean install -DskipTests
$ java -jar application/target/rad-source-application-0.0.1-SNAPSHOT.jar
```

### Sample request and response

```yaml
$ curl --request POST \
  --url http://localhost:8080/ \
  --header 'content-type: application/json' \
  --data '{
    "pathToSource": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java"
}'
```

```yaml
{
  "request": {
    "pathToSource": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java"
  },
  "restCalls": [
    {
      "httpMethod": "POST",
      "parentMethod": "edu.baylor.ecs.cms.service.EmsService.createExam",
      "returnType": "edu.baylor.ecs.cms.dto.ExamDto",
      "collection": false
    },
    {
      "httpMethod": "GET",
      "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getExams",
      "returnType": "edu.baylor.ecs.cms.model.Exam",
      "collection": true
    },
    {
      "httpMethod": "GET",
      "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getQuestionsForExam",
      "returnType": "edu.baylor.ecs.cms.model.Question",
      "collection": true
    },
    {
      "httpMethod": "GET",
      "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getINITExams",
      "returnType": "edu.baylor.ecs.cms.model.Exam",
      "collection": true
    },
    {
      "httpMethod": "DELETE",
      "parentMethod": "edu.baylor.ecs.cms.service.EmsService.deleteINITExam",
      "returnType": "uri",
      "collection": false
    }
  ]
}
```

## Integrate as library

### Compile the library

```
$ git clone https://github.com/cloudhubs/rad-source.git
$ cd rad-source
$ mvn clean install -DskipTests
```

### Add dependency to your project

```xml
<dependency>
    <groupId>edu.baylor.ecs.cloudhubs</groupId>
    <artifactId>rad-source</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Code example

```java
@Autowired
private final RadSourceService radSourceService;

public RadSourceResponseContext getRadSourceResponseContext(RadSourceRequestContext request) throws IOException {
    return radSourceService.generateRadSourceResponseContext(request);
}
```

## Core Contexts and Models

```java
public class RadSourceRequestContext {
    private String pathToSource;
}
```

```java
public class RadSourceResponseContext {
    private RadSourceRequestContext request;
    private List<RestCall> restCalls;
}
```

```java
public class RestCall {
    private String httpMethod;
    private String parentMethod;
    private String returnType;
    private boolean isCollection;
}
```
