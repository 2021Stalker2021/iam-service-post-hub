#!/usr/bin/env bash
# Указание интерпретатора: использовать bash из переменной окружения PATH

PROFILE=${PROFILE:-local-idea}
# Задаем переменную PROFILE:
# - Если переменная окружения PROFILE уже установлена, используем её значение
# - Если не установлена, используем значение по умолчанию "local-idea"
# Синтаксис ${VAR:-DEFAULT} возвращает DEFAULT, если VAR не определена или пустая

echo "Starting service with profile: $PROFILE"
# Выводим информационное сообщение в консоль с указанием используемого профиля

exec java -jar /srv/iam_service-0.0.1-SNAPSHOT.jar --spring.profiles.active=$PROFILE
# Запускаем Java-приложение:
# - exec заменяет текущий процесс (bash-скрипт) процессом java
# - java -jar запускает JAR-файл как исполняемый
# - /srv/iam_service-0.0.1-SNAPSHOT.jar - путь к файлу приложения
# - --spring.profiles.active=$PROFILE передает Spring Framework активный профиль
#   (в Spring Profiles позволяют настраивать поведение приложения для разных сред)