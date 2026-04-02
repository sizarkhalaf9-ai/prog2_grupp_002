# PROG2: Övningsuppgift 2

# Instruktioner

Se iLearn.

# Kompilering och testning

Kompilera koden med:

```bash
./mvnw compile
```

(byt ut `./mvnw` till `mvnw.bat` på Windows)

Paketera till ett `.jar`-arkiv med:

```bash
./mvnw package -Dmaven.test.skip
```

(ni behöver lägga till `-Dmaven.test.skip` om alla testfall inte lyckas)

Ni kan köra tillämpningsprogrammet med (efter `package`):

```bash
java -cp target/ovning2-1.0-SNAPSHOT.jar se.su.ovning2.Exercise2
```

Kör testfallen med:

```bash
./mvnw test
```
