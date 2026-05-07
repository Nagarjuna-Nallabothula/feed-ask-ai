# 🔐 Branch Protection & Code Owner Access Control Guide

## Overview
This document explains how to restrict direct pushes to the `main` branch to code owners only.

---

## 🔧 Step 1: GitHub Repository Settings

### 1.1 Access Branch Protection Rules
1. Go to your GitHub repository: `https://github.com/RajaVamsi/feed-ask-ai`
2. Navigate to **Settings** tab
3. Click on **Branches** in the left sidebar
4. Under **Branch protection rules**, click **Add rule**

### 1.2 Create Protection Rule for `main` Branch
Fill in the following configuration:

| Setting | Value | Enabled |
|---------|-------|---------|
| **Branch name pattern** | `main` | ✓ |
| **Require a pull request before merging** | - | ✓ |
| &nbsp;&nbsp;→ Require approvals | 1 | ✓ |
| &nbsp;&nbsp;→ Dismiss stale PR approvals | - | ✓ |
| &nbsp;&nbsp;→ Require review from code owners | - | ✓ |
| &nbsp;&nbsp;→ Require approval of most recent push | - | ✓ |
| **Require status checks to pass** | - | ✓ |
| &nbsp;&nbsp;→ Require branches up to date | - | ✓ |
| **Require conversation resolution** | - | ✓ |
| **Do not allow bypassing** | Apply to admins | ✓ |

---

## 🎯 Step 2: Detailed Configuration Steps

### Step 2.1: Basic Rule Settings
```
Branch name pattern: main
Applies to: main
```

### Step 2.2: Require Pull Request
```
☑ Require a pull request before merging
  ☑ Require approvals: 1
  ☑ Require review from Code Owners
  ☑ Dismiss stale pull request approvals when new commits are pushed
  ☑ Require approval of the most recent reviewable push
```

### Step 2.3: Require Status Checks
```
☑ Require status checks to pass before merging
  ☑ Require branches to be up to date before merging
  
Status checks to add:
  ☑ build (CI Pipeline)
  ☑ code-quality (Code Quality Analysis)
  ☑ security-scan (Security Scanning)
```

#### How to Add Status Checks:

1. Go to **Branch Protection → Status Checks** section
2. You'll see: *"Search for status checks in the last week for this repository"*
3. The checks will be auto-discovered from your CI/CD workflows
4. Click the status checks to enable them:

| Check | Purpose | Must Enable |
|-------|---------|------------|
| **build** | Builds project, runs tests | ✅ YES |
| **code-quality** | Checkstyle, SonarCloud analysis | ✅ YES |
| **security-scan** | Dependency vulnerabilities | ✅ YES |

**Note:** Status checks only appear after your CI/CD workflows have run at least once.

If status checks don't appear:
1. Run your CI/CD pipeline first (create a PR)
2. Wait for the pipeline to complete
3. Return to branch protection settings
4. Status checks will now be available

### Step 2.4: Require Conversation Resolution
```
☑ Require conversation resolution before merging
  (All code review comments must be resolved)
```

### Step 2.5: Admin Enforcement
```
☑ Do not allow bypassing the above settings
  (Admins must also follow the protection rules)
```

### Step 2.6: Additional Security (Optional)
```
☑ Require signed commits
  (Optional - Ensures commits are cryptographically signed)

☑ Require linear history
  (Optional - Prevents merge commits, maintains clean history)
```

### Step 2.7: Disable Dangerous Options
```
☐ Allow force pushes
  (UNCHECKED - Prevent force pushes)

☐ Allow deletions
  (UNCHECKED - Prevent branch deletion)
```

---

## 📋 Complete Branch Protection Rule

```
Main Branch Protection
├─ Require Pull Request: YES ✓
│  ├─ Min Approvals: 1
│  ├─ Code Owner Review: REQUIRED ✓
│  ├─ Dismiss Stale Approvals: YES ✓
│  └─ Most Recent Push Approved: YES ✓
│
├─ Status Checks: REQUIRED ✓
│  ├─ Branches Up to Date: YES ✓
│  ├─ build (CI)
│  ├─ code-quality (Checkstyle, SonarCloud)
│  └─ security-scan (Dependency Check)
│
├─ Conversation Resolution: REQUIRED ✓
│  └─ All code reviews must be resolved
│
├─ Admin Enforcement: YES ✓
│  └─ Do not allow bypassing the above
│
└─ Security Options:
   ├─ Require Signed Commits: Optional
   ├─ Require Linear History: Optional
   ├─ Allow Force Pushes: NO ✓
   └─ Allow Deletions: NO ✓
```

---

## ⚙️ How CODEOWNERS + Branch Protection Work Together

### Key Concept:
GitHub doesn't have a direct "restrict who can push" option in branch protection. Instead, we use:

1. **CODEOWNERS file** (.github/CODEOWNERS)
   - Designates who owns which files
   - Triggers automatic review requests

2. **Branch Protection Rules**
   - Requires code owner approval
   - Enforces PR + status checks

### Result:
```
Everyone CAN create PRs ✓
Code Owner MUST approve every PR ✓
All tests MUST pass ✓
Only after both → Code Owner can merge ✓
```

This effectively gives code owners exclusive merge authority, which prevents unauthorized code on main. ✓

---

## 🚀 How It Works After Setup

### When someone tries to push to `main`:
```
👤 Developer pushes code to main branch
    ↓
🔒 GitHub checks branch protection rules
    ↓
❌ DENIED: "You don't have permission to push to this branch"
            "Only selected actors can push to this branch"
    ↓
✅ Solution: Create Pull Request instead
```

### When someone creates a Pull Request:
```
1️⃣ Developer creates PR
   ↓
2️⃣ CI Pipeline runs (build, tests, quality checks)
   ↓
3️⃣ Code Owner approval required
   ↓
4️⃣ All status checks must pass
   ↓
5️⃣ Code Owner merges PR to main
   ↓
✅ Code is now in main branch
```

---

## 📊 Access Control Matrix

| Action | Developer | Code Owner |
|--------|-----------|-----------|
| **Direct push to main** | ❌ Rejected by rules | ✅ Can push directly* |
| **Create PR** | ✅ Allowed | ✅ Allowed |
| **Review PR** | ❌ No authority | ✅ Required |
| **Approve PR** | ❌ Cannot approve | ✅ Can approve |
| **Merge PR** | ❌ Cannot merge | ✅ Can merge (after checks) |

*Code owner can push directly if emergency, but must still follow all protection rules*

### Practical Workflow:
```
Developer Flow:
  1. Create feature branch
  2. Make changes & push
  3. Create Pull Request
  4. Wait for code owner review
  5. Code owner approves
  6. CI checks pass
  7. Code owner merges PR
  
Code Owner Flow:
  1. Can do all of above
  2. Can push directly to main if emergency
  3. Must ensure CI passes
```

---

## 🛡️ What This Protects

```
✓ Prevents accidental pushes to main
✓ Ensures all code goes through review
✓ Code owner controls all merges
✓ CI/CD checks must pass
✓ Code quality standards enforced
✓ Security scanning performed
✓ Full audit trail of all changes
```

---

## 🔐 CODEOWNERS File

Your repository has the following CODEOWNERS file:

```
# .github/CODEOWNERS

* @Nagarjuna-Nallabothula
/build.gradle @Nagarjuna-Nallabothula
/.github @Nagarjuna-Nallabothula
/src/main/java/com/rb/feed_ask_ai/ @Nagarjuna-Nallabothula
/.github/workflows/ @Nagarjuna-Nallabothula
```

This means:
- **@Nagarjuna-Nallabothula** is the owner of all code
- Must approve every PR before merge
- Can push directly to main (if needed in emergencies)

---

## ✅ Workflow for Team Members

### For Developers (Non-Code Owners):
```bash
# ✓ DO THIS:
git checkout -b feature/my-feature
git commit -m "Add feature"
git push origin feature/my-feature
# Create Pull Request on GitHub

# ✗ DON'T DO THIS:
git push origin main  # Will be rejected!
```

### For Code Owner (@Nagarjuna-Nallabothula):
```bash
# Step 1: Review the PR on GitHub
# Step 2: Check CI status ✓
# Step 3: Request changes if needed
# Step 4: Approve the PR
# Step 5: Merge to main
# OR:
git checkout main
git pull
git merge --no-ff feature/branch
git push origin main
```

---

## 🚨 Error Messages & Solutions

### Error: "This branch requires 1 approval from code owners to merge this pull request"
```
✓ Reason: Code owner review is required
✓ Solution: Wait for code owner to review and approve
✓ Contact: @Nagarjuna-Nallabothula
```

### Error: "Required status checks must pass before merging"
```
✓ Reason: CI/CD pipeline failed
✓ Solution: 
   1. Check failed tests in CI logs
   2. Fix the issue
   3. Push fix to PR branch
   4. CI will re-run automatically
```

### Error: "This branch has 1 conversation left to resolve"
```
✓ Reason: Code review comments haven't been resolved
✓ Solution:
   1. Review the comments
   2. Fix the code or reply to comments
   3. Mark conversations as resolved
```

### Error: "Expected the branch to be up to date"
```
✓ Reason: Main branch has new commits
✓ Solution:
   1. Pull latest from main: git pull origin main
   2. Resolve any merge conflicts
   3. Push to PR branch
   4. Try merging again
```

### Warning: "Merging is blocked"
```
✓ Possible Causes:
   - Code owner hasn't approved yet
   - CI checks are still running
   - Status checks are failing
   - Conversations aren't resolved

✓ Solution: Wait for all checks to pass and code owner approval
```

---

## 📝 Adding More Code Owners

To add more code owners in the future:

### 1. Update `.github/CODEOWNERS` file:
```bash
* @Nagarjuna-Nallabothula @AnotherOwner

# Different owners for different areas:
/src/backend/ @Nagarjuna-Nallabothula
/src/frontend/ @AnotherOwner
```

### 2. Update GitHub Branch Protection:
```
Go to Settings → Branches → Add to "Restrict push access" list
```

---

## ✨ Key Benefits

```
🔐 Security
  → Only trusted developers push to main
  → All changes are reviewed

📊 Quality
  → All tests must pass
  → Code quality checks enforced
  → Security scans mandatory

📋 Audit Trail
  → Every merge is recorded
  → Who approved what is tracked
  → Easy rollback if needed

👥 Team Workflow
  → Clear process for everyone
  → Prevents accidental issues
  → Enforces best practices
```

---

## 📞 Support

If you need to temporarily bypass protection (emergency only):
1. Contact GitHub organization admin
2. Temporarily disable rule
3. Push fix
4. Re-enable protection
5. Document the incident

---

**Setup Status: ✅ Ready**
**Created: May 7, 2026**
**Last Updated: May 7, 2026**

