# Используем официальный образ PostgreSQL
FROM postgres:latest

# Устанавливаем переменные окружения для базы данных
ENV POSTGRES_DB=mydatabase
ENV POSTGRES_USER=myuser
ENV POSTGRES_PASSWORD=mypassword

# Открываем порт 5432 для подключения к базе данных
EXPOSE 5432