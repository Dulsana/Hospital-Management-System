# Login fix: what changed

## Problem
- `login.html` accepted ANY email/password (it only waited 1.5s and redirected).
- `signup.html` saved nothing.
- The backend had no users table and no login API.
- `login.html`, `signup.html`, `appointments.html`, `departments.html` existed only in
  `backend/target/` (build output), so `mvn clean` / Rebuild deleted them.

## Changes
Backend (`backend/src/main/java/com/medicare/hms/`):
- `model/User.java` – new `users` table (Hibernate creates it automatically in MySQL)
- `repository/UserRepository.java`
- `dto/LoginRequest.java`, `dto/RegisterRequest.java`, `dto/AuthResponse.java`
- `service/AuthService.java` – passwords hashed with BCrypt
- `controller/AuthController.java` – `POST /api/auth/register`, `POST /api/auth/login`
- `config/DemoUserSeeder.java` – creates demo accounts on first start
- `pom.xml` – added `spring-security-crypto`; all `*.html` pages now packaged

Frontend:
- The 4 pages were moved to the project root; all links were updated
- `js/auth.js` – shared login helper
- `login.html` / `signup.html` now call the backend
- Fixed the "Invalid credentials" box that was visible as soon as the page opened

## How to run
1. Start **MySQL** in the XAMPP Control Panel. Apache is not needed.
2. Open the project in IntelliJ, then in the Maven panel click **Reload All Maven Projects**.
3. Run `MedicareHmsApplication`.
4. Open http://localhost:8080/login.html

## Demo accounts (created automatically)
| Role    | Email                 | Password    |
|---------|-----------------------|-------------|
| Admin   | admin@medicare.lk     | Admin@123   |
| Doctor  | dr.silva@medicare.lk  | Doctor@123  |
| Staff   | staff@medicare.lk     | Staff@123   |
| Patient | patient@gmail.com     | Patient@123 |

New patients can register on `signup.html`. Check phpMyAdmin → `medicare_hms_db` → `users`:
the password column only contains a BCrypt hash (`$2a$10$...`), never the real password.

## Not done yet
- Pages are not protected: you can still open `pharmacy.html` without logging in.
- There is no logout button yet. The `Auth.logout()` helper already exists in `js/auth.js`.
