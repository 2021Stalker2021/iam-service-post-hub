FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /srv

COPY target/*.jar .
COPY docker/iam-service.sh .

# Устанавливаем bash, так как в alpine его нет по умолчанию
RUN apk add --no-cache bash

# Делаем скрипт запуска исполняемым
# Устанавливаем права на выполнение (execute permission) для скрипта
RUN chmod +x iam-service.sh

# Устанавливаем точку входа контейнера
EXPOSE 8189

# При запуске контейнера будет выполнен этот скрипт
# Используется exec форма для лучшей обработки сигналов
ENTRYPOINT ["./iam-service.sh"]