Установка:
```
git clone git@bitbucket.org:nserd/showclassinfo.git
```
```
cd showclassinfo && mvn package
```
> JAR: `./target/ShowClassInfo-1.0.jar`
___
*Пример работы программы:*
```
$ java -jar ShowClassInfo-1.0.jar java.io.IOException
Class: java.io.IOException
--------------------------
Interfaces : none
Parents    : java.lang.Exception <- java.lang.Throwable <- java.lang.Object
```
```
$ java -jar ShowClassInfo-1.0.jar java.lang.String
Class: java.lang.String
-----------------------
Interfaces : java.io.Serializable, java.lang.Comparable, java.lang.CharSequence, java.lang.constant.Constable, java.lang.constant.ConstantDesc
Parents    : java.lang.Object
```

