**Prerequisites**
- Java 17

**Set up**
- Pentru crearea inedxului se ruleaza functia ```main() ``` din clasa   ```IndexBuilder```. Datele pentru acesta sunt luate din 80 de fisiere aflate in directorul ```wiki-subset```. Indexul format este salvat in directorul ```resources\wiki-index```. In cazul nostru crearea acestuia a durat 3h 30m.
  - indexul deja creat poate fi descarcat de la adresa: 
- Clasa  ```SearchBuilder``` testeaza intrebarile pe indexul creat anterior. Cele 100 de intrebari sunt luate din fisierul ```resources\questions.txt```. Rangul documentelor ce contin raspunsul sunt salvate in fisierul ```resources\ranks.txt```. 
- Clasa ```MetricsComparison```  compară rezultatele obținute în urma interogării pe index cu rezultatele
  obținute în urma optimizării realizate folosind aplicația ChatGPT. Rezultatele obtinute de ChatGPT sunt in fisierul ```resources\chatGPT\results.txt```. Fișier care a fost
  completat manual cu răspunsurile oferite de aplicație

**Link-uri utile**