# 6302-010302D. Новикова Виктория, Мартынов Олег

# beauty-salon-booking

Веб-сервис для онлайн-записи клиентов на услуги в салон красоты.
Поддерживаются две роли пользователей:
1) Клиент — может регистрироваться, записываться на услуги, просматривать и редактировать свои записи.
2) Мастер — может управлять услугами, просматривать и подтверждать записи.
   
## Используемый стек:
Backend: Java 21, Spring Boot, Spring Data JPA, Spring Security
Frontend: Thymeleaf (Server-side rendering)
База данных: PostgreSQL
Контейнеризация: Docker + Docker Compose
Аутентификация: JWT
Формат обмена: JSON, REST API

Проект развёртывается в Docker-контейнерах и полностью изолирован для удобного запуска и тестирования.