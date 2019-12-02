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
    --cookie JSESSIONID=3AFF4A7EDCBFA4249E1269FDA19A77C9 \
    --data '{
      "pathToMsRoots": [
  			"C:\\seer-lab\\cil-tms\\tms-cms",
  			"C:\\seer-lab\\cil-tms\\tms-ems"
  		]
  }'
```

<details><summary>Response</summary>
<p>

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
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getExams",
          "returnType": "edu.baylor.ecs.cms.model.Exam",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getQuestionsForExam",
          "returnType": "edu.baylor.ecs.cms.model.Question",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.service.EmsService.getINITExams",
          "returnType": "edu.baylor.ecs.cms.model.Exam",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\EmsService.java",
          "httpMethod": "DELETE",
          "parentMethod": "edu.baylor.ecs.cms.service.EmsService.deleteINITExam",
          "returnType": "uri",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\QmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.service.QmsService.getCategoryInfoDtos",
          "returnType": "java.lang.Object",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\QmsService.java",
          "httpMethod": "POST",
          "parentMethod": "edu.baylor.ecs.cms.service.QmsService.createConfiguration",
          "returnType": "java.lang.Object",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\QmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.service.QmsService.getConfigurations",
          "returnType": "java.lang.Object",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\UmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.service.UmsService.isEmailValid",
          "returnType": "java.lang.String",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\UmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.service.UmsService.getAllUsers",
          "returnType": "java.lang.Object",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\UmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.service.UmsService.getExamineeId",
          "returnType": "java.lang.String",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\UmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.service.UmsService.getExamineeInfo",
          "returnType": "edu.baylor.ecs.cms.model.User",
          "collection": false
        }
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
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ConfigurationController.java",
          "httpMethod": "POST",
          "parentMethod": "edu.baylor.ecs.cms.controller.ConfigurationController.createConfiguration",
          "arguments": "[@RequestBody ConfigurationDto object]",
          "returnType": "java.lang.Object",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ConfigurationController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.controller.ConfigurationController.getConfigurations",
          "arguments": "[]",
          "returnType": "java.lang.Object",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ExamController.java",
          "httpMethod": "POST",
          "parentMethod": "edu.baylor.ecs.cms.controller.ExamController.createExam",
          "arguments": "[@RequestBody ExamDto object]",
          "returnType": "java.lang.Object",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.controller.ExamController.isEmailValid",
          "arguments": "[@PathVariable String email, @RequestHeader(\"Authorization\") String authorication]",
          "returnType": "edu.baylor.ecs.cms.dto.EmailDto",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.controller.ExamController.getAllUsers",
          "arguments": "[@RequestHeader(\"Authorization\") String authorication]",
          "returnType": "java.lang.Object",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.controller.ExamController.getExamDetail",
          "arguments": "[@PathVariable Integer id]",
          "returnType": "edu.baylor.ecs.cms.model.Question",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.controller.ExamController.getAllExams",
          "arguments": "[]",
          "returnType": "edu.baylor.ecs.cms.model.Exam",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.controller.ExamController.getAllExamsInStatusINIT",
          "arguments": "[]",
          "returnType": "edu.baylor.ecs.cms.model.Exam",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ExamController.java",
          "httpMethod": "DELETE",
          "parentMethod": "edu.baylor.ecs.cms.controller.ExamController.deleteExam",
          "arguments": "[@PathVariable Integer id]",
          "returnType": "java.lang.String",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
          "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.cms.controller.ExamController.getExamineeById",
          "arguments": "[@PathVariable String id, @RequestHeader(\"Authorization\") String auth]",
          "returnType": "edu.baylor.ecs.cms.model.User",
          "collection": false
        }
      ]
    },
    {
      "pathToMsRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
      "restCalls": [
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\service\\QmsService.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.ems.service.QmsService.getQuestions",
          "returnType": "new ParameterizedTypeReference<List<QuestionQmsDto>>() {\r\n}",
          "collection": false
        }
      ],
      "restEndpoints": [
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ChoiceController.java",
          "httpMethod": "POST",
          "parentMethod": "edu.baylor.ecs.ems.controller.ChoiceController.updateChoices",
          "arguments": "[@RequestBody SelectedChoiceEmsDto selectedChoiceEmsDto]",
          "returnType": "edu.baylor.ecs.ems.model.Choice",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.listAllExams",
          "arguments": "[]",
          "returnType": "edu.baylor.ecs.ems.model.Exam",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.listAllQuestionsForExam",
          "arguments": "[@PathVariable Integer id]",
          "returnType": "edu.baylor.ecs.ems.model.Question",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.getByUserName",
          "arguments": "[@PathVariable String username, @RequestHeader(\"Authorization\") String authorization]",
          "returnType": "edu.baylor.ecs.ems.model.Exam",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
          "httpMethod": "POST",
          "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.createExam",
          "arguments": "[@RequestBody ExamDto examDto]",
          "returnType": "edu.baylor.ecs.ems.model.Exam",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.takeExam",
          "arguments": "[@PathVariable(\"id\") Integer id]",
          "returnType": "edu.baylor.ecs.ems.dto.QuestionEmsDto",
          "collection": true
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.submitExam",
          "arguments": "[@PathVariable(\"id\") Integer id]",
          "returnType": "edu.baylor.ecs.ems.model.Exam",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.finishExam",
          "arguments": "[@PathVariable(\"id\") Integer id]",
          "returnType": "java.lang.String",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
          "httpMethod": "GET",
          "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.getExam",
          "arguments": "[@PathVariable(\"id\") Integer id]",
          "returnType": "edu.baylor.ecs.ems.model.Exam",
          "collection": false
        },
        {
          "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
          "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
          "httpMethod": "DELETE",
          "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.deleteINITExam",
          "arguments": "[@PathVariable Integer id]",
          "returnType": "java.lang.String",
          "collection": false
        }
      ]
    }
  ],
  "restFlows": [
    {
      "client": {
        "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
        "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\UmsService.java",
        "httpMethod": "GET",
        "parentMethod": "edu.baylor.ecs.cms.service.UmsService.isEmailValid",
        "returnType": "java.lang.String",
        "collection": false
      },
      "endpoint": {
        "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
        "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
        "httpMethod": "GET",
        "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.finishExam",
        "arguments": "[@PathVariable(\"id\") Integer id]",
        "returnType": "java.lang.String",
        "collection": false
      }
    },
    {
      "client": {
        "msRoot": "C:\\seer-lab\\cil-tms\\tms-cms",
        "source": "C:\\seer-lab\\cil-tms\\tms-cms\\src\\main\\java\\edu\\baylor\\ecs\\cms\\service\\UmsService.java",
        "httpMethod": "GET",
        "parentMethod": "edu.baylor.ecs.cms.service.UmsService.getExamineeId",
        "returnType": "java.lang.String",
        "collection": false
      },
      "endpoint": {
        "msRoot": "C:\\seer-lab\\cil-tms\\tms-ems",
        "source": "C:\\seer-lab\\cil-tms\\tms-ems\\src\\main\\java\\edu\\baylor\\ecs\\ems\\controller\\ExamController.java",
        "httpMethod": "GET",
        "parentMethod": "edu.baylor.ecs.ems.controller.ExamController.finishExam",
        "arguments": "[@PathVariable(\"id\") Integer id]",
        "returnType": "java.lang.String",
        "collection": false
      }
    }
  ]
}
```

</p>
</details>

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
