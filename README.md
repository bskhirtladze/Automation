# 🤖 Selenium Java Automation Framework

A demo UI test automation framework built with **Selenium WebDriver 4** and **Java 21**, using the **Page Object Model (POM)** design pattern. Tests run via **TestNG**, results are reported in **Allure**, and the pipeline is automated with **Jenkins + Docker**.

---

## 📋 Table of Contents

- [About the Project](#about-the-project)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Test Cases](#test-cases)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running the Tests](#running-the-tests)
- [Allure Report](#allure-report)
- [CI/CD – Jenkins Pipeline](#cicd--jenkins-pipeline)
- [Docker Setup](#docker-setup)

---

## About the Project

This project demonstrates a production-style Selenium automation framework. It covers end-to-end user flows on a web application, with a focus on clean code structure, reusable components, and integrated CI/CD reporting.

Key highlights:
- **Page Object Model** keeps test logic separate from UI interactions
- **TestNG** handles test execution with parallel support and retry logic
- **Allure** generates rich visual test reports with step-by-step breakdowns
- **Jenkins** runs the suite automatically every day at 08:00 and publishes the Allure report
- **Docker** spins up a Jenkins instance in one command

---

## Tech Stack

| Tool / Library             | Version   | Purpose                                  |
|----------------------------|-----------|------------------------------------------|
| Java                       | 21        | Programming language                     |
| Selenium WebDriver         | 4.18.1    | Browser automation                       |
| TestNG                     | 7.10.2    | Test framework & assertions              |
| WebDriverManager           | 5.8.0     | Automatic browser driver management      |
| Allure TestNG              | 2.27.0    | Test reporting & visualization           |
| AspectJ Weaver             | 1.9.22    | Enables Allure `@Step` annotation weaving|
| Apache Commons IO          | 2.15.1    | Screenshot capture utilities             |
| Jackson Databind           | 2.17.0    | JSON test data handling                  |
| Lombok                     | 1.18.32   | Reduces boilerplate code                 |
| SLF4J + Log4j2             | 2.0.13    | Logging                                  |
| Maven                      | 3.6+      | Build & dependency management            |
| Jenkins                    | LTS-JDK21 | CI/CD pipeline                           |
| Docker                     | Latest    | Jenkins containerization                 |

---

## Project Structure

```
Automation/
│
├── .github/workflows/              # GitHub Actions (if configured)
├── screenshots/                    # Captured screenshots on failure
├── allure-results/                 # Raw Allure result files
│
├── src/
│   └── test/
│       └── java/
│           ├── base/
│           │   └── BaseTest.java           # Shared setup, teardown & logStep()
│           ├── enums/
│           │   └── NavigationItem.java     # Enum for nav items (e.g. CASES)
│           ├── listeners/
│           │   ├── TestListener.java       # TestNG listener (screenshots, hooks)
│           │   └── RetryListener.java      # Retry logic for flaky tests
│           ├── pages/
│           │   ├── NavigationPage.java     # Page Object – navigation bar
│           │   └── TestCasePage.java       # Page Object – test cases page
│           └── tests/
│               └── RegisterUserTest.java   # 5 test methods
│
├── docker-compose.yml              # Jenkins Docker setup
├── Jenkinsfile                     # Declarative Jenkins pipeline
├── testng.xml                      # TestNG suite configuration
├── pom.xml                         # Maven dependencies & build config
└── README.md
```

---

## Test Cases

All 5 tests are in `RegisterUserTest.java`, grouped under:
- **Epic:** `User Management`
- **Feature:** `Test Cases Page`

| # | Method              | Description                                                         | Severity |
|---|---------------------|---------------------------------------------------------------------|----------|
| 1 | `registerUser()`    | Navigates to Test Cases page, closes pop-up, asserts title, clicks a test case | NORMAL   |
| 2 | `userWorkTest()`    | Verifies the user is in a working state                             | CRITICAL |
| 3 | `userOnHoldTest()`  | Verifies the user is in an on-hold state                            | NORMAL   |
| 4 | `userStuckTest()`   | Intentionally failing test — demonstrates Allure failure reporting  | MINOR    |
| 5 | `logOutUserTest()`  | Verifies the user is successfully logged out                        | CRITICAL |

> ⚠️ `userStuckTest()` uses `Assert.assertFalse(true)` and is **intentionally designed to fail**.

### TestNG Suite Configuration (`testng.xml`)

```xml
<suite name="Automation Suite" parallel="methods" thread-count="2">
  <listeners>
    <listener class-name="listeners.TestListener"/>
    <listener class-name="listeners.RetryListener"/>
  </listeners>
  <test name="Register User Tests">
    <classes>
      <class name="tests.RegisterUserTest"/>
    </classes>
  </test>
</suite>
```

Tests run in **parallel** (2 threads), with automatic **retry** support via `RetryListener`.

---

## Prerequisites

- **Java JDK 21**
- **Maven 3.6+**
- **Google Chrome** (ChromeDriver managed automatically via WebDriverManager)
- **Allure CLI** — [installation guide](https://allurereport.org/docs/install/)
- **Docker** (optional, for Jenkins CI)

---

## Getting Started

1. **Clone the repository:**

```bash
git clone https://github.com/bskhirtladze/Automation.git
cd Automation
```

2. **Install dependencies:**

```bash
mvn clean install -DskipTests
```

---

## Running the Tests

**Run the full suite:**

```bash
mvn clean test
```

**Run a specific test method:**

```bash
mvn -Dtest=RegisterUserTest#registerUser test
```

The suite is configured in `testng.xml` and picked up automatically by the Maven Surefire plugin.

---

## Allure Report

After running tests, generate and open the report:

```bash
# Recommended — serves report in your browser instantly
allure serve target/allure-results
```

Or generate a static report:

```bash
allure generate target/allure-results --clean -o allure-report
allure open allure-report
```

The Allure report includes:
- ✅ Pass / ❌ Fail status per test
- Numbered step logs via `logStep()` from `BaseTest`
- Severity and story grouping (`@Epic`, `@Feature`, `@Story`, `@Severity`)
- Screenshots on failure (captured by `TestListener`)

---

## CI/CD – Jenkins Pipeline

The `Jenkinsfile` defines a declarative pipeline with the following stages:

| Stage          | Description                                              |
|----------------|----------------------------------------------------------|
| **Checkout**   | Clones the `main` branch from GitHub                     |
| **Build**      | Compiles the project with `mvn clean compile`            |
| **Test**       | Runs the TestNG suite with `mvn test`, publishes JUnit XML results |
| **Allure Report** | Generates and publishes the Allure report in Jenkins  |

The pipeline is **triggered automatically every day at 08:00** via a cron schedule:

```groovy
triggers {
    cron('0 8 * * *')
}
```

After each run the workspace is cleaned automatically.

---

## Docker Setup

A Jenkins instance can be started locally using Docker Compose:

```bash
docker-compose up -d
```

This starts Jenkins on **http://localhost:8080** with:
- JDK 21 pre-installed
- Persistent data stored in the `jenkins_home` Docker volume
- Docker socket mounted so Jenkins can run Docker commands

```bash
# Stop Jenkins
docker-compose down
```

---

## 🔗 Repository

[https://github.com/bskhirtladze/Automation](https://github.com/bskhirtladze/Automation)

---

> *Built for learning and practice purposes.*
