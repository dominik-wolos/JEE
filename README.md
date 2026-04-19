# Laboratorium 5 - Spring Boot i JPA

Projekt realizujący zadania z laboratorium 5 dotyczące Spring Boot, Spring Data JPA i Hibernate.

## Struktura projektu

```
pai_springboot/
├── src/
│   └── main/
│       ├── java/
│       │   └── bp/
│       │       └── pai_springboot/
│       │           ├── Main.java                    # Klasa startowa aplikacji
│       │           ├── controllers/
│       │           │   └── PageController.java      # Kontroler obsługujący żądania HTTP
│       │           ├── entities/
│       │           │   └── Zadanie.java             # Encja JPA reprezentująca zadanie
│       │           └── repositories/
│       │               └── ZadanieRepository.java   # Repozytorium do operacji CRUD
│       └── resources/
│           └── application.properties               # Konfiguracja aplikacji
├── pom.xml                                          # Konfiguracja Maven
└── README.md                                        # Ten plik
```

## Wymagania

- Java 17 lub nowsza
- Maven 3.6+
- (Opcjonalnie) MySQL 5.7+ dla Task 5.3

## Konfiguracja

### Baza danych H2 (domyślna)

Aplikacja domyślnie używa bazy H2 w trybie in-memory. Dane są przechowywane tylko w pamięci RAM i są tracone po restarcie aplikacji.

Aby zapisywać dane do pliku, odkomentuj w `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:./bazaDanych
```

### Baza danych MySQL (Task 5.3)

Aby przełączyć się na MySQL:

1. Zakomentuj zależność H2 i odkomentuj MySQL w `pom.xml`:
```xml
<!-- H2 Database -->
<!--
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
-->

<!-- MySQL Connector -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

2. W `application.properties` zakomentuj konfigurację H2 i odkomentuj MySQL:
```properties
# MySQL Configuration
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.naming-strategy=org.hibernate.cfg.ImprovedNamingStrategy
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
```

3. Upewnij się, że serwer MySQL jest uruchomiony i istnieje baza danych `test`:
```sql
CREATE DATABASE IF NOT EXISTS test;
```

## Uruchomienie aplikacji

### Kompilacja
```bash
cd pai_springboot
mvn clean install
```

### Uruchomienie
```bash
mvn spring-boot:run
```

lub

```bash
java -jar target/pai_springboot-1.0-SNAPSHOT.jar
```

Aplikacja będzie dostępna pod adresem: `http://localhost:8081`

## Dostępne endpointy (Tasks 5.1-5.4)

### Task 5.1 - Podstawowe endpointy

- `http://localhost:8081/` - Strona główna
- `http://localhost:8081/hello` - Druga strona testowa

### Task 5.2 & 5.4 - Operacje na zadaniach

- `http://localhost:8081/listaZadan` - Wyświetla wszystkie zadania (generuje 10 testowych przy pierwszym uruchomieniu)

### Task 5.4 - Wyszukiwanie

- `http://localhost:8081/wykonane/{status}` - Filtruje zadania według statusu wykonania
  - Przykład: `http://localhost:8081/wykonane/true` - zadania wykonane
  - Przykład: `http://localhost:8081/wykonane/false` - zadania niewykonane

- `http://localhost:8081/kosztMniejszyNiz/{max}` - Znajduje zadania o koszcie mniejszym niż podana wartość
  - Przykład: `http://localhost:8081/kosztMniejszyNiz/1500` - zadania kosztujące mniej niż 1500

- `http://localhost:8081/koszt/{min}/{max}` - Znajduje zadania o koszcie w zadanym przedziale
  - Przykład: `http://localhost:8081/koszt/1000/1500` - zadania kosztujące od 1000 do 1500

- `http://localhost:8081/delete/{id}` - Usuwa zadanie o podanym ID
  - Przykład: `http://localhost:8081/delete/5` - usuwa zadanie o ID=5

## Zadanie 5.5 - Aplikacja z bazą World

Aby utworzyć aplikację dla bazy world:

### 1. Utworzenie nowego projektu

Skopiuj strukturę tego projektu i zmień:
- Nazwę artefaktu w `pom.xml` na np. `pai_springboot_world`
- Nazwę pakietu na np. `bp.pai_springboot_world`

### 2. Utworzenie encji Country

```java
package bp.pai_springboot_world.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "country")
public class Country {

    @Id
    private String Code;
    private String Name;
    private String Continent;
    private Double SurfaceArea;
    private Integer Population;

    // Konstruktory, gettery, settery i toString()
}
```

### 3. Konfiguracja dla bazy world

W `application.properties`:
```properties
server.port=8082

spring.jpa.hibernate.ddl-auto=none
spring.datasource.url=jdbc:mysql://localhost:3306/world?serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=
spring.jpa.show-sql=true

# Ważne dla mapowania nazw pól!
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

**UWAGA:** Ustawienie `spring.jpa.hibernate.naming.physical-strategy` jest kluczowe dla poprawnego mapowania pól typu `SurfaceArea` na kolumny w bazie.

### 4. Utworzenie repozytorium

```java
package bp.pai_springboot_world.repositories;

import bp.pai_springboot_world.entities.Country;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CountryRepository extends CrudRepository<Country, String> {

    // Wyszukiwanie krajów z danego kontynentu
    Iterable<Country> findByContinent(String continent);

    // Wyszukiwanie krajów o populacji z zadanego przedziału
    Iterable<Country> findByPopulationBetween(Integer min, Integer max);

    // Wyszukiwanie krajów danego kontynentu o powierzchni z zadanego przedziału
    // Opcja 1: Spring Data JPA query method
    Iterable<Country> findByContinentAndSurfaceAreaBetween(
        String continent, Double minArea, Double maxArea);

    // Opcja 2: @Query annotation (jeśli query method nie działa)
    @Query("SELECT c FROM Country c WHERE c.Continent = :continent " +
           "AND c.SurfaceArea BETWEEN :minArea AND :maxArea")
    Iterable<Country> findByContinentAndSurfaceArea(
        @Param("continent") String continent,
        @Param("minArea") Double minArea,
        @Param("maxArea") Double maxArea);
}
```

### 5. Przykładowe endpointy kontrolera

```java
@RequestMapping("/continent/{name}")
@ResponseBody
public String findByContinent(@PathVariable String name) {
    StringBuilder result = new StringBuilder();
    result.append("<h2>Kraje z kontynentu: ").append(name).append("</h2>");

    for (Country c : countryRepository.findByContinent(name)) {
        result.append(c).append("<br>");
    }

    return result.toString();
}

@RequestMapping("/population/{min}/{max}")
@ResponseBody
public String findByPopulation(@PathVariable Integer min, @PathVariable Integer max) {
    // Implementacja...
}

@RequestMapping("/continent/{name}/area/{min}/{max}")
@ResponseBody
public String findByContinentAndArea(
    @PathVariable String name,
    @PathVariable Double min,
    @PathVariable Double max) {
    // Implementacja...
}
```

## Task 5.6 - Spring Security

Aby włączyć Spring Security:

1. Odkomentuj zależność w `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

2. Odkomentuj konfigurację w `application.properties`:
```properties
spring.security.user.name=beata
spring.security.user.password=beata
spring.security.user.roles=admin
```

3. Przebuduj projekt (`mvn clean install`) i uruchom ponownie.

Przy pierwszym dostępie do jakiejkolwiek strony pojawi się formularz logowania. Użyj skonfigurowanych danych:
- Login: `beata`
- Hasło: `beata`

## Szczegóły implementacji

### Adnotacje JPA

- `@Entity` - oznacza klasę jako encję JPA
- `@Id` - oznacza pole jako klucz główny
- `@GeneratedValue` - automatyczne generowanie wartości klucza głównego
- `@Column` - mapowanie pola na kolumnę w bazie danych
- `@Lob` - przechowywanie dużych obiektów (Large Object)
- `@Table` - mapowanie encji na konkretną tabelę

### Metody wyszukiwania w Spring Data JPA

Spring Data JPA automatycznie generuje implementację metod na podstawie ich nazw:

- `findBy{FieldName}` - wyszukiwanie po wartości pola
- `findBy{FieldName}LessThan` - wartości mniejsze niż
- `findBy{FieldName}Between` - wartości w przedziale
- `findBy{Field1}And{Field2}` - wyszukiwanie po wielu polach

### Alternatywnie: zapytania @Query

Jeśli automatyczne metody nie działają, można użyć adnotacji `@Query`:

```java
@Query("SELECT z FROM Zadanie z WHERE z.koszt < :maxKoszt")
Iterable<Zadanie> findCheapTasks(@Param("maxKoszt") Double maxKoszt);
```

## Testowanie

### Testowe dane

Aplikacja automatycznie generuje 10 testowych zadań przy pierwszym wywołaniu `/listaZadan`:
- Zadania 1-10
- Koszty od 1000 do 2801.50
- Naprzemiennie wykonane/niewykonane

### Przykładowe testy

1. Sprawdź stronę główną: `http://localhost:8081/`
2. Wygeneruj dane testowe: `http://localhost:8081/listaZadan`
3. Znajdź zadania wykonane: `http://localhost:8081/wykonane/true`
4. Znajdź tanie zadania: `http://localhost:8081/kosztMniejszyNiz/1500`
5. Usuń zadanie: `http://localhost:8081/delete/3`

## Eksport do JAR

Aby utworzyć wykonywalny plik JAR:

```bash
mvn clean package
java -jar target/pai_springboot-1.0-SNAPSHOT.jar
```

Utworzony JAR zawiera wszystkie zależności i wbudowany serwer Tomcat.

## Rozwiązywanie problemów

### Port 8080 jest zajęty

Zmień port w `application.properties`:
```properties
server.port=8081
```

### Błędy z Hibernate/JPA

Sprawdź:
- Czy wszystkie importy są z pakietu `javax.persistence`
- Czy baza danych jest uruchomiona (dla MySQL)
- Czy strategia nazewnictwa jest poprawnie ustawiona

### Metody wyszukiwania nie działają

Użyj zapytań z adnotacją `@Query` zamiast automatycznych metod.

## Dodatkowe zasoby

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Spring Data JPA Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)

## Autor

Projekt wykonany w ramach laboratorium PAI (Programowanie Aplikacji Internetowych).
