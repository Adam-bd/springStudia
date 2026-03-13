## Napisz program, który umożliwi wypożyczenie pojazdu. Prosta wersja programu powinna zawierać:

#### **1. Abstrakcyjną klasę Vehicle**, z polami:
- pole, które jednoznacznie będzie odróżniało od siebie pojazdy,
- brand,
- model,
- year,
- price,
- rented.
Metodą toVCS zwracającą String z polami w jednym wierszu oddzielone średnikiem.
Metodę toString.

#### **2. Klasy potomne:**
- Car,
- Motorcycle. Klasa Motorcycle posiada pole category (prawa jazdy - A, A1, A2, AM).

#### **3. Interfejs IVehicleRepository:**
Metody:
- rentVehicle,
- returnVehicle,
- getVehicles,
- save, load.
Należy wymyślić jakie argumenty powinny przyjmować te metody.

#### **4. Implementację interfejsu IVehicleRepositoryImpl**
- Z listą pojazdów.
Pojazdy do listy będą wczytywane z pliku oraz zapisywane do pliku po każdej zmianie.
**Uwaga!** getVehicles powinno robić głęboką kopię.
Dostosuj resztę klas do tego zadania!

#### **5. Klasę, która umożliwi interakcję z użytkownikiem** - pozwoli wyświetlić infromacje o pojazdach, wypożyczyć i zwrócić pojazd (UI konsolowe).

#### Sprawdź czy jesteś w stanie podmienić pola pojazdów z repozytorium oraz czy jesteś w stanie dodać nowe pojazdy do repozytorium z poziomu skopiowanej listy za pomocą dostarczonego Testu.
Test zakłada, że pojazdy wczytują się przy utworzeniu obiektu repo.
```
public VehicleRepositoryImpl(){
    load();
}
```
