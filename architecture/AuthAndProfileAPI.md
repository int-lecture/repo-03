# API für Authentifizierung und Profile

  ## Login eines Benutzers
  
  Loginanfragen werden bei der URL `/login` per `POST` als JSON-Dokument (Mime-Type: `application/json`) abgegeben. 
  Eine beispielhafte Nachricht sieht wie folgt aus:

```json
{
  "username": "bob",
  "password": "halloIchbinBob"
}
```
Die SessionID ist gemäß [Base64](https://de.wikipedia.org/wiki/Base64) formatiert.

Sind die Daten nicht korrekt formatiert, sendet der Server den Statuscode 401. Zusätzliche Felder werden aber ignoriert, damit man später das Protokoll einfacher erweitern kann.

Sind die Daten korrekt formatiert, sendet der Server den Status 200 und ein Antwort-JSON mit der SessionID in Base64-Format und dem Auslaufdatum. Z.B.:

```json
{
  "sessionid": "test123",
  "expire-date": "2017-03-30T17:00:00Z"
}
```
  ## Authentifizierung des Benutzers
  
  Authentifizierungsanfragen werden bei der URL `/auth` per `POST` als JSON-Dokument (Mime-Type: `application/json`) abgegeben. 
  Eine beispielhafte Nachricht sieht wie folgt aus:

```json
{
  "sessionid": "test123",
  "username": "bob"
}
```
Falls die Authentifizierung erfolgreich ist sendet der Server den Statuscode 200 und eine json response zurück.

```json
{
  "success": "true",
  "expire-date": "2017-03-30T17:00:00Z"
}
```
Ist die SessionID nicht korrekt formatiert oder abgelaufen, sendet der Server den Statuscode 401. 

## Profilanfragen
  
  Profilanfragen werden bei der URL `/profile` per `POST` als JSON-Dokument (Mime-Type: `application/json`) abgegeben. 
  Eine beispielhafte Nachricht sieht wie folgt aus:

```json
{
  "sessionid": "test123",
  "name": "bob",
  "getprofile": "susi"
}
```

Sind die Daten nicht korrekt formatiert, sendet der Server den Statuscode 401. Zusätzliche Felder werden aber ignoriert, damit man später das Protokoll einfacher erweitern kann.

Sind die Daten korrekt formatiert, sendet der Server den Status 200 und ein Antwort-JSON mit allen Daten des geforderten Profils.
Hier ist zu beachten das die Kontaktliste und die E-Mail nur zurückgegeben wenn der Nutzer eine Anfrage über sich selbst macht. z.B. :

```json
{
  "name": "susi",
  "email": "susiLiebtBob@web.de",
  "contact": ["bob" , "peter", "klaus"]
}
```

## Registrierungsanfragen
  
 Registrierungsanfragen werden bei der URL `/register` per `POST` als JSON-Dokument (Mime-Type: `application/json`) abgegeben. 
  Eine beispielhafte Nachricht sieht wie folgt aus:

```json
{
  "username": "bob",
  "password": "halloIchbinBob",
  "email": "bob@web.de"
}
```
Die SessioID ist gemäß [Base64](https://de.wikipedia.org/wiki/Base64) formatiert.

Sind die Daten nicht korrekt formatiert, sendet der Server den Statuscode 401. Zusätzliche Felder werden aber ignoriert, damit man später das Protokoll einfacher erweitern kann.

Sind die Daten korrekt formatiert, sendet der Server den Status 200 und ein Antwort-JSON mit dem Resultat. Z.B.:

```json
{
  "success": "true",
}
```

## Chat Server Änderungen

Die /send Methode des Chat Servers muss um die SessionID des Absenders ergänzt werden.

```json
{
  "sessionid": "1234acbd",
  "from": "bob",
  "to" : "jim",
  "date": "2017-03-30T17:00:00Z",
  "text": "Hallo Jim, wie geht es Dir?"  
}
```

Außerdem muss ein neuer response Typ 401 (Unauthorized) zurück gegeben werden falls die sessionid ungültig oder abgelaufen ist.

