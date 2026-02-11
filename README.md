### Git and GitHub

**Clone repository:**

Navigate to target directory

`git clone https://github.com/nattprog/ooad-code.git`

**Rules:**

- Work in your own branches

- Use Github pull requests to submit code

- Regularly pull main and merge main into your own branch, especially before pull requests

## Lab Exercise

### Dependencies

Java JDK

### Environment Setup

**Recommended IDE:**

VSCode

**Recommended VSCode extensions:**

Java by Oracle Corporation, oracle.com

Gradle for Java by Microsoft, microsoft.com

### Run project

(in seminar_manager directory)

**Fresh initialisation of sqlite database, and seed with dummy data:**
`./gradlew cleanSeedDb`

**Run project:**
`./gradlew run`

**Build executable jar:**
`./gradlew shadowJar`

**Run executable jar:**
`java -jar app/build/libs/app-all.jar`
