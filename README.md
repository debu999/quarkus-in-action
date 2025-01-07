# quarkus-in-action
all handson for the book quarkus in action by redhat

- Install Mandrel in Mac `sdk install java 24.1.1.r23-mandrel`
- java version
```
java -version
openjdk version "23.0.1" 2024-10-15
OpenJDK Runtime Environment Temurin-23.0.1+11 (build 23.0.1+11)
OpenJDK 64-Bit Server VM Temurin-23.0.1+11 (build 23.0.1+11, mixed mode, sharing)
```
- GRAALVM_HOME: `/Library/Java/JavaVirtualMachines/sdkman-current/Contents/Home`
- native compilation: `mvn package -Pnative`
- container build linux: `mvn package -Pnative -Dquarkus.native.container-build=true`
- run native: `./target/quarkus-in-action-1.0.0-SNAPSHOT-runner`
- run quarkus app: `java -jar target/quarkus-app/quarkus-run.jar`
- In reservation-service i have a file application-db.properties and application.properties
  - ```properties
    pg_server=LOCAL_PG_SERVER
    quarkus.datasource.jdbc.url=jdbc:postgresql://${pg_server}:5432/reservation?currentSchema=reservation
    quarkus.datasource.username=PG_USER
    quarkus.datasource.password=SECRET_PG_CREDS
    ```
- prometheus.yml
  - ```yaml
    global:
      scrape_interval: 60s
    remote_write:
      - url: https://prometheus-prod-xxxxxxxx.grafana.net/api/prom/push
        basic_auth:
          username: 1234567
          password: GRAPHANA_TOKEN_REMOTE_WRITE
    scrape_configs:
      - job_name: 'inventory-service'
        scrape_interval: 10s
        metrics_path: '/q/metrics'
        static_configs:
        - targets:
          - host.docker.internal:8083
    ```
- 
