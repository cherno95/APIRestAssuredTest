API-автотесты с использованием фреймворка OkHttp3.

Используемый фреймворк для отправки запросов клиента: RestAssured;
Используемый фреймворк для запуска тестов: allure-junit5;
Используемая библиотека для аннотаций DTO: Lombok;
Используемая библиотека для формирования отчета: io.qameta.allure
Используемая версия Java - 11.
Сборщик проектов: build.gradle

После прохождения сценариев можно сформировать Allure-отчет. Для этого необходимо:
1) Запустить тесты и дождаться их завершения;
2) gradle allureReport - команда позволяет репортировать результат тестов в виде отчета;
3) gradle allureServe - открывает страницу Allure с отчетом.