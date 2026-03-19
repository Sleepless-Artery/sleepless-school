<div align="center">
  <h1>⚡ Sleepless School ⚡</h1>
  <p><strong>Платформа для онлайн-обучения, где каждый может быть как учеником, так и учителем.</strong></p>
  <p>
    <a href="#-архитектура">Архитектура</a> •
    <a href="#-технологический-стек">Стек</a> •
    <a href="#-особенности">Особенности</a> •
    <a href="#-быстрый-старт">Быстрый старт</a> •
    <a href="#-структура-репозитория">Структура репозитория</a>
  </p>
  <br>

  <p>
    <img src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot">
    <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?logo=postgresql&logoColor=white" alt="PostgreSQL">
    <img src="https://img.shields.io/badge/Redis-7.2-DC382D?logo=redis&logoColor=white" alt="Redis">
    <img src="https://img.shields.io/badge/Kafka-3.6-231F20?logo=apachekafka&logoColor=white" alt="Kafka">
    <img src="https://img.shields.io/badge/gRPC-1.62-990000?logo=grpc&logoColor=white" alt="gRPC">
    <img src="https://img.shields.io/badge/MinIO-2026-69B4E7?logo=minio&logoColor=white" alt="MinIO">
    <img src="https://img.shields.io/badge/Kubernetes-1.28-326CE5?logo=kubernetes&logoColor=white" alt="K8s">
  </p>
</div>

---

## 📚 О проекте

**Sleepless School** — это современная образовательная платформа, созданная с целью дать людям возможность легко обмениваться знаниями. Здесь нет строгого разделения на преподавателей и студентов: любой может проходить курсы других пользователей и создавать собственные.

### ✨ Ключевые возможности

-   **Регистрация и управление профилем** по email с подтверждением.
-   **Гибкая система заданий:**
    -   *Тесты* с автоматической проверкой.
    -   *Файловые задания* с ручной проверкой и оценкой от автора курса.
-   **Поиск и навигация** по каталогу курсов.
-   **Личный кабинет** с курсами.

---

## 🏗 Архитектура

Сервисы взаимодействуют синхронно через **gRPC** (высокопроизводительные внутренние вызовы) и асинхронно через **Apache Kafka** (события для каскадного удаления и отправки уведомлений). Единой точкой входа является API Gateway, который также валидирует JWT.

### 🧩 Микросервисы: подробно

| Сервис | Ответственность | Технологии/Протоколы |
|:---|:---|:---|
| **Gateway Service** | Единая точка входа (API Gateway), валидация JWT, маршрутизация запросов к микросервисам | Spring Security, Spring Cloud Gateway, JWT |
| **Auth Service** | Регистрация, аутентификация, генерация JWT, смена/восстановление пароля, подтверждение email (генерация кодов). Внутренняя архитектура: DDD | Spring Security, gRPC, Kafka, JWT, PostgreSQL, Redis, Flyway, Resilience4j |
| **User Service** | Управление профилями пользователей, делегирование смены email в Auth Service | gRPC, Kafka, PostgreSQL, Redis, Flyway, Resilience4j |
| **Course Service** | Управление курсами, поиск по критериям | gRPC, Kafka, PostgreSQL, Redis, Flyway, Resilience4j |
| **Lesson Service** | Управление уроками в рамках курса | gRPC, Kafka, PostgreSQL, Redis, Flyway, Resilience4j |
| **Enrollment Service** | Управление записями пользователей на курсы (записаться/покинуть) | gRPC, Kafka, PostgreSQL, Redis, Flyway, Resilience4j |
| **Assignment Service** | Управление заданиями. Поддержка двух типов: тесты (с вариантами ответов) и задания в формате файла | gRPC, Kafka, PostgreSQL, Redis, Flyway, MinIO Client, Resilience4j |
| **Submission Service** | Управление решениями, автоматическая проверка тестов, ручная оценка решений заданий в формате файлов | gRPC, Kafka, PostgreSQL, Redis, Flyway, MinIO Client, Resilience4j |
| **Notification Service** | Асинхронная отправка email-уведомлений (коды подтверждения, уведомления о событиях) | Kafka, SMTP-client |

---

## 🛠 Технологический стек и инженерные решения

-   **Язык и платформа:** Java 21, Spring Boot 4.0
-   **Межсервисное взаимодействие:**
    -   *gRPC* (синхронные вызовы) — высокая производительность и строгая типизация контрактов (`.proto` файлы).
    -   *Apache Kafka* (асинхронные события) — гарантированная доставка, слабая связанность (каскадное удаление, email-уведомления).
-   **Хранение данных:**
    -   *PostgreSQL* — основная ACID-совместимая база данных для каждого сервиса (схема "база данных на сервис").
    -   *Redis* — кэширование для снижения нагрузки на БД.
    -   *MinIO* — S3-совместимое объектное хранилище для файлов заданий и решений.
-   **Отказоустойчивость (Resilience4j):**
    -   *Circuit Breaker* — предотвращение каскадных сбоев при недоступности сервисов.
    -   *Retry* — повтор идемпотентных операций при временных сбоях сети.
    -   *TimeLimiter* — ограничение времени ожидания ответа от внешних сервисов.
-   **Безопасность:** JWT (JSON Web Tokens) для аутентификации.
-   **Контейнеризация и оркестрация:** Docker, Kubernetes, Minikube (для локальной разработки).
-   **Операторы Kubernetes:** Strimzi (для управления Kafka).
-   **Миграции БД:** Flyway (версионность схем данных).
-   **Документация API:** Swagger/OpenAPI для REST, Protobuf-файлы для gRPC-контрактов, Javadoc.

---

## ⚙️ Модель отказоустойчивости (Resilience4j)

Проект спроектирован с учетом возможных сбоев. Для защиты от них используются следующие паттерны:

-   **Circuit Breaker:** Применяется для всех gRPC-вызовов между сервисами. При превышении порога ошибок цепь "размыкается", предотвращая лавинообразные сбои.
-   **Retry:** Настроен для операций, которые могут временно завершаться ошибкой, например, при обращении к MinIO. Также используется для gRPC-вызовов в сочетании с Circuit Breaker.
-   **TimeLimiter:** Жестко ограничивает время ожидания ответа от внешних систем, таких как MinIO, чтобы не блокировать потоки приложения.

---

## 📬 Асинхронное взаимодействие (Kafka)

Kafka используется для обеспечения согласованности данных (eventual consistency) и разгрузки синхронного трафика.

---

## 🚀 Быстрый старт

### Предварительные требования

-   Установленный и настроенный **Git**
-   Установленный и запущенный **Minikube** (или любой другой Kubernetes кластер)
-   Утилита командной строки **kubectl**

### Запуск платформы в Minikube

1.  **Клонируйте репозиторий (монорепозиторий):**
   
    ```bash
    git clone https://github.com/Sleepless-Artery/sleepless-school.git
    ```

2. **Перейдите в директорию с Kubernetes-манифестами:**
    
    ```bash
    cd sleepless-school/k8s
    ```

3. **Запустите кластер Minikube:**

    ```bash
    minikube start
    ```

4. **Настройте секреты (важно!):**
   
    Файлы с реальными данными не хранятся в репозитории. Вместо них есть файлы-примеры secret.yaml.example, которые нужно настроить.

    Для Notification Service:
    ```bash
    # Скопируйте файл-пример
    cp services/notification-service/secret.yaml.example services/notification-service/secret.yaml
    ```

    Отредактируйте файл, указав реальные данные почты
    ```bash
    MAIL_USERNAME: ваша_почта@gmail.com
    MAIL_PASSWORD: ваш_пароль_или_токен_приложения
    ```

    Для Auth Service:
    ```bash
    cp services/auth-service/secret.yaml.example services/auth-service/secret.yaml
    ```
    Отредактируйте файл, обращая внимание на совпадение значений:
    ```bash
    JWT_SECRET - должен быть одинаковым в Auth Service и Gateway Service (512-битный ключ)
    ADMIN_EMAIL_ADDRESS - почта администратора
    ```

    Для Gateway Service:
    ```bash
    cp services/gateway-service/secret.yaml.example services/gateway-service/secret.yaml
    ```
    Отредактируйте файл:
    ```bash
    JWT_SECRET - обязательно укажите тот же ключ, что и в Auth Service!
    ```

    ⚠️ Важно: JWT секрет должен быть 512-битным (64 байта)

5. **Создайте namespace для платформы:**
    
    ```bash
    kubectl apply -f .\namespace.yaml
    ```

6. **Установите Kafka Operator (Strimzi):**
    
    ```bash
    kubectl create -f 'https://strimzi.io/install/latest?namespace=online-learning-platform' -n online-learning-platform
    ```

7. **Разверните Kafka-кластер:**
    
    ```bash
    kubectl apply -f .\infra\kafka\
    ```

8. **Разверните MinIO (S3-совместимое хранилище):**
    
    ```bash
    kubectl apply -f .\infra\minio\
    ```

9.  **Разверните базы данных (PostgreSQL) для каждого микросервиса:**
    
    ```bash
    kubectl apply -f .\services\auth-service\postgres
    kubectl apply -f .\services\user-service\postgres
    kubectl apply -f .\services\course-service\postgres
    kubectl apply -f .\services\lesson-service\postgres
    kubectl apply -f .\services\enrollment-service\postgres
    kubectl apply -f .\services\assignment-service\postgres
    kubectl apply -f .\services\submission-service\postgres
    ```

10. **Разверните Redis для каждого микросервиса:**
    
    ```bash
    kubectl apply -f .\services\auth-service\redis
    kubectl apply -f .\services\user-service\redis
    kubectl apply -f .\services\course-service\redis
    kubectl apply -f .\services\lesson-service\redis
    kubectl apply -f .\services\enrollment-service\redis
    kubectl apply -f .\services\assignment-service\redis
    kubectl apply -f .\services\submission-service\redis
    ```

11. **Разверните микросервисы:**
    
    ```bash
    kubectl apply -f .\services\auth-service
    kubectl apply -f .\services\user-service
    kubectl apply -f .\services\course-service
    kubectl apply -f .\services\lesson-service
    kubectl apply -f .\services\enrollment-service
    kubectl apply -f .\services\assignment-service
    kubectl apply -f .\services\submission-service
    kubectl apply -f .\services\notification-service
    kubectl apply -f .\services\gateway-service
    ```

12. **Разверните фронтенд:**
    
    ```bash
    kubectl apply -f .\frontend\
    ```

13. **Проверьте статус подов:**
    
    ```bash
    kubectl get pods -n online-learning-platform
    ```
    Дождитесь, когда все поды перейдут в состояние Running.

## 🗂 Структура репозитория

```
sleepless-school/
├── backend/                     # Исходный код микросервисов
│   ├── auth-service/
│   ├── user-service/
│   ├── course-service/
│   ├── lesson-service/
│   ├── enrollment-service/
│   ├── assignment-service/
│   ├── submission-service/
│   ├── notification-service/
│   └── gateway-service/
├── frontend/                    # Исходный код фронтенда
├── k8s/                         # Kubernetes манифесты для развертывания
│   ├── frontend/                # Манифесты фронтенда
│   ├── infra/                   # Манифесты инфраструктуры
│   │   ├── kafka/
│   │   └── minio/
│   ├── namespace.yaml           # Манифест namespace
│   └── services/                # Манифесты микросервисов
│       ├── auth-service/
│       ├── user-service/
│       ├── course-service/
│       ├── lesson-service/
│       ├── enrollment-service/
│       ├── assignment-service/
│       ├── submission-service/
│       ├── notification-service/
│       └── gateway-service/
└── README.md                    # Этот файл
```