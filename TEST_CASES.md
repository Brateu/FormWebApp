TEST_CASES — User Service

--Registracija--
--------------------------------------------------------------------------------------------------------------------------
Sistem: FormApp (User-Service @ http://localhost:8080/api/user/register),
Okruženje: Docker lokalno (Postgres 15, User-Service 8080)
---------------------------------------------------------------------------------------------------------------------------
TestCase - Scenario - Očekivano - Dobijeno - {kod}

**1)** TC01 - Valid Register (valid email, password, fullname) - 200 - 200 -
**{ "email": "qa_user3@mail.com", "password": "Passw0rd!", "fullName": "QA User Three" }**

**2)** TC02 - Case-insensitive Email - 200 - 200 - 
**{ "email": "QA_USER4@MAIL.COM", "password": "Passw0rd!", "fullName": "QA User 4" }**

**3)** TC03 - Plus-Alias Email - 200 - 200 -
**{ "email": "qa.user+alias@mail.com", "password": "Passw0rd!", "fullName": "QA User Alias" }**

**4)** TC04 - Blank Email - 400 - **200** -
**{ "email": "", "password": "Passw0rd!", "fullName": "X" }**

**5)** TC05 - Email Already Exist - 400 - 400 -
**{ "email": "qa_user3@mail.com", "password": "Passw0rd!", "fullName": "QA User Three" }**

**6)** TC06 - Default @ in Email - 400 - **200** - 
**{ "email": "not-an-email", "password": "Passw0rd!", "fullName": "X" }**

**7)** TC07 - Len(password) < 8 - 401 - 401 - 
**{ "email": "qa_p1@mail.com", "password": "Ab1!a", "fullName": "X" }**

**8)** TC08 - Password without number - 400 - **200** - 
{ "email": "qa_p2@mail.com", "password": "Abcdefg!", "fullName": "X" }

**9)** TC09 - Password Without Uppercase Letter - 400 - **200** - 
**{ "email": "qa_p3@mail.com", "password": "abcde1!a", "fullName": "X" }**

**10)** TC10 - Password Without Lowercase Letter - 400 - **200** - 
**{ "email": "qa_p4@mail.com", "password": "ABCDE1!A", "fullName": "X" }**

**11)** TC11 - Password Without Special Sign - 400 - **200** - 
**{ "email": "qa_p5@mail.com", "password": "Abcdefg1", "fullName": "X" }**

**12)** TC12 - Password Only Spaces - 400 - **200** - 
**{ "email": "qa_p6@mail.com", "password": "        ", "fullName": "X" }**

**13)** TC13 - Password Full Special Signs - 200 - 200 - 
**{ "email": "qa_p8@mail.com", "password": "Ab1!(){}[]:;',?/*~$^+=<>.", "fullName": "X" }**

**14)** TC14 - Empty Fullname - 400 - **200** - 
**{ "email": "qa_fn1@mail.com", "password": "Passw0rd!", "fullName": "" }**

**15)** TC15 - Fullname Minimum Len - 200 - 200 - 
**{ "email": "qa_fn3@mail.com", "password": "Passw0rd!", "fullName": "X" }**

**16)** TC16 - Ignore Additional Field - 200 - 200 - 
**{ "email": "qa_edge1@mail.com", "password": "Passw0rd!", "fullName": "Edge Case", "role": "ADMIN" }**

**17)** TC17 - Empty Body - 500 - 500 - 
**{}**

--------------------------------------------------------------------------------------------------------------------------
--Prijava--
--------------------------------------------------------------------------------------------------------------------------
Sistem: FormApp (User-Service @ http://localhost:8080/api/user/login),
Okruženje: Docker lokalno (Postgres 15, User-Service 8080)
---------------------------------------------------------------------------------------------------------------------------
TestCase - Scenario - Očekivano - Dobijeno - {kod}

**1)** TC01 - Valid Login (valid email, password) - 200 - 200 -
**{ "email": "qa_user3@mail.com", "password": "Passw0rd!" }**

**2)** TC02 - Email Case-Insensitive - 200 - **400** -
**{ "email": "QA_USER3@MAIL.COM", "password": "Passw0rd!" }**

**3)** TC03 - Plus-Alias Email - 200 - **400** -
**{ "email": "qa_user3+alias@mail.com", "password": "Passw0rd!" }**

**4)** TC04 - Invalid Password - 400 - 400 -
**{ "email": "qa_user3@mail.com", "password": "WrongPass!" }**

**5)** TC05 - Unknown Email - 400 - 400 -
**{ "email": "nepoznat_user@mail.com", "password": "Passw0rd!" }**

**6)** TC06 - Middle Space Email - 400 - 400 - 
**{ "email": "qa user3@mail.com", "password": "Passw0rd!" }**

**7)** TC07 - Password with Tailer Space - 400 - 400 -
**{ "email": "qa_user3@mail.com", "password": "  Passw0rd!  " }**

**8)** TC08 - Email with Spaces Around - 400 - 400 -
**{ "email": "  qa_user3@mail.com  ", "password": "Passw0rd!" }**

**9)** TC09 - Empty Email - 400 - **200** - 
{ "email": "", "password": "Passw0rd!" }

**10)** TC10 - Empty Password - 400 - 400 - 
**{ "email": "qa_user3@mail.com", "password": "" }**

**11)** TC11 - Empty Email Empty Password - 400 - 400 -
**{ "email": "", "password": "" }**

**12)** TC12 - Null Field - 500 - 500 - 
**{ "email": "qa_user3@mail.com" }**

**13)** TC13 - Empty Body - 400 - 400 - 
**{}**

**14)** TC14 - Body Without Bracket - 500 - 500 -
**{ "email": "qa_user3@mail.com", "password": "Passw0rd!"**   

**15)** TC15 - Login - GET Instead of POST - 500 - 500 - 
**{ "email": "qa_user3@mail.com", "password": "Passw0rd!" }**

-------------------------------------------------------------------------------------------------------------------------

TEST_CASES — Form Service

--------------------------------------------------------------------------------------------------------------------------
Sistem: FormApp (Form-Service @ http://localhost:8090/api/forms/...), 
Okruženje: Docker lokalno (Postgres 15, Form-Service 8090)
--------------------------------------------------------------------------------------------------------------------------
TestCase - Scenario - Očekivano - Dobijeno - {kod}

**1)** TC01 - Create Form - 200 - 200 - 
**{ "name": "Test Form", "description": "Minimal body to create a form" }**

**2)** TC02 - Test Created Form - 200 - 200 - 
{}

**3)** TC03 - List of Created Forms - 200 - 200 - 
{}

**4)** TC04 - Updated Form - 200 - 200 - 
**{ "name": "Test Form Updated", "description": "Opis forme ažuriran za testiranje", "allowAnonymous": true, "responseLimit": 100 }**

**5)** TC05 - Updated Form Status - 200 - 200 - 
{} - PUT http://localhost:8090/api/forms/2/status?status=ACTIVE

**6)** TC06 - Updated Form Status - 200 - 200 - 
{} - PUT http://localhost:8090/api/forms/2/status?status=CLOSED	

**7)** TC07 - Locked Form - 200 - 200 - 
{} - http://localhost:8090/api/forms/1/lock - DRAFT -> ACTIVE

**8)** TC08 - Unlocked Form - 200 - 200 - 
{} - http://localhost:8090/api/forms/1/unlock

**9)** TC09 - Delete Form - 204 - 204 - 
{} 

**10)** TC10 - Get Forms for the Current User - 200 - 200 -
{}

**11)** TC11 - Update Form Visibility - 200 - 200 -
{} - http://localhost:8090/api/forms/2/visibility?visibility=PUBLIC

**12)** TC12 - Get Forms by Visibility - 200 - 200 - 
{} - http://localhost:8090/api/forms/visibility/PUBLIC

**13)** TC13 - Create a Copy of a Form - 200 - 200 -
{} - POST http://localhost:8090/api/forms/2/copy

**14)** TC14 - Get Forms by Status - 200 - 200 -
{} - http://localhost:8090/api/forms/status/CLOSED

**15)** TC15 - Get All Public Forms - 200 - 200 - 
{} - http://localhost:8090/api/forms/public

**16)** TC16 - Get a Specific Public Form - 200 - 200 - 
{} - http://localhost:8090/api/forms/public/2

-----------------------------------------------------------------------------------------------------------------------
--QUESTIONS--
-----------------------------------------------------------------------------------------------------------------------

**1)** TC01 - Create Short Text Question - 200 - 200 - 
**{ "text": "Vaše ime i prezime", "type": "SHORT_TEXT", "required": true, "orderIndex": 1 }**

**2)** TC02 - Create Long Text Question - 200 - 200 - 
**{"text": "Opišite svoje iskustvo", "type": "LONG_TEXT", "required": false, "orderIndex": 2 }**

**3)** TC03 - Create Single Choice Question - 200 - 200 - 
**{ "text": "Koji paket koristite?", "type": "SINGLE_CHOICE", "required": true, "orderIndex": 3, }**

**4)** TC04 - Create Multi Choice Question - 200 - 200 - 
**{ "text": "Odaberite dve stavke", "type": "MULTI_CHOICE", "required": false, "orderIndex": 4, }**

**5)** TC05 - Create Number Question - 200 - 200 - 
**{ "text": "Ocenite uslugu (1–5)", "type": "NUMBER", "required": true, "orderIndex": 5 }**

**6)** TC06 - Create Date Question - 200 - 200 - 
**{ "text": "Datum rođenja", "type": "DATE", "required": false, "orderIndex": 6 }**

**7)** TC07 - Create Time Question - 200 - 200 - 
**{ "text": "Vreme kontakta", "type": "TIME", "required": false, "orderIndex": 7 }**

**8)** TC08 - List of Question in Form - 200 - 200 - 
{} - http://localhost:8090/api/forms/2/questions

**9)** TC09 - Get a Specific Question - 200 - 200 - 
{} - http://localhost:8090/api/forms/2/questions/7

**10)** TC10 - Update a Question - 200 - 200 -
**{ "text": "Vaše puno ime", "type": "SHORT_TEXT", "required": true, "orderIndex": 1 }**

**11)** TC11 - Delete a Question - 204 - 204 - 
{} - http://localhost:8090/api/forms/2/questions/11

**12)** TC12 - Reordered Questions - 200 - 200 - 
**[6, 9, 3, 7, 10, 4, 8]**

**13)** TC13 - Clone Question - 200 - **500** - 
{} - http://localhost:8090/api/forms/2/questions/4/clone

---------------------------------------------------------------------------------------------------------------------
--OPTIONS--
----------------------------------------------------------------------------------------------------------------------

**1)** TC01 - Create a New Option for Single Choice 1 - 200 - 200 - 
**{ "text": "Basic" }**

**2)** TC02 - Create a New Option for Single Choice 2 - 200 - 200 - 
**{ "text": "Pro" }**

**3)** TC03 - Create a New Option for Question with Image - 200 - 200 -
**{ "text": "BasicPro", "imageUrl": "https://sh.wikipedia.org/wiki/Slika#/media/Datoteka:Mona_Lisa,_by_Leonardo_da_Vinci,_from_C2RMF_retouched.jpg" }**

**4)** TC04 - Updated an Option - Text - 200 - 200 -
**{ "text": "BasicProPlus" }**

**5)** TC05 - Updated an Option - Image - 200 - 200 -
**{ "imageUrl": "https://sr.wikipedia.org/wiki/%D0%A1%D0%BB%D0%B8%D0%BA%D0%B0#/media/%D0%94%D0%B0%D1%82%D0%BE%D1%82%D0%B5%D0%BA%D0%B0:Andrea_Mantegna_062.jpg" }**

**6)** TC06 - Updated an Options - Text and Image - 200 - 200 -
**{ "text": "UpdatedText", "imageUrl": "https://sr.wikipedia.org/wiki/%D0%A1%D0%BB%D0%B8%D0%BA%D0%B0#/media/%D0%94%D0%B0%D1%82%D0%BE%D1%82%D0%B5%D0%BA%D0%B0:Vincent_Willem_van_Gogh_107.jpg" }**

**7)** TC07 - Delete an Option - 204 - 204 -
{} - http://localhost:8090/api/forms/2/questions/6/options/3

**8)** TC08 - List of Options - Single Choice - 200 - 200 - 
{} - http://localhost:8090/api/forms/2/questions/6/options

**9)** TC09 - Create a New Option for Multi Choice 1 - 200 - 200 - 
**{ "text": "Brzina", "imageUrl": "https://sh.wikipedia.org/wiki/Krik_%28slika%29#/media/Datoteka:The_Scream.jpg" }**

**10)** TC10 - Create a New Option for Multi Choice 2 - 200 - 200 - 
**{ "text": "Vektor" }**

**11)** TC11 - List of Options - Multi Choice - 200 - 200 - 
{} - http://localhost:8090/api/forms/2/questions/7/options

-----------------------------------------------------------------------------------------------------------------------
--COLLABORATOR--
-----------------------------------------------------------------------------------------------------------------------

**1)** TC01 - Collaborator Editor - 200 - 200 - 
**{ "userId": 21, "role": "EDITOR" }**

**2)** TC02 - Collaborator Change (Editor -> Viewer) - 200 - 200 - 
**{ "role": "VIEWER" }**

**3)** TC03 - Collaborator Viewer - 200 - 200 - 
**{ "userId": 22, "role": "VIEWER" }**

**4)** TC04 - Collaborator Change (Viewer -> Editor) - 200 - 200 - 
**{ "role": "EDITOR" }**

**5)** TC05 - List of Collaborators - 200 - 200 - 
{} - http://localhost:8090/api/forms/2/collaborators

**6)** TC06 - Delete Collaborator - 204 - 204 -
{} 
