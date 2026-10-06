# CRUD fix: what changed

All five existing modules now save to MySQL and support Create, Read, Update and Delete.
Every page shows the real server error message if a save fails (no more fake "SUCCESS" alerts).

## Main bugs that were fixed
- **pharmacy.html had a JavaScript syntax error** (missing `}` in `submitAddMedicine`), so none of the page's JavaScript ran.
- **Doctors and Appointments pages were fake**: lists were hard-coded in the HTML, and Edit, Cancel and Reschedule only showed alerts.
- **Register Doctor sent the wrong field names** (`dept`, `qual`, `room` and so on), so nothing was saved, but the page said it was.
- **Pharmacy dispense called a URL that does not exist** (`/dispensing/{id}`). The correct URL is `/prescriptions/{id}/dispense`.
- **Dispensing always failed with "0 units available"** because adding a stock batch never increased the medicine quantity.
- **Blood bank numbers used `count() + 1`**, which creates a duplicate number (and a crash) after any delete.
- **Scheduling time fields were free text**. Typing `8:00` instead of `08:00` crashed the server. They are now time pickers and are validated on the server.
- **Scheduling availability was hard-coded to doctor #101**, and every form asked you to type a doctor ID by hand.
- **The database was empty on a fresh install** because `data.sql` never runs. `DemoDataSeeder` now adds departments, doctors (IDs 101-108), and medicine categories and suppliers, but only when those tables are empty.

- **Pharmacy pop-up forms were never real pop-ups.** The page used the `appt-modal-overlay` class, but its CSS only existed inside doctors.html, so every form was always shown under the footer. The modal CSS now lives in `css/style.css`.
- **Pharmacy tables overflowed on phones**, which made the whole page scroll sideways and hid the Edit and Delete buttons. Tables now scroll inside their own box, and the form fields stack in one column on small screens.

## What each page can do now
| Page | Create | Read | Update | Delete |
|---|---|---|---|---|
| doctors.html | Register doctor | List from DB | Edit doctor | Delete (blocked if the doctor has appointments) |
| appointments.html | Book | List / tabs / stats | Reschedule, mark completed, cancel | Delete |
| pharmacy.html | Medicine, category, supplier, stock batch, prescription | All tables | All of them, plus prescription items | All of them (rows still in use are set to INACTIVE) |
| bloodbank.html | Donor, request, donation, test, issue | All tables | Donor, request | Donor, request |
| doctor-scheduling.html | Specialization, availability, schedule, shift, roster, leave | All tables | All of them | All of them |

Donations, test results, issues and the audit history are kept as permanent records (create and read only), which is normal for medical records.

## New and changed files
- `js/api.js`: shared `api()` helper used by every page (checks errors, escapes HTML)
- `backend/.../config/DemoDataSeeder.java`: starter data for an empty database
- New endpoints: `PUT`/`DELETE /api/doctors/{id}`, `PUT`/`DELETE /api/appointments/{id}`,
  `PUT /api/appointments/{id}/status`, `PUT`/`DELETE /api/pharmacy/stock/{id}`,
  `PUT`/`DELETE /api/bloodbank/requests/{id}`, `GET /api/doctor-scheduling/availability`,
  and `PUT` for scheduling specializations, availability, schedules, shifts, rosters and leave.
  `DELETE /api/doctor-scheduling/schedules/{id}` is also new.
- `GlobalExceptionHandler`: clear messages for duplicate values and invalid form data

## How to test
1. Start MySQL in XAMPP, then in IntelliJ click **Maven → Reload All Maven Projects**.
2. Run `MedicareHmsApplication` and open http://localhost:8080/doctors.html
3. On each page, try: Add → Edit → Delete. Changes must still be there after refreshing the page (F5).
4. Pharmacy flow: add a medicine → add a stock batch for it → create a prescription → Dispense.
   The medicine quantity should go down.

## Ward & Bed Management (merged from the separate HospitalManagementSystem project)
The team member's separate project (Spring Boot 4.1 + SQL Server) was merged into this project
(Spring Boot 3.2 + MySQL). Their design is kept: wards with an ID such as `WD-ICU`, a ward number and a type,
and beds with an ID, number, status and ward. The URLs `/api/wards` and `/api/beds` are unchanged.

- New and replaced files: `model/Ward.java` (replaces the old read-only ward), `model/Bed.java`,
  `repository/WardRepository.java`, `repository/BedRepository.java`, `service/WardService.java`,
  `service/BedService.java`, `controller/WardController.java` (now full CRUD) and `controller/BedController.java`.
- `wards.html` now uses this API. The old random demo beds are gone. Forms open as pop-ups instead of `prompt()` boxes.
- Added rules: a ward or bed ID that already exists is refused (before, saving silently overwrote it); ward numbers are unique;
  bed numbers are unique within a ward; status must be Available, Occupied, Reserved or Maintenance;
  a ward with beds cannot be deleted (now also checked on the server); an occupied bed cannot be deleted;
  an optional patient name is kept for occupied or reserved beds.
- Roles: Admin manages wards and beds, Staff manage beds, Doctors can view, and Patients cannot open the page.
- MySQL creates the new `ward` and `bed` tables automatically, and `DemoDataSeeder` adds 4 starter wards with beds.
  Data from the old SQL Server database is not copied over.
