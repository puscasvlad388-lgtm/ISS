# 🥗 BiteWise — Planificator de mese

Aplicație web pentru planificarea meselor, cu rețete, jurnal alimentar, urmărirea greutății, obiective nutriționale și liste de cumpărături. Construită cu **Spring Boot** (REST API) + frontend HTML/CSS/JS, bază de date **H2 in-memory**.

---

## 🔒 Securitate (important — ești pe rețeaua căminului)

Aplicația este configurată să asculte **exclusiv pe `127.0.0.1` (localhost)**. Concret, în `application.properties`:

```
server.address=127.0.0.1
```

Asta înseamnă că serverul acceptă conexiuni **doar de pe acest laptop**. Nimeni altcineva din rețeaua căminului nu poate ajunge la aplicație, indiferent ce IP ai sau ce setări are routerul — sistemul de operare nu rutează `127.0.0.1` către exterior. Nu se deschide niciun port spre rețea.

Baza de date este H2 in-memory: trăiește doar în RAM cât rulează aplicația și se șterge complet la oprire. Nu se scrie nimic pe disc.

---

## ▶️ Cum rulezi (în IntelliJ IDEA)

1. **Deschide proiectul**: `File → Open` și selectează folderul `bitewise`. IntelliJ va recunoaște că e un proiect Gradle și va importa automat dependențele (ai nevoie de internet la primul import — e sigur, doar descarcă librării din Maven Central).
2. Așteaptă să se termine sincronizarea Gradle (bara de jos).
3. **Rulează aplicația**: deschide `src/main/java/com/bitewise/BiteWiseApplication.java` și apasă pe săgeata verde ▶ din dreptul metodei `main`.
4. Deschide în browser: **http://localhost:8080**

> 💡 Dacă IntelliJ îți cere un JDK, alege **JDK 21** (proiectul e configurat pentru Java 21).

### Alternativ, din terminal

```bash
# Linux / macOS
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

> **Notă despre Gradle wrapper:** dacă vezi o eroare legată de `gradle-wrapper.jar` lipsă, rulează o dată `gradle wrapper` (dacă ai Gradle instalat) **sau** pur și simplu lasă IntelliJ să facă importul — îl regenerează automat. Cel mai simplu e să deschizi proiectul direct în IntelliJ, care nu are nevoie de wrapper.

---

## 🧭 Funcționalități

| Pagină | Ce face |
|--------|---------|
| **Panou** | Rezumat: BMI, necesar caloric, consum azi, alimente ce expiră |
| **Profil** | Date personale + calcul automat BMI / BMR / TDEE |
| **Greutate** | Istoricul greutății cu grafic |
| **Obiective** | Ținte zilnice de calorii și macronutrienți |
| **Jurnal** | Înregistrare mese; scade automat din cămară |
| **Cămară** | Stoc de alimente + alerte de expirare |
| **Rețete** | Creare rețete, calcul calorii, listă de cumpărături |
| **Ingrediente** | Bază de date de alimente (valori per 100g) |

La pornire se încarcă automat date demo (un utilizator, ingrediente, rețete, stoc), ca aplicația să fie funcțională imediat.

---

## 🏗️ Arhitectură (pe straturi)

```
com.bitewise
├── domain        — entitățile JPA (User, Recipe, Ingredient, ...)
├── repository    — interfețe Spring Data JPA
├── service       — logica de business (BMI, calorii, includes pe cămară)
├── controller    — REST controllers (/api/...)
├── dto           — obiecte de transfer (NutritionSummary, BodyMetrics, ...)
├── exception     — excepții custom + handler global
└── config        — DataSeeder (date demo)
```

Frontend-ul (în `src/main/resources/static/`) este un SPA simplu în vanilla JS care consumă API-ul REST.

---

## 🧪 API REST (exemple)

```
GET  /api/users/{id}/metrics              → BMI, BMR, TDEE
GET  /api/users/{id}/meals/progress?date= → progres caloric pe o zi
POST /api/users/{id}/meals                → înregistrează masă (scade din cămară)
GET  /api/users/{id}/pantry/expiring?days=→ alimente ce expiră
GET  /api/recipes/{id}/nutrition          → calorii totale rețetă
GET  /api/recipes/{id}/shopping-list?userId= → listă de cumpărături
```

Consola H2 (pentru inspecția bazei) e disponibilă local la `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:bitewise`, user `sa`, fără parolă).
