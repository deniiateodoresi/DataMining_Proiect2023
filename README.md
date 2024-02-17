##Prerequisites
- Java 17
- Gradle

##Set up
1. Clonarea repozitorului: ```git clone https://github.com/deniiateodoresi/DataMining_Proiect2023.git```
2. Utilizarea gradle pentru partea de build a proiectului: ```gradle build```

##Rulare proiect
1. Pentru crearea inedxului se ruleaza functia ```main() ``` din clasa   ```IndexBuilder```. Datele pentru acesta sunt luate din 80 de fisiere aflate in directorul ```wiki-subset```. In cazul nostru crearea acestuia a durat 3h 30m.
- Nota: indexul deja creat poate fi descarcat accesand: [Index](https://ubbcluj-my.sharepoint.com/personal/denisa_ateodoresi_stud_ubbcluj_ro/_layouts/15/onedrive.aspx?id=%2Fpersonal%2Fdenisa%5Fateodoresi%5Fstud%5Fubbcluj%5Fro%2FDocuments%2FIndex%5FDataMining%5FProiect2023&ga=1) 
2. Clasa  ```SearchBuilder``` testeaza intrebarile pe indexul creat anterior. Cele 100 de intrebari sunt luate din fisierul ```resources\questions.txt```. Rangul documentelor ce contin raspunsul sunt salvate in fisierul ```resources\ranks.txt```. Rezultatele sunt salvate si in fisierul ```index-results.txt```
3. Clasa ```MetricsComparison```  compară rezultatele obținute în urma interogării pe index cu rezultatele
    obținute în urma optimizării realizate folosind aplicația ChatGPT. Rezultatele obtinute de ChatGPT sunt in fisierul ```resources\chatGPT\results.txt```, fișier care a fost
    completat manual cu răspunsurile oferite de aplicație

##Link-uri utile

1. [Indexul generat](https://ubbcluj-my.sharepoint.com/personal/denisa_ateodoresi_stud_ubbcluj_ro/_layouts/15/onedrive.aspx?id=%2Fpersonal%2Fdenisa%5Fateodoresi%5Fstud%5Fubbcluj%5Fro%2FDocuments%2FIndex%5FDataMining%5FProiect2023&ga=1)
2. [Documentatie](https://github.com/deniiateodoresi/DataMining_Proiect2023/blob/main/Documentatie%20-%20Building%20(a%20part%20of)%20Watson.pdf)
3. [Videoclip prezetare](https://github.com/deniiateodoresi/DataMining_Proiect2023/blob/main/Prezentare%20Video%20%20-%20Building%20(a%20part%20of)%20Watson.mkv)
4. [Slide-uri prezentare](https://github.com/deniiateodoresi/DataMining_Proiect2023/blob/main/Slides%20Prezentare%20-%20Building%20(a%20part%20of)%20Watson.pdf)
5. [Fisier Rezultate](https://github.com/deniiateodoresi/DataMining_Proiect2023/blob/main/index-results.txt)
6. [Intrebari GPT](https://chat.openai.com/share/9dcd7be8-1a04-459a-a27a-f04b5ea85a39)