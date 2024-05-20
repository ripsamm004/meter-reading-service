## REST Meter Reading Service API running on port [8081]

```
 > http://localhost:8081
```

## Service details
```
REST API that manages meter readings for gas and electricity meters.
```
## Prerequisites

- Java 17 or higher
- Gradle 8.2.1 or higher

## Test the project
```
./gradlew test
```

## Build the project
```
./gradlew build
```

## Run the project
```
./gradlew bootRun
```
---
# Fetch Meter Reads

### Sample request

**URL:** {host_URL}/api/smart/reads/{accountNumber}  
**URL Example:** http://localhost:8081/api/smart/reads/ACC123  
**Headers:**
```
Content-Type: application/json
Authorization: Basic <base64encoded_credentials>
```
## Sample responses

### Success
**Content-Type:** application/json  
**Response Code:** 200 OK  
**Response Body:**

```
{
    "accountNumber": "ACC123",
    "gasReadings": [
        {
            "id": 1,
            "meterId": 101,
            "reading": 150.0,
            "readDate": "2023-04-10",
            "usageSinceLastRead": null,
            "periodSinceLastRead": null,
            "avgDailyUsage": 3.3333333333333335,
            "gasComparison": null,
            "elecComparison": null
        },
        {
            "id": 4,
            "meterId": 101,
            "reading": 250.0,
            "readDate": "2023-05-10",
            "usageSinceLastRead": 100.0,
            "periodSinceLastRead": 30,
            "avgDailyUsage": 3.3333333333333335,
            "gasComparison": null,
            "elecComparison": null
        }
    ],
    "elecReadings": [
        {
            "id": 2,
            "meterId": 102,
            "reading": 250.0,
            "readDate": "2023-04-10",
            "usageSinceLastRead": null,
            "periodSinceLastRead": null,
            "avgDailyUsage": 3.6666666666666665,
            "gasComparison": null,
            "elecComparison": null
        },
        {
            "id": 5,
            "meterId": 102,
            "reading": 360.0,
            "readDate": "2023-05-10",
            "usageSinceLastRead": 110.0,
            "periodSinceLastRead": 30,
            "avgDailyUsage": 3.6666666666666665,
            "gasComparison": null,
            "elecComparison": null
        }
    ]
}
```

### Error (Account Not Found)
**Content-Type:** application/json  
**Response Code:** 404 Bad Request  
**Response Body:**

```
{
    "code": "API002",
    "message": "Account not found"
}
```
---

# Submit Meter Reads
## Sample request
**Request-Method:** POST  
**URL:** {host_URL}/api/smart/reads  
**URL Example:** http://localhost:8081/api/smart/reads  
**Headers:**
```
Content-Type: application/json
Authorization: Basic <base64encoded_credentials>
```
**Request Body:**


```
[
  {
    "accountNumber": "ACC123",
    "meterId": 101,
    "reading": 602.0,
    "readDate": "2023-05-11",
    "type": "GAS"
  },
  {
    "accountNumber": "ACC123",
    "meterId": 102,
    "reading": 342.0,
    "readDate": "2023-05-11",
    "type": "ELEC"
  }
]


```

## Sample responses

### Success
**Content-Type:** application/json  
**Response Code:** 201 Created  
**Response Body:**

```
[
    {
        "id": 4,
        "meterId": 101,
        "reading": 602.0,
        "readDate": "2023-05-11",
        "type": "GAS"
    },
    {
        "id": 5,
        "meterId": 102,
        "reading": 342.0,
        "readDate": "2023-05-11",
        "type": "ELEC"
    }
]
```

---

### Error (Invalid Request)
**Content-Type:** application/json  
**Response Code:** 400 Bad Request
**Response Body:**

```
{
    "code": "API007",
    "message": "Meter read requests cannot be empty"
}
```

**Content-Type:** application/json  
**Response Code:** 409 Conflict
**Response Body:**
```
{
    "code": "API005",
    "message": "Duplicate meter reading for the given date."
}
```

**Content-Type:** application/json  
**Response Code:** 400 Bad Request
**Response Body:**

```
{
    "code": "API004",
    "message": "Meter reading must be higher than the previous reading"
}
```

---

### Health Checks Endpoint

**URL:** {host_URL}/actuator/health 

**Request-Method:** GET

**URL Example:** http://localhost:8081/actuator/health

```
Response
// HTTP/1.1 200 OK
{
"status": "UP"
}
```

### Metric Endpoint

**URL:** {host_URL}/actuator/metrics 

**Request-Method:** GET

**URL Example:** http://localhost:8081/actuator/metrics  

**Headers:**
```
Content-Type: application/json
Authorization: Basic <base64encoded_credentials>
```

Response
// HTTP/1.1 200 OK
{
"names": [ ..., ..., ..., ...]
}

```
