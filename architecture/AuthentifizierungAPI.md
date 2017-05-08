# Schnittstellen für die Authentifizierung

  ## Login eines Benutzers
  
  Loginanfragen werden bei der URL `/login` per `POST` als JSON-Dokument (Mime-Type: `application/json`) abgegeben. 
  Eine beispielhafte Nachricht sieht wie folgt aus:

```json
{
  "username": "bob",
  "password" : "halloIchbinBob"
}
```
Die SessioID ist gemäß [Base64](https://de.wikipedia.org/wiki/Base64) formatiert.

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

Ist die SessionID nicht korrekt formatiert, sendet der Server den Statuscode 401. Zusätzliche Felder werden aber ignoriert, damit man später das Protokoll einfacher erweitern kann.

Ist die SessionID korrekt formatiert, sendet der Server den Status 200 . 

## Profilanfragen
  
  Profilanfragen werden bei der URL `/profile` per `POST` als JSON-Dokument (Mime-Type: `application/json`) abgegeben. 
  Eine beispielhafte Nachricht sieht wie folgt aus:

```json
{
  "sessionid": "test123",
  "getprofile" : "susi"
}
```

Sind die Daten nicht korrekt formatiert, sendet der Server den Statuscode 401. Zusätzliche Felder werden aber ignoriert, damit man später das Protokoll einfacher erweitern kann.

Sind die Daten korrekt formatiert, sendet der Server den Status 200 und ein Antwort-JSON mit allen Daten des geforderten Profils. Z.B.:

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




