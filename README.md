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

1. **RadSourceService:** Takes path of a single JAVA source file or, a directory as input. If input is a directory then it scans all java source files recursively and for each of them runs `RestCallService`.

2. **RestCallService:** Takes a single JAVA source file as input and detects all the rest calls along with their parent method, HTTP type, and return type.

## Run the Application

### Compile and run

```
$ git clone https://github.com/cloudhubs/rad-source.git
$ cd rad-source
$ mvn clean install -DskipTests
$ java -jar application/target/rad-source-application-0.0.1-SNAPSHOT.jar
```

### Sample request and response

You can either use a single java source file path or a directory path in `pathToSource`.

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
      "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
      "httpMethod": "POST",
      "parentMethod": "edu.baylor.ecs.cms.service.EmsService.createExam",
      "returnType": "edu.baylor.ecs.cms.dto.ExamDto",
      "collection": false
    },
    {
      "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
      "httpMethod": "GET",
      "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getExams",
      "returnType": "edu.baylor.ecs.cms.model.Exam",
      "collection": true
    },
    {
      "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
      "httpMethod": "GET",
      "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getQuestionsForExam",
      "returnType": "edu.baylor.ecs.cms.model.Question",
      "collection": true
    },
    {
      "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
      "httpMethod": "GET",
      "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getINITExams",
      "returnType": "edu.baylor.ecs.cms.model.Exam",
      "collection": true
    },
    {
      "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
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
    private String source;
    private String httpMethod;
    private String parentMethod;
    private String returnType;
    private boolean isCollection;
}
```
