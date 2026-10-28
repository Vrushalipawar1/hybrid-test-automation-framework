# Hybrid Test Automation Framework (UI + API)

A Java test automation framework using the Page Object Model. It tests a public web shop UI (SauceDemo) with Selenium and a public booking API (Restful-Booker) with REST Assured, runs in parallel, retries flaky tests, reads test data from Excel, and produces Allure reports with failure screenshots.

**Stack:** Java 17 · Selenium 4 · TestNG · REST Assured · Allure · Apache POI · Maven · Docker · GitHub Actions

## What's inside

| Area | How it is done |
|------|----------------|
| Page Object Model | `pages/` holds one class per page; tests never touch locators |
| Parallel runs | `DriverFactory` keeps one WebDriver per thread (`ThreadLocal`); `testng.xml` runs 3 classes at a time |
| Data-driven tests | `ExcelReader` (Apache POI) feeds a TestNG `@DataProvider` from `testdata/login-data.xlsx` |
| API tests | REST Assured with a shared request spec, token auth, JSON schema validation, negative cases |
| Flaky-test handling | `RetryAnalyzer` + `RetryTransformer` re-run a failed test once (`retry.count`) |
| Reporting | Allure: screenshots on UI failure, request/response attached for API calls |
| Config | `config.properties`, any value overridable with `-Dkey=value` |
| Cross-browser / remote | Chrome, Firefox, or a Selenium container via `docker-compose.yml` |
| CI | GitHub Actions runs the suite headless on every push |

## Run it

```bash
mvn test                                  # full suite, headless Chrome
mvn test -Psmoke                          # smoke tests only (groups = "smoke")
mvn test -Dheadless=false                 # watch the browser
mvn test -Dbrowser=firefox
mvn test -Dretry.count=0                  # no retries while debugging
mvn allure:serve                          # open the Allure report after a run
```

Remote browser in Docker:

```bash
docker compose up -d
mvn test -Dexecution=remote -Dremote.url=http://localhost:4444/wd/hub
```

## Tests

| Class | Tests |
|-------|-------|
| `LoginTests` | valid login, locked-out user, 4 invalid-credential rows from Excel, logout, protected page blocked |
| `InventoryTests` | 6 products shown, sort by price (both ways), sort by name Z-A |
| `CartTests` | badge counts, remove item, cart page contents |
| `CheckoutTests` | full order, missing-first-name validation |
| `BookingApiTests` | auth (valid and invalid), create, JSON schema, 404, list, update, delete, delete without token (403) |

15 UI test methods (18 runs with the Excel rows) and 9 API test methods.

## Good to know

- **These are public practice sites** (saucedemo.com, restful-booker.herokuapp.com). They can be slow, reset their data, or change. If a UI test fails on a locator, inspect the element in Chrome DevTools and update the selector in the matching class in `pages/`. If an API test fails with a timeout, rerun it.
- The Restful-Booker API has quirks that the tests encode on purpose: wrong credentials return **200** with a `reason` message, and a successful DELETE returns **201**.
- Test accounts and passwords in `config.properties` are the ones the practice sites publish on their own pages. Never put real credentials in this file; use environment variables or `-D` flags for those.

## Layout

```
src/main/java/com/framework/
  config/   ConfigReader (config.properties + -D overrides)
  driver/   DriverFactory (ThreadLocal WebDriver, local or remote)
  pages/    BasePage + Login / Inventory / Cart / Checkout page objects
  utils/    ExcelReader
src/test/java/com/framework/
  base/       BaseUiTest (browser per test)
  listeners/  screenshot on failure, retry
  ui/         UI tests
  api/        API tests
src/test/resources/  testng suites, test data, JSON schema
```
