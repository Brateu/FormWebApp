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
