TEST_CASES — User Service / Registracija

Sistem: FormApp (User-Service @ http://localhost:8070)
Okruženje: Docker lokalno (Postgres 15, User-Service 8070)

Sažetak nalaza (Bug Summary)
ID	Naslov	Opis	Očekivano	Stvarno	Status	Težina
BUG-REG-001	Nevalidan email prolazi	API prihvata email bez validnog formata (npr. not-an-email)	400 Bad Request	200 OK / 201 Created	FAIL	High
BUG-REG-002	Duplikat email-a prolazi	Ponovna registracija istog email i username prolazi	409 Conflict (ili 400)	200 OK / 201 Created	FAIL	Critical
BUG-REG-003	Case-insensitive problem	USER@mail.com i user@mail.com tretirani kao različiti	Normalizacija i blokiranje duplikata	200/201	FAIL	High
BUG-REG-004	Razmaci u email-u prolaze	" user @mail.com " prolazi bez trim/validacije	Trim + 400 Bad Request	200/201	FAIL	High

Napomena dev timu: Predlaže se @Email + @NotBlank, trim() + toLowerCase() pre upisa, i UNIQUE ograničenje u bazi nad email (posle normalizacije). Za duplikat vraćati 409.


Detaljni test slučajevi:

TC-REG-001 — Registracija (validan slučaj)

URL: POST /users/register

Headers: Content-Type: application/json

Body:

{
  "username": "qa_user1",
  "password": "Passw0rd!",
  "email": "qa_user1@mail.com",
  "fullName": "QA User One"
}

Očekivano: 201 Created (ili 200 OK), telo sadrži podatke o korisniku ili potvrdu.

Stvarno: 200 OK (korisnik kreiran) — PASS.



TC-REG-002 — Registracija (nevalidan email)

URL: POST /users/register

Body:

{
  "username": "qa_user_invalid_email",
  "password": "Passw0rd!",
  "email": "not-an-email",
  "fullName": "QA Invalid"
}

Očekivano: 400 Bad Request.

Stvarno: 200 OK — FAIL (nema validacije formata email-a).



TC-REG-003 — Registracija (duplikat korisnika)

Preuslov: Izvršen TC-REG-001 (korisnik postoji).

URL: POST /users/register

Body:

{
  "username": "qa_user1",
  "password": "Passw0rd!",
  "email": "qa_user1@mail.com",
  "fullName": "QA User One"
}

Očekivano: 409 Conflict (ili 400) + poruka da korisnik već postoji.

Stvarno: 200 OK / 201 Created — FAIL (dozvoljen duplikat).



TC-REG-004 — Registracija (email UPPERCASE)

Preuslov: Postoji korisnik sa email = qa_user1@mail.com (TC-REG-001).

URL: POST /users/register

Body:

{
  "username": "qa_user_upper",
  "password": "Passw0rd!",
  "email": "QA_USER1@MAIL.COM",
  "fullName": "QA Upper"
}

Očekivano: 409/400 (email normalizovan na lowercase i detektovan duplikat).

Stvarno: 200/201 — FAIL (case-sensitive tretman email-a).



TC-REG-005 — Registracija (razmaci u email-u)

URL: POST /users/register

Body:

{
  "username": "qa_spaces",
  "password": "Passw0rd!",
  "email": "  qa_user1 @mail.com  ",
  "fullName": "QA Spaces"
}

Očekivano: 400 Bad Request (trim + format validacija).

Stvarno: 200/201 — FAIL (nema trim/validacije).