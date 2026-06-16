# Currency Exchange Service

REST API для работы с валютами и обменными курсами. Позволяет просматривать, добавлять и редактировать валюты и курсы, а также выполнять конвертацию денежных сумм по прямому, обратному и кросс-курсу.

Проект выполнен в рамках изучения Java Backend и демонстрирует построение многослойного веб-приложения на Servlet API с использованием JDBC и SQL-базы данных.

---

## 🚀 Возможности

* Получение списка валют
* Получение валюты по коду
* Добавление новых валют
* Получение списка обменных курсов
* Получение курса по валютной паре
* Добавление новых курсов
* Обновление существующего курса (PATCH)
* Конвертация валют
* Поддержка прямого курса
* Поддержка обратного курса
* Поддержка кросс-курса
* Централизованная обработка ошибок
* Валидация входных данных
* Тестовый веб-интерфейс

---

## 🛠 Технологии

* Java 17+
* Jakarta Servlet
* JDBC
* HikariCP
* Jackson
* PostgreSQL / SQLite
* Maven
* Apache Tomcat 10+
* SLF4J
* Logback

---

## 📦 Структура проекта

```text
src/
├── main/
│   ├── java/com/dandaev/edu/
│   │   ├── controller/
│   │   ├── dao/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── filter/
│   │   ├── mapper/
│   │   ├── model/
│   │   ├── service/
│   │   ├── util/
│   │   └── validator/
│   │
│   ├── resources/
│   │   ├── sql/
│   │   │   ├── create_db.sql
│   │   │   ├── schema.sql
│   │   │   └── data.sql
│   │   ├── application.properties
│   │   └── database.properties
│   │
│   └── webapp/
│       ├── css/
│       ├── js/
│       ├── WEB-INF/
│       ├── index.html
│       └── index.jsp
```

---

## 📡 REST API

### Валюты

| Метод | Endpoint           | Описание                |
| ----- | ------------------ | ----------------------- |
| GET   | `/currencies`      | Получить список валют   |
| GET   | `/currency/{code}` | Получить валюту по коду |
| POST  | `/currencies`      | Добавить новую валюту   |

Пример:

```http
GET /currency/USD
```

---

### Обменные курсы

| Метод | Endpoint               | Описание                       |
| ----- | ---------------------- | ------------------------------ |
| GET   | `/exchangeRates`       | Получить список курсов         |
| GET   | `/exchangeRate/{pair}` | Получить курс по валютной паре |
| POST  | `/exchangeRates`       | Добавить новый курс            |
| PATCH | `/exchangeRate/{pair}` | Обновить курс                  |

Пример:

```http
GET /exchangeRate/USDEUR
```

---

### Конвертация валют

| Метод | Endpoint                               |
| ----- | -------------------------------------- |
| GET   | `/exchange?from=USD&to=RUB&amount=100` |

---

## 🔧 Локальный запуск

### 1. Клонирование проекта

```bash
git clone https://github.com/your-username/currency-exchange-service.git
cd currency-exchange-service
```

### 2. Подготовка базы данных

SQL-скрипты находятся в директории:

```text
src/main/resources/sql
```

Выполните их в следующем порядке:

```text
create_db.sql
schema.sql
data.sql
```

### 3. Настройка подключения к БД

Укажите параметры подключения в файле:

```text
src/main/resources/database.properties
```

или через переменные окружения.

### 4. Сборка проекта

```bash
mvn clean package
```

### 5. Развертывание

Скопируйте WAR-файл в каталог Tomcat:

```text
$CATALINA_HOME/webapps/
```

или загрузите его через Tomcat Manager.

### 6. Запуск

После запуска приложение будет доступно по адресу:

```text
http://localhost:8080/simple-currency-exchange-service/
```

---

## 🐳 Деплой

1. Установить Java и Tomcat на сервер.
2. Выполнить сборку проекта:

```bash
mvn clean package
```

3. Передать WAR-файл на сервер.
4. Разместить его в каталоге:

```text
$CATALINA_HOME/webapps/
```

5. Перезапустить Tomcat.

---

## ✅ Реализовано

* Servlet-based REST API
* JDBC DAO слой
* DTO и маппинг моделей
* Валидация входящих данных
* Централизованная обработка ошибок
* Конвертация по прямому, обратному и кросс-курсу
* Пул соединений HikariCP
* Логирование через SLF4J + Logback

---

## 📬 Контакты

**Арсен Дандаев**

Telegram: https://t.me/Aibek_Dandaev

GitHub: https://github.com/AmanbekAzizUulu

---

## 📄 License

This project is licensed under the GNU General Public License v3.0 (GPL-3.0).

See the LICENSE file for details.
