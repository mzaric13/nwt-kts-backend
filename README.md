Back-End projekta na predmetima napredne web tehnologije i konstrukcija i testiranje softvera

Studenti:
Teodor Sakal Francišković, broj indeksa: SW 22/2019
Matija Zarić, broj indeksa: SW 24/2019
Zoran Bukorac, broj indeksa: SW 40/2019

Način pokretanja:

    1. Otvoriti projekat kao IntellIj projekat
    2. Pokrenuti spring boot aplikaciju

Skripta za simulaciju kretanja:

    U folderu locust se nalazi skripta za simulaciju kretanja vozila
    
    1. Otvoriti command prompt
    2. Pozicionirati se u folder locust
    3. Kreirati viruelno okruzenje
    4. Aktivirati ga
    5. Instalirati u virtuelnom okruzenju biblioteke locust i requests
    6. Pokrenuti front-end i back-end deo aplikacije
    7. Pokrenuti skriptu komandom: locust -f locust/simulation.py --headless -u 7 -r 1 --run-time 30m