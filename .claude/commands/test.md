# /test — Run all tests

Run the full test suite for this project.

1. Run `mvn test` from the project root to execute all backend tests
2. Report the number of tests passed, failed, and skipped
3. If any tests fail, show the failure messages and suggest fixes

To run a single test class, the user can pass it as an argument: `/test TwseServiceTest`
If an argument is provided, run `mvn test -Dtest=$ARGUMENTS` instead.
