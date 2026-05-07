# ✅ Branch Protection Quick Checklist

## GitHub Settings → Branches → Add Rule

Copy-paste this configuration exactly:

---

## 📋 Settings Checklist

### 1. Branch Protection Rule
- [x] Branch name pattern: **main**
- [ ] (GitHub will show: "Applies to 1 branch: main")

---

### 2. Protect Matching Branches

#### Pull Request Requirements
- [x] **Require a pull request before merging**
- [x] **Require approvals**: Set to **1**
- [x] **Dismiss stale pull request approvals when new commits are pushed**
- [x] **Require review from Code Owners**
- [x] **Require approval of the most recent reviewable push**

#### Status Checks
- [x] **Require status checks to pass before merging**
- [x] **Require branches to be up to date before merging**
- [x] Select the following checks (after they appear):
  - [ ] `build`
  - [ ] `code-quality`
  - [ ] `security-scan`
  
  **Note:** These will auto-appear after first CI run. If not visible yet:
  1. Create a test PR first
  2. Let CI/CD run
  3. Come back and select the checks

#### Other Requirements
- [x] **Require conversation resolution before merging**
- [ ] **Require signed commits** (Optional - skip for now)
- [ ] **Require linear history** (Optional - skip for now)
- [ ] **Require deployments to succeed before merging** (Not applicable)
- [ ] **Lock branch** (No - this would make branch read-only)

---

### 3. Rules Applied To

- [x] **Do not allow bypassing the above settings**
  - This makes admins follow the rules too ✓

---

### 4. Additional Restrictions

- [ ] **Allow force pushes** (Leave UNCHECKED)
  - Force pushes can overwrite history
  
- [ ] **Allow deletions** (Leave UNCHECKED)
  - Prevents accidental branch deletion

---

## 🎯 Final Summary

```
✅ ENABLED (Must enable):
  ├─ Require pull request before merging
  ├─ Require approvals (1)
  ├─ Require review from Code Owners
  ├─ Dismiss stale approvals
  ├─ Require approval of most recent push
  ├─ Require status checks to pass
  ├─ Require branches up to date
  ├─ Require conversation resolution
  ├─ Do not allow bypassing
  └─ [When available: build, code-quality, security-scan]

❌ DISABLED (Leave unchecked):
  ├─ Allow force pushes
  └─ Allow deletions
```

---

## 📸 Expected Result

After clicking **"Create"**, you should see:

```
Branch protection rule created

Rules for main:
✓ Requires pull request reviews before merging
  • 1 approvals required
  • Code owner review required
  • Stale approvals will be dismissed
  • Most recent push must be approved
✓ Requires status checks to pass
  • [Once configured: build, code-quality, security-scan]
✓ Conversations must be resolved
✓ Apply to administrators
✓ Dismisses stale reviews when new commits are pushed
```

---

## 🚀 After Setup - What Happens

### When a developer tries to push to main:
```
❌ Push rejected (not a code owner)
✅ Alternative: Create Pull Request

@Nagarjuna-Nallabothula can push directly, but 
CI checks must pass first.
```

### When a PR is created:
```
1. CI/CD automatically runs
2. Code owner is automatically requested for review
3. Cannot merge until:
   ✓ Code owner approves
   ✓ All status checks pass
   ✓ All conversations resolved
   ✓ Branch is up to date with main
4. Code owner merges PR
```

---

## ⚠️ Troubleshooting

### "Status checks not showing up"
- Solution: Create a test PR first to let CI run, then come back to enable them

### "Can't see 'Require code owners' option"
- Solution: Make sure `.github/CODEOWNERS` file exists and is committed to repo

### "Rule won't save"
- Solution: Check all required fields are filled, refresh page and try again

---

**Last Updated:** May 7, 2026
**Status:** ✅ Production Ready

