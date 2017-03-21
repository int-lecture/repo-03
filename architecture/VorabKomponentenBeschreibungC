# Komponentenbeschreibung (Serverkomponenten)

## Nachrichten Verteiler

* ist für den Verlauf zwischen „Abschicken einer Nachricht“ und „Erhalten der Nachricht beim Gegenüber“ verantwortlich.
* Verarbeitet erhaltene Daten (Strings, Bilder, Videos …)
* Arbeitet in unserem Beispiel auf den Chat(-Verlauf) Daten

## Account Manager

* ist für die Verwaltung der User-Base verantwortlich
* Arbeitet in unserem Beispiel auf den Profildaten, den Kontakten des Users, den Einstellungen, der Sperrliste des Users und der User-Base (zu viel?)

## Authentifizierung

* ist für die Anmeldefunktion (des Messengers) verantwortlich und verwaltet die Anmeldungen
* Arbeitet in unserem Beispiel auf der User-Base

## Statische Inhalte (Web-Server)

* stellt Inhalte für das Internet parat. Über einen Webbrowser können diese dann angezeigt werden
* Arbeitet in unserem Beispiel auf den Mediendaten

## Verschlüsselung

* Verschlüsselt bestimmte Daten mit einem Verschlüsselungsalgorithmus
* sorgt dafür, dass Inhalte nur von berechtigten Personen eingesehen werden können (Profildaten, Nachrichten usw.…)
* Arbeitet in unserem Beispiel auf der User-Base



# Komponenten, welche vermutlich mehrfach verwendet werden können:
* Authentifizierungsserver, da dieser lediglich auf die Profildaten zugreifen muss und diese nicht verändert
* Nachrichten-Verteiler, da dieser lediglich die empfangenden Daten an gewisse Nutzer zustellen muss und dies auch parallelisiert ablaufen kann

# Komponenten, welche vermutlich NICHT mehrfach verwendet werden können:
* Account Manager, da hier Änderungen an Datensätzen vorgenommen werden und es zu Problemen kommen kann
* Web-Server, da nur ein Server die Webseite darstellt
* Verschlüsselung, da die Verschlüsselung im Allgemeinen nicht auf verschiedene Server aufgeteilt werden kann



