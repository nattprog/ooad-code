### Git and GitHub

- Work in your own branches

- Use Github pull requests to submit code

- Regularly pull main and merge main into your own branch, especially before pull requests

## Lab Exercise

### Dependencies

sqlite3, gradle

### Environment Setup

You're on your own

### Run project

(in seminar_manager directory)

Fresh initialisation of sqlite database, and seed with dummy data:
`./gradlew cleanSeedDb`

Run project:
`./gradlew run`

Build executable jar:
`./gradlew shadowJar`

Run executable jar:
`java -jar app/build/libs/app-all.jar`
