# Shopping Cart Microservices

This is a technical test project that simulates a shopping cart system using a microservices architecture with Java Spring Boot. The solution is divided into three independent microservices:

## 🧱 Microservices

### 1. Product Service
- Acts as a proxy for the external [FakeStore API](https://fakestoreapi.com).
- Provides endpoints to retrieve, create, update, and delete products.
- Supports pagination and proper HTTP status management.
- Includes `/actuator/info` for metadata.

### 2. Order Service
- Handles the creation and retrieval of customer orders.
- Calculates order totals based on product prices.
- Validates product data through the Product Service.
- Returns enriched response with product details and totals.
- Simulates in-memory storage using `ConcurrentHashMap`.

### 3. Payment Service
- Simulates payment processing for orders.
- Validates order existence, customer identity, and total amount.
- Uses `RestTemplate` to consume the Order Service.
- Returns structured payment status (`SUCCESS`, `FAILED`) based on validations.

## 🚀 Technologies Used

- Java 21
- Spring Boot 3.x
- Maven
- Spring Web, Validation, Actuator
- RestTemplate
- Lombok
- Postman Collection (included)

## 🔧 How to Run

Each microservice can be run independently using:
`mvn spring-boot:run`

Ports:
- Product Service: `8081`
- Order Service: `8082`
- Payment Service: `8083`

## 📂 Postman Collection

A Postman collection is included for testing all endpoints easily.

## 👨‍💻 Developer

**Alfredo Alas**

---
# Microservicios del Carrito de Compras

Este es un proyecto de prueba técnica que simula un sistema de carrito de compras utilizando una arquitectura de microservicios con Java Spring Boot. La solución está dividida en tres microservicios independientes:

## 🧱 Microservicios

### 1. Servicio de Productos
- Actúa como proxy de la API externa [FakeStore API](https://fakestoreapi.com).
- Proporciona endpoints para obtener, crear, actualizar y eliminar productos.
- Soporta paginación y gestión adecuada de códigos HTTP.
- Incluye `/actuator/info` para metadatos.

### 2. Servicio de Órdenes
- Maneja la creación y consulta de órdenes de clientes.
- Calcula totales de órdenes en base al precio de productos.
- Valida productos consultando al Servicio de Productos.
- Devuelve la orden enriquecida con detalles y subtotales.
- Simula almacenamiento en memoria usando `ConcurrentHashMap`.

### 3. Servicio de Pagos
- Simula el procesamiento de pagos de las órdenes.
- Valida existencia de la orden, identidad del cliente y monto total.
- Usa `RestTemplate` para consumir el Servicio de Órdenes.
- Devuelve estado estructurado del pago (`SUCCESS`, `FAILED`) según validaciones.

## 🚀 Tecnologías Usadas

- Java 21
- Spring Boot 3.x
- Maven
- Spring Web, Validation, Actuator
- RestTemplate
- Lombok
- Colección Postman (incluida)

## 🔧 Cómo Ejecutar

Cada microservicio puede ejecutarse de forma independiente con:
`mvn spring-boot:run`


Puertos:
- Servicio de Productos: `8081`
- Servicio de Órdenes: `8082`
- Servicio de Pagos: `8083`


## 📂 Colección Postman

Se incluye una colección de Postman para probar todos los endpoints fácilmente.

## 👨‍💻 Desarrollador

**Alfredo Alas**


