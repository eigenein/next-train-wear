# Next Train Wear

## Issue Tracker

# Version 1

1. [x] :bangbang: Приложение падает в режиме полета.
2. [x] :thought_balloon: Кэшировать ответы API.
3. [x] :thought_balloon: Обратный отсчет.
5. [x] :thought_balloon: Логгировать запросы к API в Google Analytics.
6. [ ] :thought_balloon: Голландский язык.
10. [x] :thought_balloon: Отображать отсутствие рейсов.
17. [ ] :thought_balloon: Обучающие подсказки.
19. [x] :thought_balloon: Затенять экран в ambient mode.
22. [ ] :thought_balloon: Выводить текущее время на экране с рейсом.
23. [ ] :thought_balloon: Обновление статуса рейса.
27. [ ] :thought_balloon: Прокручивать список рейсов, когда поезд уходит.
31. [ ] :thought_balloon: Улучшить отображение отсутствия рейсов: добавить станцию отправления, выделить станции жирным шрифтом и вывести текущее время.
32. [ ] :bangbang: Таймер перестает обновляться в ambient mode, система все же засыпает. Вариант: периодически будить `AlarmManager`-ом.
33. [ ] :thought_balloon: Скрыть экран настроек до следующего обновления.
34. [ ] :warning: Не отображаются опаздывающие рейсы, если planned departure time прошло.

# Future Versions

4. [ ] :thought_balloon: Экран настроек.
7. [ ] :thought_balloon: Уведомлять про изменение или отмену рейса.
8. [ ] :thought_balloon: Выделять изменение платформы.
9. [ ] :thought_balloon: Показывать отмеченные станции вверху списка.
11. [ ] :thought_balloon: Выводить тип поезда.
12. [ ] :thought_balloon: Выводить длину поезда и удобства.
13. [ ] :thought_balloon: Отображать число пересадок.
14. [ ] :thought_balloon: Выключать фильтрацию отмененных рейсов в настройках и выделять их цветом фона. Помнить про фон в ambient mode.
15. [ ] :thought_balloon: Ручной выбор станции отправления.
16. [ ] :thought_balloon: Подумать над редизайном экрана с обратным отсчетом.
18. [ ] :warning: Не обновлять `JourneyOptionsAdapter`, если данные не изменились.
20. [ ] :thought_balloon: Отключать сглаживание в ambient mode.
21. [ ] :thought_balloon: Отключать анимацию в ambient mode.
24. [ ] :thought_balloon: Уведомления об изменении статуса рейса.
25. [ ] :thought_balloon: Добавить стабильные ID в адаптеры.
26. [ ] :thought_balloon: Exponential backoff на вызове `trainPlanner`.
28. [ ] :thought_balloon: Включать/выключать текущее местоположение в настройках.
29. [ ] :thought_balloon: Режим "В пути".
30. [ ] :thought_balloon: Отправлять запрос только когда прокрутка завершена (debouncing).
