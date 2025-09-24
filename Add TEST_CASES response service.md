TEST_CASES — User Service

--REGISTER--
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
--LOGIN--
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

-------------------------------------------------------------------------------------------------------------------------

TEST_CASES — Response Service

--------------------------------------------------------------------------------------------------------------------------
Sistem: FormApp (Response-Service @ http://localhost:8060/api/responses/...), 
--------------------------------------------------------------------------------------------------------------------------
TestCase - Scenario - Očekivano - Dobijeno - {kod}

**1)** TC01 - Create (SUBMITTED) - 201 - 201 - 
**{
  "formId": 4,
  "answeredQuestions": [
    { "questionId": "12", "type": "TEXT",   "value": "Pera Perić" },
    { "questionId": "13", "type": "CHOICE", "value": "8"          },
    { "questionId": "14", "type": "NUMBER", "value": 5            }
  ],
  "questionDefinitions": [
    {
      "id": "12",
      "text": "Vaše puno ime",
      "type": "TEXT",
      "required": true,
      "options": [],
      "validationRules": {}
    },
    {
      "id": "13",
      "text": "Koji paket koristite?",
      "type": "CHOICE",
      "required": true,
      "options": [
        { "id": "8", "text": "Basic" },
        { "id": "9", "text": "Pro" }
      ],
      "validationRules": {}
    },
    {
      "id": "14",
      "text": "Ocenite uslugu (1–5)",
      "type": "NUMBER",
      "required": true,
      "options": [],
      "validationRules": {}
    }
  ]
}**

**2)** TC02 - Get by ID - 200 - 200 - http://localhost:8060/api/responses/68d323377c967db7497c4f63

**3)** TC03 - Get List of All Responses - 200 - 200 - http://localhost:8060/api/responses/form/4

**4)** TC04 - Get Response by User ID - 200 - 200 - http://localhost:8060/api/responses/user/23

**5)** TC05 - Create DRAFT - 201 - 201 - 
**{
  "formId": 4,
  "userId": 23,
  "status": "DRAFT",
  "answeredQuestions": [
    { "questionId": "12", "type": "TEXT",   "value": "Neko Ime" },
    { "questionId": "13", "type": "CHOICE", "value": "9" }
  ],
  "questionDefinitions": [
    {
      "id": "12",
      "text": "Vaše puno ime",
      "type": "TEXT",
      "required": true,
      "options": [],
      "validationRules": {}
    },
    {
      "id": "13",
      "text": "Koji paket koristite?",
      "type": "CHOICE",
      "required": true,
      "options": [
        { "id": "8", "text": "Basic" },
        { "id": "9", "text": "Pro" }
      ],
      "validationRules": {}
    }
  ]
}**

**6)** TC06 - Update DRAFT - 200 - 200 - 
**{
  "formId": 4,
  "status": "DRAFT",
  "answeredQuestions": [
    { "questionId": "12", "type": "TEXT",   "value": "Petar Petrović" },
    { "questionId": "13", "type": "CHOICE", "value": "9" },
    { "questionId": "14", "type": "NUMBER", "value": 4 }
  ],
  "questionDefinitions": [
    {
      "id": "12",
      "text": "Vaše puno ime",
      "type": "TEXT",
      "required": true,
      "options": [],
      "validationRules": {}
    },
    {
      "id": "13",
      "text": "Koji paket koristite?",
      "type": "CHOICE",
      "required": true,
      "options": [
        { "id": "8", "text": "Basic" },
        { "id": "9", "text": "Pro" }
      ],
      "validationRules": {}
    },
    {
      "id": "14",
      "text": "Ocenite uslugu (1–5)",
      "type": "NUMBER",
      "required": true,
      "options": [],
      "validationRules": {}
    }
  ]
}**

**7)** TC07 - Delete Response - 204 - 204 - http://localhost:8060/api/responses/68d328647c967db7497c4f67

**8)** TC08 - Filtriranje po formi - 200 - 200 - http://localhost:8060/api/responses/form/4?page=0&size=20&sort=submittedAt&direction=DESC

**9)** TC09 - Filtriranje po statusu - 200 - 200 - http://localhost:8060/api/responses/form/4?status=SUBMITTED&page=0&size=10

**10)** TC10 - Filtriranje po datumu- 200 - 200 - http://localhost:8060/api/responses/form/4?startDate=2025-08-01T00:00:00&endDate=2025-09-23T23:59:59

**11)** TC11 - Filtriranje po korisniku - 200 - 200 - http://localhost:8060/api/responses/user/23?page=0&size=20

**12)** TC12 - Filtriranje po odgovoru - 200 - 200 - http://localhost:8060/api/responses/search?formId=4&questionId=12&answer=Pera&page=0&size=20

**13)** TC13 - Load DRAFT - 200 - 200 - http://localhost:8060/api/responses/draft?formId=4&userId=23

**14)** TC14 - Submit DRAFT - 200 - 200 - http://localhost:8060/api/responses/68d327ad7c967db7497c4f66/submit

**15)** TC15 - Export CSV - 200 - 200 - http://localhost:8060/api/responses/export/csv?formId=4

**16)** TC16 - Import CSV - 200 - 200 - http://localhost:8060/api/responses/import/csv?formId=4

**17)** TC17 - Raspodela kroz vreme - 200 - 200 - http://localhost:8060/api/analytics/time-series/4

**18)** TC18 - Statistic of Form - 200 - 200 - http://localhost:8060/api/analytics/statistics/4

**19)** TC19 - Completion rate - 200 - 200 - http://localhost:8060/api/analytics/completion-rate/4

**20)** TC20 - Average Response Time - 200 - 200 - http://localhost:8060/api/analytics/response-time/4

**21)** TC21 - Create SUBMITTED - empty answers - 400 - 400 - http://localhost:8060/api/responses

**22)** TC22 - Offline Validation without Definition - 400 - 400 - http://localhost:8060/api/responses

**23)** TC23 - Create SUBMITTED with Invalid Question ID - 400 - 400 -

**{
  "formId": 4,
  "answeredQuestions": [
    { "questionId": "12", "type": "TEXT",   "value": "Pera Perić" },
    { "questionId": "13", "type": "CHOICE", "value": "8"          },
    { "questionId": "14", "type": "NUMBER", "value": 5            }
  ],
  "questionDefinitions": [
    {
      "id": "999",
      "text": "Vaše puno ime",
      "type": "TEXT",
      "required": true,
      "options": [],
      "validationRules": {}
    },
    {
      "id": "13",
      "text": "Koji paket koristite?",
      "type": "CHOICE",
      "required": true,
      "options": [
        { "id": "8", "text": "Basic" },
        { "id": "9", "text": "Pro" }
      ],
      "validationRules": {}
    },
    {
      "id": "14",
      "text": "Ocenite uslugu (1–5)",
      "type": "NUMBER",
      "required": true,
      "options": [],
      "validationRules": {}
    }
  ]
}**

**24)** TC24 - Create SUBMITTED - Single Choice with More Options - 400 - 400 -
**{
  "formId": 4,
  "status": "SUBMITTED",
  "answeredQuestions": [
    { "questionId": "13", "type": "SINGLE_CHOICE", "value": ["8","9"] }
  ],
  "questionDefinitions": [
    { "id": "13", "type": "CHOICE", "required": true,
      "options": [{ "id":"8" }, { "id":"9" }] }
  ]
}**

**25)** TC25 - Create SUBMITTED with Wrong Form ID - 400 - **201** -
**{
  "formId": 90,
  "answeredQuestions": [
    { "questionId": "12", "type": "TEXT",   "value": "Pera Perić" },
    { "questionId": "13", "type": "CHOICE", "value": "8"          },
    { "questionId": "14", "type": "NUMBER", "value": 5            }
  ],
  "questionDefinitions": [
    {
      "id": "12",
      "text": "Vaše puno ime",
      "type": "TEXT",
      "required": true,
      "options": [],
      "validationRules": {}
    },
    {
      "id": "13",
      "text": "Koji paket koristite?",
      "type": "CHOICE",
      "required": true,
      "options": [
        { "id": "8", "text": "Basic" },
        { "id": "9", "text": "Pro" }
      ],
      "validationRules": {}
    },
    {
      "id": "14",
      "text": "Ocenite uslugu (1–5)",
      "type": "NUMBER",
      "required": true,
      "options": [],
      "validationRules": {}
    }
  ]
}**


**26)** TC26 - Multi Choice with Invalid Option - 400 - 400 -

**{
  "formId": 4,
  "status": "SUBMITTED",
  "answeredQuestions": [
    { "questionId": "15", "type": "MULTI_CHOICE", "value": ["999"] }
  ],
  "questionDefinitions": [
    {
      "id": "15",
      "type": "MULTI_CHOICE",
      "required": true,
      "options": [
        { "id": "6", "text": "Brzina" },
        { "id": "7", "text": "Vektor" }
      ]
    }
  ]
}**


**27)** TC27 - Number with String - 400 - 400 - 
**{
  "formId": 4,
  "status": "SUBMITTED",
  "answeredQuestions": [
    { "questionId": "14", "type": "NUMBER", "value": "abc" }
  ],
  "questionDefinitions": [
    { "id": "14", "type": "NUMBER", "required": true }
  ]
}**

**28)** TC28 -  Wrong Date Format - 400 - 400 - 

**{
  "formId": 4,
  "status": "SUBMITTED",
  "answeredQuestions": [
    { "questionId": "16", "type": "DATE", "value": "23-09-2025" },
    { "questionId": "17", "type": "TIME", "value": "25:99" }
  ],
  "questionDefinitions": [
    { "id": "16", "type": "DATE", "required": false },
    { "id": "17", "type": "TIME", "required": false }
  ]
}**

**29)** TC29 - Draft without Form ID - 400 - **201** - 
**{
  "status": "DRAFT",
  "userId": 23
}**


**30)** TC30 - Update Invalid Response ID - 404 - 404 - 

http://localhost:8060/api/responses/90

**{
  "formId": 4,
  "status": "DRAFT"
}** 

**31)** TC31 - Submit Response with Invalid ID - 404 - 404 - http://localhost:8060/api/responses/17/submit

**32)** TC32 - Delete Invalid Response ID - 404 - **201** - http://localhost:8060/api/responses/28

**33)** TC33 - Invalid Page - 404 - 404 - http://localhost:8060/api/responses/form/4?page=-1&size=0

**34)** TC34 - Invalid Size - 404 - 404 - http://localhost:8060/api/responses/form/4?page=15&size=-1

**35)** TC35 - Invalid Date Range - 400 - 400 - http://localhost:8060/api/responses/form/4?startDate=2025-09-30T00:00:00&endDate=2025-08-01T00:00:00

**36)** TC36 - Search without Answer - 400 - 400 - http://localhost:8060/api/responses/search?formId=4&questionId=12

**37)** TC37 - Time-series without Date - 400 - 400 - http://localhost:8060/api/analytics/time-series/4

