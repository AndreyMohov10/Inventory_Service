
# REST-сервис «Каталог товаров» (Inventory Service). Учебный REST API сервис для управления каталогом товаров и складскими остатками.


# Описание проекта
Данный сервис предназначен для ведения учета товаров на складе и предоставляет full-CRUD REST API с поддержкой пагинации, фильтрации и атомарного изменения остатков. Проект выполнен с использованием слоистой архитектуры (Controller → Service → Repository) с четким разделением DTO и JPA-сущностей.
🛠 Технологический стек
Язык: Kotlin 2.2.10, Java 23
Фреймворк: Spring Boot 3.5.5 (Spring Web, Spring Data JPA, Validation)
База данных: PostgreSQL 18
Миграции БД: Flyway
Контейнеризация: Docker & Docker Compose
Тестирование: JUnit 5, MockMvc, Testcontainers
Документация API: Springdoc OpenAPI 2.8.17 (Swagger UI)
Сборщик: Gradle (Wrapper)
---
⚙️ Требования к окружению
Перед запуском проекта убедитесь, что в вашей системе установлены:
Docker $\ge$ 20.10 и Docker Compose $\ge$ 2.0
JDK 17+ + Postgresql 16+ (для локального запуска без Docker)
Git
---
Быстрый запуск через Docker Compose
Самый простой способ запустить приложение и базу данных PostgreSQL одной командой:

1. Клонируйте репозиторий:
```bash
   git clone https://github.com/AndreyMohov10/Inventory_Service.git inventory-service
   cd inventory-service
   ```
2. Создайте файл конфигурации окружения:
```bash
   cp .env.example .env
   ```
3. Запустите контейнеры:
```bash
   docker compose up --build -d
   ```
После успешного запуска:
Приложение будет доступно по адресу: `http://localhost:8080`
Проверка здоровья (Health Check): `http://localhost:8080/actuator/health`
---
Локальный запуск (IDE / Terminal)
Если вы хотите запустить проект локально без Docker-контейнера приложения:
1. Запустите только базу данных PostgreSQL(или используйте уже существующий):
```bash
   docker compose up postgres -d
   ```

2. укажите правильные значения в .env

3. Запустите Spring Boot приложение:
С использованием Gradle:
```bash
   ./run.sh
   ```
```powershell
    ./run.ps1
```

```cmd
    ./run.cmd
```
---
Запуск тестов
Приложение покрыто Unit-тестами сервисного слоя и интеграционными тестами контроллера (с использованием MockMvc / Testcontainers).
Для запуска всех тестов выполните команду:
Gradle:
```bash
./gradlew test
```
---
Документация API (Swagger UI)
Интерактивная документация API и возможность отправки запросов доступны после запуска приложения по адресу: 
http://localhost:8080/swagger-ui.html
OpenAPI спецификация (JSON): `http://localhost:8080/api-docs`
---

Примеры cURL запросов(для windows, $id надо заменить на валидный id)
1. Проверка работоспособности сервиса
```cmd
curl -s http://localhost:8080/actuator/health
```
2. Получение списка товаров (с пагинацией и фильтром)
```cmd
curl -X GET "http://localhost:8080/api/v1/products?page=0&size=10&name=test"
```
3. Создание нового товара
```powershell
curl -X POST "http://localhost:8080/api/v1/products" ^
 -H "Content-Type: application/json" ^
 -d "{ \"sku\": \"TEST-001\", \"name\": \"Тестовый товар\", \"description\": \"Описание товара\", \"price\": 199.90, \"quantity\": 10 }"
```
4. Получение товара по ID
```cmd
curl -s -i "http://localhost:8080/api/v1/products/$id"
```
5. Полное обновление товара (PUT)
```cmd
curl -s -i -X PUT "http://localhost:8080/api/v1/products/$id" ^
  -H "Content-Type: application/json" ^
  -d "{\"sku\": \"TEST-001\",\"name\": \"Обновленный товар\",\"description\": \"Новое описание\",\"price\": 249.99,\"quantity\": 15}"
```
6. Изменение остатка на складе (PATCH)
```cmd
curl -s -i -X PATCH "http://localhost:8080/api/v1/products/$id/quantity" ^
  -H "Content-Type: application/json" ^
  -d "{\"delta\": -3}"
```
7. Удаление товара
```cmd
curl -s -i -X DELETE "http://localhost:8080/api/v1/products/$id"
```

---
Формат обработки ошибок
Все ошибки приложения возвращаются в едином формате JSON:
```json
{
  "timestamp": "2026-11-02T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product with id=42 not found",
  "path": "/api/v1/products/42"
}
```
HTTP Статус	Причина
`400 Bad Request`	Ошибка валидации входных данных (пустое имя, отрицательная цена и т.д.)
`404 Not Found`	Товар с запрашиваемым ID не найден
`409 Conflict`	Артикул (`sku`) уже существует ИЛИ остаток на складе после PATCH становится $< 0$
---

Отчет о выполнении
Что сделано:
[x] Настроен Docker Compose с PostgreSQL и приложения Spring Boot.
[X] Реализована сущность `Product` и миграции базы данных с начальными тестовыми данными (5+ записей).
[X] Реализован полный набор REST API эндпоинтов согласно ТЗ.
[X] Настроена глобальная обработка ошибок (`@ControllerAdvice`) и валидация DTO.
[X] Покрыты тестами сервисный слой (Unit) и контроллер (Integration).
[X] Подключен Swagger UI.
С какими трудностями столкнулся:
Из-за высокой версии spring-boot не было аннотации @AutoConfigureMockMvc решил тем, что спустился на 3.x
Написать curl запросы на windows
