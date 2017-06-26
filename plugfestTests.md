# Fehler beim Plugfest testen
## Gruppe 04
### Tests FE(4) -> BE(4)
* Uhrzeit teilweise schlecht dargestellt
* Bei Nachricht senden wird keine Uhrzeit angezeigt
* Bei Chat-Wechsel verschwinden die alten Chats
* Man kann sich ohne Daten einloggen
* Man kann ein leeres Passwort eingeben und/oder leeren Username angeben
* Logout fehlt
* Man kann sich mit einem Account mit unterschiedlichen Pseudonymen einloggen aber nur der vom Register ist gültig
* Die Oberen Buttons machen aktuell noch gar nichts
* Jeder wird als Online angezeigt
* Schon beim Öffnen eines Chats wechselt dieser in Aktive Chats nicht erst nach dem Senden einer Nachricht
* CORS Fehler bei /profile
* CORS Fehler bei /messages
* kein Responsive Design bzw. schlechtes
* Fehler bei Login Register als Alerts -> unschön
* Remember Me funktioniert nicht richtig
* Passwort vergessen angeboten, nicht vorhanden

### Tests FE(4) -> BE(3)
* Mit einer anderen IP geht garnichts mehr da dort das Javascript fehlt somit keine Tests möglich sind
* 

### Tests FE(3) -> BE(4)
* CORS Error für Login



## Gruppe 05
### Tests FE(5) -> BE(5)
* Nachricht bleibt im Eingabefeld nach dem Absenden
* Zeit wird Nutzer unfreundlich angezeigt
* Server IP MUSS eingegeben werden, keinen Standardwert
* Kein Logout möglich 
* Handymode scrollt nicht nach  unten
* Nachrichten von neuen Kontakten werden nicht angezeigt
* Beim Einloggen kommt ein komischer Test-Chat
* HTML/JS Injection möglich
* Enter Funktionen fehlen überall -> Nutzer unfreundlich
* Registrierte Accounts können nicht chatten

### Tests FE(5) -> BE(3)
* alles funktioniert

### Tests FE(3) -> BE(5)
* Unothaurized für /profile
* immer 200 bei /messages statt 204(da es keine messages gibt)
* Not Acceptable bei Registrierung statt Im a Teapot
