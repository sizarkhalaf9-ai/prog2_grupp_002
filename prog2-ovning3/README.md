# PROG2: Övningsuppgift 3

# Instruktioner

Se iLearn.

# Kompilering och testning

Kompilera koden med:

```bash
./mvnw compile
```

(byt ut `./mvnw` till `mvnw.cmd` på Windows)

Paketera till ett `.jar`-arkiv med:

```bash
./mvnw package -Dmaven.test.skip
```

(ni behöver lägga till `-Dmaven.test.skip` om alla testfall inte lyckas)

Kör testfallen med:

```bash
./mvnw test
```
