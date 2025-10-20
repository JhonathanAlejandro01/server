# NetMasters — Backend

Servicio backend basado en Java y Maven con migraciones SQL versionadas y soporte para ejecución local y en contenedores.

- Orquestación/infra: [docker-compose.yml](docker-compose.yml), variables en [.env](.env)
- Build: [pom.xml](pom.xml), wrappers [mvnw](mvnw) / [mvnw.cmd](mvnw.cmd)
- Configuración: [src/main/resources/application.properties](src/main/resources/application.properties), banner [src/main/resources/banner.txt](src/main/resources/banner.txt)
- Migraciones: [src/main/resources/db/migration](src/main/resources/db/migration) (p. ej. [V1__initial_structure.sql](src/main/resources/db/migration/V1__initial_structure.sql))
- Colección API: [netmasters.postman_collection.json](netmasters.postman_collection.json)
- Código fuente: [src/main/java/com/NetMasters/NetMasters](src/main/java/com/NetMasters/NetMasters)
- Pruebas: [src/test/java/com/NetMasters/NetMasters](src/test/java/com/NetMasters/NetMasters)

## Requisitos

- JDK (versión definida en [pom.xml](pom.xml))
- Docker y Docker Compose (opcional, para servicios externos)
- Maven Wrapper (provisto: [mvnw](mvnw) / [mvnw.cmd](mvnw.cmd))

## Estructura

```
.
├── .env
├── docker-compose.yml
├── pom.xml
├── netmasters.postman_collection.json
├── src
│   ├── main
│   │   ├── java/com/NetMasters/NetMasters
│   │   └── resources
│   │       ├── application.properties
│   │       ├── banner.txt
│   │       └── db/migration
│   │           └── V1__initial_structure.sql
│   └── test/java/com/NetMasters/NetMasters
└── ...
```

## Configuración

1. Variables de entorno:
   - Copia/ajusta [.env](.env) si usas Docker Compose.
   - Ajusta propiedades en [application.properties](src/main/resources/application.properties) (pueden leerse de variables de entorno con Spring Boot si está configurado).

2. Servicios externos:
   - Define/ajusta servicios en [docker-compose.yml](docker-compose.yml) (por ejemplo, base de datos).

## Ejecución

- Solo backend (local):
  ```bash
  ./mvnw spring-boot:run
  ```

- Con dependencias vía Docker:
  ```bash
  docker compose up -d
  ./mvnw spring-boot:run
  ```

- Empaquetar JAR:
  ```bash
  ./mvnw clean package
  java -jar target/*.jar
  ```

## Migraciones de base de datos

- Los scripts SQL versionados viven en [src/main/resources/db/migration](src/main/resources/db/migration) (convención típica de Flyway).
- Al iniciar la aplicación, se aplican en orden (si Flyway está habilitado en [pom.xml](pom.xml) y/o [application.properties](src/main/resources/application.properties)).

## Pruebas

- Ejecutar pruebas:
  ```bash
  ./mvnw test
  ```

- En VS Code: usa el panel de pruebas de Java o la terminal integrada con el comando anterior.

## API

- Importa la colección [netmasters.postman_collection.json](netmasters.postman_collection.json) en Postman para probar los endpoints.
- Ajusta variables de entorno/base URL conforme a tu configuración local.

## Docker

- Levantar servicios definidos:
  ```bash
  docker compose up -d
  ```
- Detener:
  ```bash
  docker compose down
  ```

## Solución de problemas

- Verifica el banner y logs de inicio para propiedades efectivas: [banner.txt](src/main/resources/banner.txt), [application.properties](src/main/resources/application.properties).
- Asegura que las credenciales/URL de la base de datos coincidan entre [.env](.env) y [application.properties](src/main/resources/application.properties).
- Si las migraciones fallan, revisa el último script aplicado en [db/migration](src/main/resources/db/migration) y corrige el orden/nombre.

## Licencia

Define la licencia del proyecto aquí.