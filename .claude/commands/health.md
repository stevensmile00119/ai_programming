# /health — Check API health

Check whether the backend API is up and responding.

Run: `curl -s http://localhost:8080/api/twse/health`

- If it returns 200, report the backend is healthy
- If it fails to connect, report the backend is not running and remind the user to start it with `mvn spring-boot:run`
- Also check the frontend is reachable at http://localhost:5173 if possible
