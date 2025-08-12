# Contributing Guide

Thank you for contributing to this project!  
This guide explains how to set up the repository locally, make changes, and submit them safely.


## 1. Getting Started

### Fork or Clone?
Since you have been added as a **collaborator**, you can **clone** the repository directly.

```bash
git clone https://github.com/kavya410004/SalQ.git
cd SalQ
```


## 2. Create a New Branch

Always work on a separate branch instead of `main`.

```bash
git checkout -b feature/your-feature-name
```

**Branch naming tips:**
- `feature/...` for new features  
- `fix/...` for bug fixes  
- `docs/...` for documentation changes


## 3. Make Changes

- Keep your commits focused and descriptive.
- Follow the project’s coding style.
- Test your changes before committing.

```bash
git add .
git commit -m "Short description of change"
```


## 4. Keep Your Branch Updated

Before pushing, ensure your branch has the latest `main` updates:

```bash
git fetch origin
git merge origin/main
```

If there are **merge conflicts**:
1. Open the conflicting files and resolve manually.
2. Mark conflicts as resolved:
   ```bash
   git add <file>
   ```
3. Commit the resolution:
   ```bash
   git commit
   ```


## 5. Push Changes

```bash
git push origin feature/your-feature-name
```


## 6. Create a Pull Request (PR)

- Go to the repository on GitHub.
- Click **"Compare & pull request"**.
- Provide a **clear title** and **description** of the changes.
- Assign reviewers.



## 7. Code Review & Merge

- PR will be reviewed by at least one other collaborator.
- If changes are requested, make them on the same branch and push again.
- Once approved, the PR will be merged into `main`.



## 8. After Merging

Pull the latest `main` branch locally:

```bash
git checkout main
git pull origin main
```


## 9. Delete the Feature Branch

After your branch has been merged and is no longer needed:

**From GitHub:**
- Click the **"Delete branch"** button after merging the PR.

**From terminal:**
```bash
# Delete locally
git branch -d feature/your-feature-name

# Delete from remote
git push origin --delete feature/your-feature-name
```

💡 Use `-D` instead of `-d` if you want to delete a branch that hasn’t been merged.


## 10. General Guidelines

- **Small PRs are better** – easier to review and merge.
- Communicate with the team before starting large changes.
- Keep commits clean and meaningful.
- Avoid pushing directly to `main`.




Happy coding! 🚀
