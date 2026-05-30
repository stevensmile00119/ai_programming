# /pr — Create a pull request

Create a GitHub pull request for the current branch.

1. Run `git status` and `git log main..HEAD` to summarize what changed
2. Draft a clear PR title (under 70 chars) and a body with:
   - A bullet-point summary of changes
   - A test plan checklist
3. Push the branch if not already pushed
4. Run `gh pr create` with the drafted title and body

Target the `main` branch unless the user specifies otherwise.
