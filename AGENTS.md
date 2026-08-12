# Self-Healing Selenium Agent

Goal:

Keep Selenium tests passing.

Rules:

- Do not modify application business logic.
- Only repair tests if the UI changed.
- Run mvn test after every change.
- Stop only when all tests pass.
