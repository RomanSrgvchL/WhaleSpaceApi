## Требования

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## Инструкция запуска проекта

### 1. Клонируйте данный репозиторий и перейдите в каталог проекта

```bash
git clone https://github.com/RomanSrgvchL/WhaleSpaceApi.git
cd WhaleSpaceApi
```

### 2. Настройте переменные окружения

```bash
cp .env.example .env
```

> Файла `.env.example` достаточно для локального запуска. При необходимости измените значения переменных в файле `.env`.

### 3. Запустите контейнеры через Docker Compose

```bash
docker compose up -d
```

### 4. Откройте приложение

Введите в адресной строке браузера:

```
http://localhost
```