# Government Information Remediation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Produce a reviewable version 3.0.5 whose Play listing, app disclosure, screenshots, and public policy directly meet the misleading-claims remediation requirements.

**Architecture:** Android resource text supplies the visible initial disclaimer and disclosure page; existing `FragAbout` continues to render automatically linked sources. A resource test protects the exact source and version contract. The HelpDev policy and Play listing use matching source and independence language.

**Tech Stack:** Java/XML Android app, JUnit 4/Gradle, Android emulator, Next.js/TypeScript, Google Play Console.

**Spec:** `docs/superpowers/specs/2026-10-28-government-information-remediation-design.md`

## Global Constraints

- Keep package and namespace `br.com.helpdev.consultaendereco`.
- Use versionCode `6` and versionName `3.0.5`.
- Do not put the Correios URL on the initial Android screen.
- Put functional Correios and Ministry source URLs in the disclosure page, Play description, and public privacy policy.
- Do not claim a government affiliation and do not appeal before the replacement submission is reviewed.

## Review Focus

- The initial layout must remain free of direct source URLs while making the disclosure destination legible.
- All source URLs must be HTTPS and publicly reachable before publication.
- Default-English resources must carry the same independent-app behavior as pt-BR.
- `MainActivity.selectItem(3)` must still point the renamed drawer item to `FragAbout`.
- The HelpDev policy must retain accurate ads, location, external-provider, retention, and contact claims while adding source disclosure.

---

### Task 1: Protect the stronger disclosure contract

**Files:**
- Modify: `app/src/test/java/br/com/helpdev/consultaendereco/PolicyDisclosureResourceTest.java`
- Modify: `res/values-pt/strings.xml`
- Modify: `res/values/strings.xml`

- [ ] **Step 1: Write failing resource tests**

Require the Portuguese menu phrase `Fontes oficiais e aviso de independência`, the Correios direct source, the Ministry source, and version 6 / `v3.0.5`.

- [ ] **Step 2: Run focused test and verify RED**

Run: `./gradlew testDebugUnitTest --tests br.com.helpdev.consultaendereco.PolicyDisclosureResourceTest`

Expected: failure because the version and new official-source contract do not exist.

- [ ] **Step 3: Update Android resource copy**

Use the selected disclosure wording and source URLs. Preserve `autoLink="web"` in `frag_about.xml` and the absence of a source URL from `frag_find_address.xml`.

- [ ] **Step 4: Run focused test and verify GREEN**

Run the focused test and expect success.

### Task 2: Prepare version 3.0.5 and reviewer-visible capture

**Files:**
- Modify: `app/build.gradle`
- Modify: `app/src/test/java/br/com/helpdev/consultaendereco/PolicyDisclosureResourceTest.java`
- Create: `imagens_consulta_enderecos/pt-br/policy-disclosure-3.0.5.png`

- [ ] **Step 1: Extend the release identity test**

Require Gradle and the version resource to use code 6 / name 3.0.5.

- [ ] **Step 2: Verify RED**

Run the focused test and expect a version-contract failure.

- [ ] **Step 3: Update Gradle and version resource**

Set code 6/name 3.0.5 and update `validateV2Identity`.

- [ ] **Step 4: Build, install, and capture**

Build debug, install on the existing emulator, navigate to the disclosure page, and save the screenshot at the stated pt-BR path. Confirm no URL appears on the initial screen.

- [ ] **Step 5: Verify complete Android build**

Run: `./gradlew testDebugUnitTest validateV2Identity bundleRelease && git diff --check`

Expected: success and a signed AAB at `app/build/outputs/bundle/release/app-release.aab`.

### Task 3: Align and deploy public privacy policy

**Files:**
- Modify: `helpdev.com.br/src/app/apps/consulta-endereco/privacidade/page.tsx`

- [ ] **Step 1: Fast-forward clean local main**

Fetch `origin` and update the website checkout to `origin/main` before editing.

- [ ] **Step 2: Add source/disclaimer assertions and verify RED**

Require the private-independent wording and both official source URLs from the route source.

- [ ] **Step 3: Update the policy copy and effective date**

Add a clearly headed official-source paragraph while preserving existing verified data-practice claims.

- [ ] **Step 4: Verify and publish policy**

Run `npm run build`, inspect `out/apps/consulta-endereco/privacidade/index.html`, commit to `main`, create the next annotated patch tag, wait for the tag workflow, and check the live page.

### Task 4: Replace Play reviewer surfaces and submit

**Files:**
- Modify: Play Console default pt-BR listing and production track

- [ ] **Step 1: Replace the pt-BR full description**

Start with the independent-app warning and a “Fontes oficiais” section containing both URLs; preserve accurate product capability text below it.

- [ ] **Step 2: Save and reopen the listing**

Verify the exact new first paragraphs and the short disclaimer persist.

- [ ] **Step 3: Upload version 6 and the disclosure screenshot**

Use the signed AAB and make the new screenshot reviewer-visible in the default pt-BR phone assets.

- [ ] **Step 4: Review all pending changes and submit after action-time confirmation**

Verify no blocking Console error remains, then request the final user confirmation and submit to production review.
