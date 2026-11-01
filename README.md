# ClassMate

ClassMate is a small, browser-based classroom management app built with Java,
Spring Boot, and a single-page HTML/CSS/JavaScript interface. Teachers can
maintain a class list, track assignment submissions, take attendance, and save
reports. The Spring Boot backend stores the application's state in an embedded
H2 database so it persists across browser refreshes and application restarts.

## Features

- **Overview:** See the class size, fully completed records, pending
  submissions, and the latest saved attendance percentage for today.
- **Record Keeper:** Track student submissions against customizable columns.
  Add, rename, and remove columns; mark submissions individually or mark all
  students in a column; and filter students by submission status.
- **Submission summaries:** Generate a report of pending work, copy it, export
  it as CSV, print it or save it as PDF, and optionally save it to History.
- **Attendance:** Set a date and class/division, choose a roll-number range,
  mark students present or absent, and submit an attendance report. Unmarked
  students are confirmed as absent before submission.
- **Student List:** Add, edit, or remove students, or add multiple students by
  pasting one roll number and name per line.
- **History:** Browse saved attendance and submission reports, filter by date,
  view or edit saved attendance, and export or delete reports.
- **Settings:** Set the teacher name shown in the app.
- **Responsive interface:** Use the app in desktop and mobile browsers.

## Requirements

- JDK 25 or later. Maven compiles the project for Java 25; a newer JDK can
  compile and run that target.
- Apache Maven 3.8 or later. This project does not include a Maven Wrapper.
- A modern browser.

The project currently uses Spring Boot 3.3.5. Dependencies are resolved from
Maven Central on the first build, so an internet connection is needed then.
The interface also loads its Plus Jakarta Sans font from Google Fonts; if that
font is unavailable, the browser uses its local fallback fonts.

Check that Java is available:

```powershell
java -version
javac -version
```

Check Maven:

```powershell
mvn -version
```

## Run on Windows

Open PowerShell and change to the project directory:

```powershell
cd "C:\Users\amanr\OneDrive\Documents\classmate-java (1)\classmate-java"
```

If Maven is on your `PATH`, start the application with:

```powershell
mvn spring-boot:run
```

If you downloaded and extracted Maven but did not add it to your `PATH`, invoke
its executable directly. For example, with Maven 3.10.0 installed at the path
used during setup:

```powershell
& "C:\Users\amanr\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd" spring-boot:run
```

Keep that PowerShell window open while you use the app. When Spring Boot has
started successfully, open [http://localhost:8080](http://localhost:8080) in
your browser. Stop the application with **Ctrl+C** in the PowerShell window.

## Build and run the packaged application

From the project directory, create the executable JAR:

```powershell
mvn clean package
```

If Maven is not on `PATH`, replace `mvn` with the full `mvn.cmd` path shown
above. After the build succeeds, run the JAR:

```powershell
java -jar target\classmate-1.0.0.jar
```

Then open [http://localhost:8080](http://localhost:8080). Stop it with
**Ctrl+C**.

## Using the application

### Add students

Open **Student List** to add a student with a roll number and name, or select
**Add Many at Once** and enter one student per line. For example:

```text
1 Aarav Shah
2, Diya Patel
```

Roll numbers can contain letters, digits, and dashes, up to six characters.
Names are limited to 60 characters. Roll numbers must be unique.

The same class list is used by the Record Keeper and Attendance screens.
Removing a student also removes that student's submission ticks.

### Track submissions

In **Record Keeper**, create columns for work such as assignments, practicals,
or documents. Use the checkboxes to track work per student, or use **Mark all
submitted** in a column. The filters show all students, students with any
submissions, students with work still outstanding, or fully completed
students.

Select **Generate Summary** to see outstanding work. The report can be copied,
downloaded as CSV, printed or saved as PDF, and saved in History.

### Take attendance

In **Attendance**, choose the date, optionally enter a class or division, and
set the number of students and first and last roll numbers. Select a roll
number to cycle through present, absent, and not marked. **Mark All Present**
and **Reset Attendance** are also available.

Select **Submit Attendance** to create the report. If any roll numbers are
still unmarked, the app asks whether to count them as absent before finishing.
The report shows the totals, attendance percentage, and absent roll numbers.
Save it to History to keep it after leaving the screen. A saved attendance
report can be viewed and its marks edited later.

### Export reports

Reports can be copied to the clipboard or exported as CSV. The PDF action opens
the browser's print dialog; choose a **Save as PDF** printer/destination there
to create a PDF file.

## Data storage and backup

The app's complete state is stored in the H2 database at:

```text
data/classmate.mv.db
```

The database path is relative to the directory from which the application is
started. For the commands above, run the application from the project root so
it uses the project's `data` directory. The `data` directory is created as
needed. Avoid opening the same database with multiple running app instances:
H2 permits only one embedded application instance to own the database at a
time.

To back up your data, stop the application first, then copy the entire `data`
directory to a safe location. To restore it, stop the app and restore the
directory before starting the application again. **Do not delete or replace
the database file to resolve a lock error** unless you intend to discard the
saved data.

## API

The frontend and backend are served from the same origin. The application
exposes these state endpoints:

### `GET /api/state`

Returns the current saved application state as JSON. If no state has been
saved yet, it returns an empty JSON object.

### `PUT /api/state`

Replaces the saved application state. Send a JSON object with at least
`students` and `cols` properties and the `Content-Type: application/json`
header. A successful request returns `204 No Content`; malformed JSON or a
non-object body, or a body missing either required property, returns
`400 Bad Request`.

**Warning:** This endpoint replaces the entire state; it is not a partial
update. Back up the database before using it to restore or change data.

Example request in PowerShell:

```powershell
$state = @{
    teacher = "Teacher"
    students = @()
    cols = @(
        @{ id = "c1"; name = "Assignment 1" }
    )
    sub = @{}
    att = @{
        date = "2026-11-01"
        cls = ""
        start = 1
        end = 60
        marks = @{}
    }
    hist = @()
} | ConvertTo-Json -Depth 10

Invoke-RestMethod `
    -Uri "http://localhost:8080/api/state" `
    -Method Put `
    -ContentType "application/json" `
    -Body $state
```

This example replaces the current data with an empty state. Do not run it
against a database whose existing data you want to keep.

## Configuration

Default settings are in
[`src/main/resources/application.properties`](src/main/resources/application.properties):

| Setting | Default | Description |
| --- | --- | --- |
| `spring.datasource.url` | `jdbc:h2:file:./data/classmate` | File-backed H2 database location |
| `spring.datasource.username` | `sa` | H2 database username |
| `spring.datasource.password` | Empty | H2 database password |
| `spring.jpa.hibernate.ddl-auto` | `update` | Updates the database schema at startup |
| `server.port` | `8080` | HTTP port |

If port 8080 is already occupied, change `server.port` to an available port and
open the app at that port instead.

## Project layout

```text
.
├── pom.xml
├── data/
│   └── classmate.mv.db                 # Created at runtime; contains saved data
└── src/
    └── main/
        ├── java/com/classmate/
        │   ├── ClassMateApplication.java
        │   ├── AppState.java
        │   ├── AppStateRepository.java
        │   └── StateController.java
        └── resources/
            ├── application.properties
            └── static/index.html       # Browser interface
```

## Tests

Run the Maven test phase with:

```powershell
mvn test
```

The project currently has no `src/test` test sources, so this command verifies
the Maven test lifecycle but does not execute application-specific tests.

## Troubleshooting

### `mvn` is not recognized

Maven is not on `PATH`. Use the full path to `bin\mvn.cmd`, as shown in the
Windows run instructions, or add the Maven `bin` directory to `PATH` and open a
new PowerShell window.

### Database is already in use

Only one app process can open the embedded H2 database at a time. Stop any
other ClassMate instance with **Ctrl+C** in its PowerShell window, then start
the app once. Do not delete the database file; it contains the saved class
data.

### Port 8080 is already in use

Stop the other application using port 8080, or change `server.port` in
`application.properties` and use the new port in the browser.

### The browser cannot connect or data will not save

Keep the Spring Boot process running, check its PowerShell output for startup
errors, and confirm the browser URL uses the configured port. The interface
shows a notification if it cannot load from or save to the backend.

## Security note

The application has no login or access control. Use it only in a trusted
environment; do not expose it directly to the public internet or place
sensitive student information in an untrusted deployment.
