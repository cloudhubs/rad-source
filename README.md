# RAD Source

This project detects the REST endpoints and clients (API calls) using static source code analysis for JAVA Spring Boot projects.
For multiple microservices, it also finds the rest communication between the microservices. 

To get started clone the Github [repository](https://github.com/cloudhubs/rad-source).

```
$ git clone https://github.com/cloudhubs/rad-source.git
```

## Major Dependencies

- [Spring boot](https://spring.io/projects/spring-boot)
- [Java parser](https://github.com/javaparser/javaparser)
- [Lombok](https://projectlombok.org/)

## Core Components

1. **RadSourceService:** Takes a list of paths of a single JAVA source file or, a directory as input. If input is a directory then it scans all java source files recursively and for each of them runs `RestCallService` and `RestEndpointService`.

2. **RestCallService:** Takes a single JAVA source file as input and detects all the rest calls along with their parent method, HTTP type, and return type.

3. **RestEndpointService:** Takes a single JAVA source file as input and detects all the rest endpoints along with their parent method, HTTP type, and return type.

4. **RestFlowService:** Takes a list of `RestCall` and `RestEndpoint` as input and matches them to detect rest communication between microservices.

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
    --cookie JSESSIONID=3AFF4A7EDCBFA4249E1269FDA19A77C9 \
    --data '{
      "pathToMsRoots": [
  			"C:\\seer-lab\\cil-tms\\tms-cms",
  			"C:\\seer-lab\\cil-tms\\tms-ems"
  		]
  }'
```

```yaml
{
  "request": {
    "pathToMsRoots": [
      "C:\\seer-lab\\cil-tms\\tms-cms",
      "C:\\seer-lab\\cil-tms\\tms-ems"
    ]
  },
  "restEntityContexts": [
    {
      "pathToMsRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
      "restCalls": [
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
          "httpMethod": "POST",
          "parentMethod": "edu.baylor.ecs.cms.service.EmsService.createExam",
          "returnType": "edu.baylor.ecs.cms.dto.ExamDto",
          "collection": false
        },
        ...
      ],
      "restEndpoints": [
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\CategoryInfoController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.controller.CategoryInfoController.getCategoryInfo",
          "arguments": "[]",
          "returnType": "java.lang.Object",
          "collection": true
        },
        ...
      ]
    },
    ...
  ],
  "restFlows": [
    {
      "client": {
        "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
        "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
        "httpMethod": "GET",
        "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getQuestionsForExam",
        "returnType": "edu.baylor.ecs.cms.model.Question",
        "collection": true
      },
      "endpoint": {
        "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
        "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
        "httpMethod": "GET",
        "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.listAllQuestionsForExam",
        "arguments": "[@PathVariable Integer id]",
        "returnType": "edu.baylor.ecs.ems.model.Question",
        "collection": true
      }
    },
    ...
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
    private List<String> pathToMsRoots;
}
```

```java
public class RadSourceResponseContext {
    private RadSourceRequestContext request;
    private List<RestEntityContext> restEntityContexts;
    private List<RestFlow> restFlows;
}
```

```java
public class RestEntityContext {
    String pathToMsRoot;
    private List<RestCall> restCalls;
    private List<RestEndpoint> restEndpoints;
}
```

```java
public class RestCall {
    private String msRoot;
    private String source;
    private String httpMethod;
    private String parentMethod;
    private String returnType;
    private boolean isCollection;
}
```

```java
public class RestEndpoint {
    private String msRoot;
    private String source;
    private String httpMethod;
    private String parentMethod;
    private String arguments;
    private String returnType;
    private boolean isCollection;
}
```

```java
public class RestFlow {
    private RestCall client;
    private RestEndpoint endpoint;
}
```
