# Pong Client

## Инструкции за инсталация
На клиента са му нужни LWJGL2.9.3 native файлове за съответния OS, които се използват вътрешно от Slick2d библиотеката. Файловете могат да се изтеглят тук : http://legacy.lwjgl.org/download.php.html
Проектът също така трябва да се пусне с аргумент към Java виртуалната машина : -Djava.library.path=<пътят където са разархивирани горните файлове>/native/<linux|macosx|solaris|windows>
В Eclipse аргументите се задават като {десен бутон на проекта}->Properties->Run/Debug Settings->{Launch configuration}->Edit->VM arguments
Алтернативно може да се построи .jar и да се пусне от терминала със същите аргументи.

### Note
В по късен commit ще се опитам да пакетирам файловете с проекта.
