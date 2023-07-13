# HeavenHR Java exercise

## How to build & run

Spring Boot allows us to do this with a very simple command:
```
mvn clean spring-boot:run
```

If necessary, you can get a jar for distribution by running:

```
mvn package
```

The resulting jar will be at `target/recruiting-1.0.jar` and it can be run by doing this:

```
java -jar target/recruiting-1.0.jar
```

To run only the tests, `mvn test` will work.

## How to use

Once the application is running, you can make requests to the API.
Here are some example requests to get you started:

### Create offer

```
curl -X POST \
  http://localhost:8080/offers \
  -H 'content-type: application/json' \
  -d '{
    "jobTitle": "Test offer",
    "startDate": "2018-01-12T06:00:00.000+0000"
  }'
```

This will return a `201` status if everything went well.

If you try to create an offer that already exists (run the code above twice), you will get an error response:

```
Status: 409
{"message":"offer_exists"}
```

### List offers

```
curl -X GET http://localhost:8080/offers
```

This endpoint uses pagination, so you can specify these parameters as well:
 * `page`: which page to display
 * `size`: the size of the page to use

For example,

```
curl -X GET 'http://localhost:8080/offers?size=5&page=2'
```

will result in something like

```
{  
   "content":[  
      {  
         "id":6,
         "jobTitle":"Mobile Engineer",
         "startDate":"2018-01-12T06:00:00.000+0000",
         "numberOfApplications":1
      },
      {  
         "id":7,
         "jobTitle":"Software Architect",
         "startDate":"2018-01-12T06:00:00.000+0000",
         "numberOfApplications":1
      },
      {  
         "id":8,
         "jobTitle":"Test offer",
         "startDate":"2018-01-12T06:00:00.000+0000",
         "numberOfApplications":0
      }
   ],
   "last":true,
   "totalElements":8,
   "totalPages":2,
   "size":5,
   "number":1,
   "first":false,
   "sort":null,
   "numberOfElements":3
}
```

### View single offer

Replace `{id}` with a valid offer id (1, for example)

```
curl -X GET http://localhost:8080/offers/{id}
```

will result in something like

```
{  
   "id":7,
   "jobTitle":"Software Architect",
   "startDate":"2018-01-12T06:00:00.000+0000",
   "numberOfApplications":1
}
```

### Apply for an offer

Replace `{id}` with a valid offer id (1, for example)

```
curl -X POST \
  http://localhost:8080/applications \
  -H 'content-type: application/json' \
  -d '{
    "email": "test@gmail.com",
    "resumeText": "sample text",
    "offerId": {id}
  }'
```

This will return a `201` status if everything went well.

If you try to create an offer that already exists (run the code above twice), you will get an error response:

```
Status: 409
{"message":"application_exists"}
```

### List all applications

This endpoint supports pagination (same as the "List offers" one).

```
curl -X GET http://localhost:8080/applications
```

### List all applications per offer

This endpoint also supports pagination (same as the "List offers" one).

Replace `{id}` with a valid offer id (1, for example)

```
curl -X GET http://localhost:8080/offers/{id}/applications
```

will result in something like

```
{  
   "content":[  
      {  
         "id":5,
         "email":"candidate_email_1@gmail.com",
         "resumeText":"Resume text for candidate candidate_email_1@gmail.com",
         "offer":{  
            "id":7,
            "jobTitle":"Software Architect",
            "startDate":"2018-01-12T06:00:00.000+0000",
            "numberOfApplications":1
         },
         "status":"APPLIED"
      }
   ],
   "last":true,
   "totalElements":1,
   "totalPages":1,
   "size":20,
   "number":0,
   "first":true,
   "sort":null,
   "numberOfElements":1
}
```

### View single application

Replace `{id}` with a valid application id (1, for example)

```
curl -X GET http://localhost:8080/applications/{id}
```

will result in something like

```
{  
   "id":5,
   "email":"candidate_email_1@gmail.com",
   "resumeText":"Resume text for candidate candidate_email_1@gmail.com",
   "offer":{  
      "id":7,
      "jobTitle":"Software Architect",
      "startDate":"2018-01-12T06:00:00.000+0000",
      "numberOfApplications":1
   },
   "status":"APPLIED"
}
```

### Progress application status

Replace `{id}` with a valid application id (1, for example)

```
curl -X PATCH \
  http://localhost:8080/applications/{id} \
  -H 'content-type: application/json' \
  -d '{
    "status": "INVITED"
  }'
```

If you try to progress the application to an invalid status (you can by running the example above twice), you will get an error response:

```
Status: 422
{"message":"invalid_status"}
```

## Structure

I used the basic structure I usually use for Spring Boot projects:

 * `controller`:  REST controllers
 * `entity`: Data model classes
 * `repository`: Spring data repositories
 * `request`: Classes used by the controllers to receive input
 * `response`: Classes used by the controllers to return output
 * `seeder`: Data initialization
 * `service`: Service classes containing most of the business logic

If the configuration were to become more complex, I would create a `config` package
and separate out the configuration classes into specific ones instead of keeping it all in `RecruitingApplication.java`.
For example: one for the database, one for spring security, another one for feature-specific beans...

## Observations

Tests were not created for all the code because I ran out of time, but I made at
least one of each (unit, integration) for the important layers. If there was more time I would
make sure all the code paths were covered.

There is also no authentication or authorization, so anyone could do anything they want. These
could be easily added with spring security. I would create two roles (recruiter, candidate) and they would
be able to access only their specific functions.

The error messages returned by the API look like codes to facilitate internationalization later.
So for example, depending on the language configured, the `application_exists` error message would be t
translated to `An application for that offer already exists with the email specified`.