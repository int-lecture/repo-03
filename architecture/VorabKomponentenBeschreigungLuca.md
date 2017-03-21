# Komponenten beschreibung

## Nachrichtenverteiler

  * sendet und empfängt alle nachrichten.
  * kommuniziert mit der Datenbank:Chat-verlauf.
  
## Account-Manager
  
  * kommt zum Einsatz wenn Profil, Kontakliste oder andere Benutzerspezifische Daten geändert werde.
  * kommuniziert mit den Datenbänken:Profile, Kontakte des Users, Einstellungen, Sperrliste des Users, User-Base.
  
## Verschlüsselung

  * benutzt, immer dann wenn etwas verschickt wird, um die Datensicherheit zu erhöhen.
  * kommuniziert mit der Datenbank:User-Base.
  
## Authentifizierung

  * Immer wenn sich jemand Einloggt.
  * kommuniziert mit der Datenbank:User-Base.
  
## Verbindungen

  * Für alles Art von Verbindungen zwischen Usern, Bsp.: schreibt...
  * Keine Datenbank notwendig.
  
## Statische Inhalte

  * Immer wenn auf Dateien(die nicht lokal gespeichert sind) zugegriffen wird oder diese versendet werden.
  * kommuniziert mit der Datenbank:Medien.
  
## Web-Server

  * Wird für alles was auf der Webseitenoberfläche gemacht wird benötigt. 
