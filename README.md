# Intelligens Elosztott Rendszerek - HF specifikáció

## Okos közúti kereszteződés okos járművekkel

A házi feladat témája egy okos közúti kereszteződés fejlesztése, amely lehetővé teszi a gyalogosok és intelligens gépjárművek számára a kereszteződésen való szervezett, gyors és biztonságos áthaladást.

Ágensek:
* gyalogosok (emberi ágensek)
* sofőrök (emberi ágensek)
* intelligens gépjárművek ágensei (fedélzeti egység)
* autók közlekedési lámpáinak vezérlői
* a kereszteződés központi vezérlő és kommunikáló egysége
* gyalogos lámpák vezérlői
* világítás vezérlő

Szimulált szenzorok:
* autó számláló szenzorok, amelyek figyelik, hogy az egyes sávokban hány autó várakozik és mennyi ideje
* gyalogos számláló szenzorok, amelyek figyelik, hogy az egyes gyalogos átkelőknél hány ember várakozik és mennyi ideje
* gépjárművekkel való kommunikációt lehetővé tevő szenzorok (pl. rádióvevő)

Missziókritikus esemény:
* egy riasztott mentő-/tűzoltó-/rendőrautó közeledik a forgalmas kereszteződés felé és biztosítani kell számára a gyors és biztonságos áthaladást
