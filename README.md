# PLANAZO - Backend

Backend API REST para la aplicación móvil PLANAZO, una plataforma social para crear y unirse a planes (deportes, senderismo, cafés, lecturas, etc.).

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 4.0.0**
- **PostgreSQL 16**
- **Maven**
- **Lombok**

## 📋 Requisitos previos

- Java 21 o superior
- Maven 3.8+
- PostgreSQL 16 instalado y corriendo en localhost:5432
- Base de datos `planazo_db` creada

## ⚙️ Configuración

1. Clona el repositorio:
git clone https://github.com/TU_USUARIO/planazo-backend.git
cd planazo-backend

2. Configura la base de datos:
CREATE DATABASE planazo_db;

3. Configura las credenciales en `src/main/resources/application.properties`:
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD

## 🏃 Ejecutar localmente

mvn clean install
mvn spring-boot:run

La aplicación arrancará en: `http://localhost:8080`

## 📁 Estructura del proyecto

planazo-backend/
├── src/
│ ├── main/
│ │ ├── java/com/planazo/
│ │ │ ├── controller/ # Controladores REST
│ │ │ ├── service/ # Lógica de negocio
│ │ │ ├── repository/ # Repositorios JPA
│ │ │ ├── model/ # Entidades
│ │ │ ├── dto/ # DTOs
│ │ │ ├── config/ # Configuraciones
│ │ │ ├── security/ # Seguridad y JWT
│ │ │ └── exception/ # Manejo de excepciones
│ │ └── resources/
│ │ └── application.properties
│ └── test/
├── docs/ # Documentación
├── pom.xml
└── README.md









