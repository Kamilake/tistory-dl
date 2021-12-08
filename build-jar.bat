chcp 65001
rm -r build-jar/*
rmdir /s /q build-jar
rmdir -r build-jar
mkdir build-jar
unzip -o ./libs/*.jar -d build-jar
javac -cp libs/* -encoding utf-8 -d ./build-jar src/mainmain\*
chcp 949
jar -cfve tistory-dl.jar mainmain.Backup -C build-jar .
pause