# Komponentenbeschreibung (Serverkomponenten)

## Nachrichten Verteiler

* ist für den Verlauf zwischen „Abschicken einer Nachricht“ und „Erhalten der Nachricht beim Gegenüber“ verantwortlich.
* Arbeitet in unserem Beispiel auf den Chat(-Verlauf) Daten

## Account Manager

* verwaltet alle Änderungen, die an Profil, Kontaktliste oder anderen benutzerspezifischen Daten getätigt werden
* Arbeitet in unserem Beispiel auf den Profildaten, den Kontakten des Users, den Einstellungen, der Sperrliste des Users und der User-Base (zu viel?)

## Authentifizierung

* ist für die Anmeldefunktion (des Messengers) verantwortlich und verwaltet diese
* Arbeitet in unserem Beispiel auf den User-Base Daten

## Statische Inhalte

* Verwaltet alle Dateien (die nicht lokal gespeichert sind)
* Arbeitet in unserem Beispiel auf den Mediendaten

## Web-Server

* stellt Inhalte für das Internet parat. Über einen Webbrowser können diese dann angezeigt werden

## Verschlüsselung

* Verschlüsselt alle über das Internet laufenden Daten mit einem Verschlüsselungsalgorithmus
* Arbeitet in unserem Beispiel auf der User-Base

## Verbindungen (Load-Balancer)

* Verteilt die Verbindungen bestmöglichst auf die verfügbaren Webserver
* optimiert die Ressourcennutzung



# Komponenten, welche vermutlich mehrfach verwendet werden können:

* Statische Inhalte, kann durch den Load-Balancer parallelisiert werden 
* Webserver, kann durch den Load-Balancer parallelisiert werden

Die Folgenden Server können nur durch zusätzlichen Aufwand parallelisert werden:

* Authentifizierungsserver
* Account-Manager
* Nachrichten-Verteiler (sollte parallelisert werden, da wahrscheinlich höchste Last)

# Komponenten, welche vermutlich NICHT mehrfach verwendet werden können:

* Verschlüsselung (nur wenn vor dem Load-Balancer) ?!
* Load-Balancer, da dieser der zentrale Knoten für Anfragen ist






