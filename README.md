# AutoManagement — Car Rental Management System

Web-приложение для управления арендой автомобилей с AI-чатботом, built with Java Spring Boot.

##  Функциональность

-  Управление автопарком (добавление, редактирование, удаление машин)
-  Управление арендой (создание, отслеживание, история)
-  Управление пользователями и сотрудниками
-  AI-чатбот на базе Groq API
-  Авторизация и аутентификация (JWT)
-  Курс валют в реальном времени

##  Технологии

- **Backend:** Java 17, Spring Boot 3, Spring Security
- **Database:** MySQL, Hibernate JPA
- **Frontend:** Thymeleaf, HTML/CSS
- **AI:** Groq API
- **Auth:** JWT Tokens

##  Запуск проекта

### Требования
- Java 17+
- MySQL 8+
- Maven

### Установка

1. Клонируй репозиторий:
   git clone https://github.com/aubakir01/AutoManagement.git

2. Создай базу данных:
   CREATE DATABASE userlistdb;

3. Настрой `application.properties`:
   spring.datasource.url=jdbc:mysql://localhost:3306/userlistdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=твой_пароль
   groq.api.key=твой_groq_ключ

4. Запусти проект:
   mvn spring-boot:run

5. Открой в браузере:
   http://localhost:8081

##  Автор

**Amir Aubakir** — [LinkedIn](https://www.linkedin.com/in/amir-aubakir) | [GitHub](https://github.com/aubakir01)