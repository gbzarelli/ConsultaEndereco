# Play Policy Remediation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the app and its public privacy policy clearly identify the independent status of Consulta Endereço and its official CEP reference before resubmitting version 3.0.4.

**Architecture:** The first search screen continues to show a concise independence notice without an official-source URL. The navigation drawer makes the existing disclosure screen explicitly discoverable as “Transparência e fontes”, where the functional Correios URL remains clickable. The public privacy policy uses the same independent-service terminology and links the Correios reference.

**Tech Stack:** Android XML resources and Java/JUnit; Next.js/TypeScript privacy-policy page; Gradle and npm builds.

**Spec:** `docs/superpowers/specs/android-v2-publication.md`, plus Play Console issue 4988781799155905129 reviewed on 2026-10-25.

## Global Constraints

- Application ID and namespace remain `br.com.helpdev.consultaendereco`.
- Increment the release to versionName `3.0.4` and versionCode `5`.
- The first app screen must not contain a direct Correios URL.
- The Correios Busca CEP URL remains functional and clickable in the disclosure screen and appears in the Play full description.
- Do not claim government affiliation; do not submit an appeal.
- Publish the privacy-policy change from `helpdev.com.br` `main` and create the next semantic patch tag.

## Review Focus

- Drawer item 4 must still open `FragAbout` after its label changes; cover the array order in the resource test.
- Portuguese and default-English resources must agree on the updated disclosure entry; cover both resource files.
- The initial search layout must remain free of the Correios URL; preserve the existing assertion.
- The privacy page must not describe ViaCEP or BrasilAPI as government/public services; inspect the generated page text in the site build.
- The Correios reference URL must return HTTP 200 before release preparation.

---

### Task 1: Make the in-app disclosure easy to find

**Files:**
- Modify: `res/values-pt/strings.xml`
- Modify: `res/values/strings.xml`
- Modify: `app/src/test/java/br/com/helpdev/consultaendereco/PolicyDisclosureResourceTest.java`

**Interfaces:**
- Consumes: The fourth `menus` array entry selected by `MainActivity.selectItem(3)`.
- Produces: A drawer label that identifies the existing `FragAbout` page as the disclosure and source page.

- [ ] **Step 1: Write a failing resource test**

Assert that the Portuguese and default resource arrays label item 4 as “Transparência e fontes” / “Transparency and sources”, while the initial-screen layout has no Correios URL.

- [ ] **Step 2: Run the focused test and verify it fails**

Run: `./gradlew testDebugUnitTest --tests br.com.helpdev.consultaendereco.PolicyDisclosureResourceTest`

Expected: FAIL because the existing drawer labels are “Sobre” and “About”.

- [ ] **Step 3: Update the fourth drawer labels in both string resources**

Keep `MainActivity.selectItem(3)` unchanged so the renamed item still opens `FragAbout`.

- [ ] **Step 4: Run the focused test and verify it passes**

Run: `./gradlew testDebugUnitTest --tests br.com.helpdev.consultaendereco.PolicyDisclosureResourceTest`

Expected: PASS.

### Task 2: Publish an aligned privacy policy

**Files:**
- Modify: `helpdev.com.br/src/app/apps/consulta-endereco/privacidade/page.tsx`

**Interfaces:**
- Consumes: The app package ID, the Correios Busca CEP URL, and the established Help DEV privacy-page component pattern.
- Produces: A public privacy page that states the app’s independence and accurately names ViaCEP and BrasilAPI as independent providers.

- [ ] **Step 1: Add the policy assertions before editing the page**

Use a narrow text verification to require the independence statement, Correios source URL, and the absence of “serviços públicos ViaCEP e BrasilAPI”.

- [ ] **Step 2: Run the verification and verify it fails**

Run: a Node or shell assertion against `src/app/apps/consulta-endereco/privacidade/page.tsx`.

Expected: FAIL because the current page does not contain the independence statement or Correios URL.

- [ ] **Step 3: Update the page copy and effective date**

Add the independence statement, link Correios Busca CEP as the official reference source, replace “serviços públicos” with “serviços independentes”, and set the effective date to 25 October 2026.

- [ ] **Step 4: Run the page assertion and production site build**

Run: `npm run build`

Expected: PASS with the privacy route generated successfully.

- [ ] **Step 5: Commit, push `main`, and create the next patch tag**

Push the privacy-page commit to `origin/main`, then create and push the next `v1.3.x` tag after checking the remote tag list.

### Task 3: Create and verify the Android release

**Files:**
- Modify: `app/build.gradle`
- Modify: `app/src/test/java/br/com/helpdev/consultaendereco/PolicyDisclosureResourceTest.java`

**Interfaces:**
- Consumes: Android resource changes from Task 1.
- Produces: Signed AAB versionCode 5 / versionName 3.0.4.

- [ ] **Step 1: Extend the resource test to require version 5 release identity**

Assert the Gradle identity task configuration expects code 5.

- [ ] **Step 2: Run the targeted identity test and verify it fails**

Run: `./gradlew testDebugUnitTest --tests br.com.helpdev.consultaendereco.PolicyDisclosureResourceTest`

Expected: FAIL until the version is incremented.

- [ ] **Step 3: Increment the Gradle version and identity guard**

Set `versionCode 5`, `versionName '3.0.4'`, and the `validateV2Identity` expectation to 5.

- [ ] **Step 4: Run all Android verification and build the signed bundle**

Run: `./gradlew testDebugUnitTest validateV2Identity bundleRelease && git diff --check`

Expected: PASS and produce `app/build/outputs/bundle/release/app-release.aab`.

- [ ] **Step 5: Commit and push the Android remediation branch**

Commit the resource, test, version, and plan changes and push `gz/android-modernizacao`.

### Task 4: Reconcile and resubmit Play metadata

**Files:**
- Modify: Google Play Console default pt-BR store listing and production release.

**Interfaces:**
- Consumes: version 5 AAB and the published privacy-policy URL.
- Produces: A production submission with an explicit short-description disclaimer and current full-description source block.

- [ ] **Step 1: Update and save the short description**

Use: `App independente, sem vínculo com Governo do Brasil ou Correios.`

- [ ] **Step 2: Reopen the listing and confirm persisted metadata**

Verify the short description states independence and the full description starts with the independence notice plus the Correios source URL.

- [ ] **Step 3: Upload version 5 and save the production release**

Use release notes that identify transparency and source-disclosure improvements.

- [ ] **Step 4: Send the changes for review after user confirmation**

Confirm the final Google Play submission action at action time and verify that Publishing overview reports the changes in review.

## Self-Review

- The plan covers both policy surfaces cited by Google: Play metadata and app UI.
- It preserves the explicit user constraint against a direct Correios link on the initial screen.
- It adds the public privacy-policy consistency check requested by the user.
- It includes release identity, signed-bundle, site-build, metadata persistence, and Console-status verification.
