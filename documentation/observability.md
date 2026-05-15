# MediTrack Observability

## Current Logging Flow

```text
Spring Boot JSON logs
  -> Docker container stdout
  -> Filebeat
  -> Logstash
  -> Elasticsearch index meditrack-logs-*
  -> Kibana data view
```

## Trace ID Carriers

| Boundary | Carrier |
| --- | --- |
| HTTP | `X-Trace-Id` header |
| Kafka | `X-Trace-Id` record header |
| gRPC | `x-trace-id` metadata |
| Application logs | `trace.id` and MDC `traceId` |

## Kibana Data View

Create or import a data view with:

```text
Name: MediTrack Logs
Index pattern: meditrack-logs-*
Time field: @timestamp
```

An importable starter data view is available at:

```text
elk/kibana/meditrack-data-view.ndjson
```

In Kibana:

```text
Stack Management -> Saved Objects -> Import
```

## Useful Kibana Filters

Trace one request across services:

```text
trace.id : "paste-trace-id-here"
```

Errors for one service:

```text
service.name : "patient-service" and level : "ERROR"
```

Slow requests:

```text
event.duration_ms >= 1000
```

HTTP failures:

```text
http.status_code >= 400
```

Kafka notification events:

```text
messaging.destination.name : "notifications"
```

gRPC billing calls:

```text
rpc.system : "grpc" and rpc.method : "CreateBillingAccount"
```

Logs from one Java source file:

```text
file.name : "GlobalExceptionHandler.java"
```

Stacktrace search:

```text
stack_trace : "*NullPointerException*"
```

Message search:

```text
message : "*billing*"
```

## Suggested Discover Columns

Add these columns in Kibana Discover:

```text
@timestamp
service.name
trace.id
level
message
http.method
url.path
http.status_code
event.duration_ms
messaging.destination.name
rpc.method
file.name
line.number
stack_trace
```

## Sensitive Data Masking

Current Logstash masking handles common accidental leaks in `message`, `stack_trace`, and `error.message`:

```text
password
jwt
token
authorization
secret
email addresses
```

Application code should still avoid logging raw:

```text
JWTs
passwords
Authorization headers
patient names
patient emails
recipient addresses
request/response bodies
```

Prefer structured identifiers instead:

```text
patient.id
hospital.id
notification.id
billing.account.id
event.type
downstream.service.name
```
