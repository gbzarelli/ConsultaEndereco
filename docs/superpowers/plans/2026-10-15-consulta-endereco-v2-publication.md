# Consulta Endereço v2 Publication Implementation Plan

> Release follow-up: version code `1` / name `3.0` was submitted to Play Console and is in review. The address-search correction is shipped as version code `2` / name `3.0.1` so the reviewed artifact is not reused.

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Prepare and publish a separate Google Play listing named Consulta Endereço under `br.com.helpdev.consultaendereco`, signed through Play App Signing with a locally protected upload key.

**Architecture:** Keep the original Play app intact while assigning the new app a distinct Gradle `applicationId` and moving Java source/tests to the new namespace `br.com.helpdev.consultaendereco`. Google Play manages the distribution signing key; Gradle reads a separate upload key and password file from the local `android-keys` repository. Configure v2-specific Ads and Maps identifiers, validate a signed AAB on an internal track, then roll it out to production.

**Tech Stack:** Java/XML, Android Gradle Plugin 8.13.2, Gradle 8.13, AndroidX, Google Play Console, Play App Signing, AdMob, Google Maps SDK.

**Spec:** `docs/superpowers/specs/android-v2-publication.md`

## Global Constraints

- New `applicationId` and Java namespace: `br.com.helpdev.consultaendereco`; move source and test packages to the matching directory.
- Keep the visible app title **Consulta Endereço** and the original listing/package intact.
- Do not migrate old app data automatically; saved places remain in the original app.
- Start the new package version sequence at `versionCode 1`; keep version name `3.0` unless Play validation requires a change.
- Use Google Play App Signing for the new app and keep a distinct local upload key in `android-keys`.
- Do not put passwords, keystores, production Ads IDs, or Maps API keys in Git.
- Preserve the current dirty state of `/Volumes/gbz-ext/repositories/helpdev/android-keys`; exclude only new v2 key files in its local `.git/info/exclude`.
- Use the existing modernization baseline: Java/XML, `minSdk 24`, `compileSdk/targetSdk 36`.
- Do not alter or unpublish the existing listing.
- Stop for the user's direct acceptance if Play Console shows a legally binding agreement.

## Review Focus

- Package collision or Console draft created with the wrong package — Task 3 checks the exact package before creating the listing.
- Keystore/password/alias mismatch or secret files visible to Git — Task 1 verifies keytool access, alias, file permissions, and local excludes.
- AAB has wrong package/version or is unsigned/incorrectly signed — Tasks 2 and 5 verify Gradle identity, package metadata, and upload certificate.
- Maps key does not authorize the Play-distributed app signature — Task 4 verifies package plus app-signing certificate restrictions; Task 5 checks rendered map tiles from the internal track.
- AdMob app ID/unit mismatch or policy blocks serving — Task 4 checks the app/package association and unit; Task 5 checks debug test ads and the Console's production status.

---

### Task 1: Create and protect the v2 upload key

**Files:**
- Create: `/Volumes/gbz-ext/repositories/helpdev/android-keys/consultaendereco_v2_upload.jks`
- Create: `/Volumes/gbz-ext/repositories/helpdev/android-keys/.consultaendereco_v2_upload.pass`
- Create: `/Volumes/gbz-ext/repositories/helpdev/android-keys/consultaendereco_v2_upload_certificate.pem`
- Create: `/Volumes/gbz-ext/repositories/helpdev/android-keys/consultaendereco-v2-signing.properties`
- Modify local only: `/Volumes/gbz-ext/repositories/helpdev/android-keys/.git/info/exclude`

**Interfaces:**
- Produces: alias `consultaendereco_v2_upload`; store and key passwords are read from the `.pass` file; the properties file records keystore path, password-file path, alias, and key-password-file path.
- Consumes: none.

- [ ] **Step 1: Record the existing key-repository state**

Run: `git -C /Volumes/gbz-ext/repositories/helpdev/android-keys status --short`
Expected: preserve all currently listed changes; do not edit, stage, or delete existing key files or `pass.txt`.

- [ ] **Step 2: Create a strong password file and upload keystore**

Write one newly generated high-entropy password to `.consultaendereco_v2_upload.pass`; create a 4096-bit RSA upload key with alias `consultaendereco_v2_upload` and a long validity period. Use the same password for the store and key so one password file is sufficient. Do not print the password or put it in command arguments, shell history, Gradle output, or a tracked file.

- [ ] **Step 3: Export the public upload certificate and local metadata**

Export the alias certificate as PEM to `consultaendereco_v2_upload_certificate.pem`. Write `consultaendereco-v2-signing.properties` with `storeFile`, `storePasswordFile`, `keyAlias`, and `keyPasswordFile` values; store file paths only, with no literal password.

- [ ] **Step 4: Protect and exclude only the new files**

Set owner-only permissions on the `.jks`, `.pass`, and properties file. Add the four exact v2 paths to local `.git/info/exclude`. Do not edit shared `.gitignore` files.

- [ ] **Step 5: Verify the key and repository state**

Run `keytool -list -v` using the password-file input, confirm the alias and certificate, confirm file permissions, and run `git status --short` plus `git check-ignore` for the new files.
Expected: key opens, alias is present, four intended files are ignored locally, and no prior dirty status changed.

### Task 2: Set the new Android identity and release signing configuration

**Files:**
- Modify: `app/build.gradle`
- Modify: `local.properties.example`
- Modify local only: `local.properties`

**Interfaces:**
- Consumes: Task 1 signing properties and upload alias.
- Produces: release `applicationId` `br.com.helpdev.consultaendereco`, release `versionCode 1`, and release signing configuration using the v2 upload key; debug package remains suffixed `.debug` and debug Ads IDs remain Google's samples.

- [ ] **Step 1: Add a failing Gradle identity check**

Register `validateV2Identity` in `app/build.gradle`. It must fail unless `android.defaultConfig.applicationId` equals `br.com.helpdev.consultaendereco`, `android.defaultConfig.versionCode` equals `1`, and `android.namespace` equals `br.com.helpdev.consultaendereco`.

- [ ] **Step 2: Run the identity check and confirm it fails on the old ID**

Run: `./gradlew :app:validateV2Identity --no-daemon --console=plain`
Expected: FAIL because the current release application ID and version code have not yet been changed to the v2 values.

- [ ] **Step 3: Update the application ID and version sequence**

In `app/build.gradle`, set `applicationId` to `br.com.helpdev.consultaendereco`, preserve `namespace`, set `versionCode 1`, and retain `versionName '3.0'`.

- [ ] **Step 4: Load the external signing properties without committing them**

Read a `SIGNING_PROPERTIES_FILE` path from ignored `local.properties`, load the four metadata fields from that external file, and assign a release `signingConfig`. Read password file contents at build time without logging them. Keep debug signing behavior unchanged.

- [ ] **Step 5: Document the local-only signing path**

Add only a placeholder `SIGNING_PROPERTIES_FILE=/absolute/path/to/android-keys/consultaendereco-v2-signing.properties` to `local.properties.example` and set the actual path in ignored `local.properties`.

- [ ] **Step 6: Run identity and build checks**

Run: `./gradlew :app:validateV2Identity testDebugUnitTest assembleDebug validateReleaseConfiguration --no-daemon --console=plain`
Expected: identity check passes, debug build passes, and release configuration resolves the v2 upload key without exposing secret values.

### Task 3: Create the Play Console v2 draft

**Files:**
- External: new Play Console app/listing `Consulta Endereço` with package `br.com.helpdev.consultaendereco`.
- External: v2 app-signing certificate and Android developer verification registration state.

**Interfaces:**
- Consumes: Task 2 exact package ID and version identity.
- Produces: a new Play Console app ID, same visible title, Play App Signing enabled with a Google-managed app-signing key, and an available app-signing public certificate.

- [ ] **Step 1: Verify the package is available for this developer account**

In Play Console, create a new app using the exact title and package from the spec. Confirm the package shown before saving. Leave the existing listing unchanged.

- [ ] **Step 2: Complete non-binding listing setup**

Set default language and free/paid state consistent with the existing app; copy relevant store text and graphics only after reviewing them. Do not submit a production release from this step.

- [ ] **Step 3: Confirm Play App Signing configuration**

Configure the new app to use the Google-generated app-signing key and register the public upload certificate from Task 1. Confirm the app-signing and upload certificates are distinct.

- [ ] **Step 4: Confirm package verification/account state**

Check Play Console's Android developer verification and account status for the new package. Record only status and public certificate fingerprints in the local release notes.

- [ ] **Step 5: Handle a legally binding terms screen**

If Play Console requires acceptance of terms, stop and hand the browser to the user. Continue only after the user completes it.

### Task 4: Configure v2 Ads and Maps credentials

**Files:**
- External: new AdMob app and banner unit for the v2 package if required by the package association.
- External: Android Maps API key restricted to the v2 package and Play app-signing certificate.
- Modify local only: `local.properties`
- Modify only if required: `app/build.gradle` or ad configuration code for distinct v2 production IDs.

**Interfaces:**
- Consumes: Task 3 app package and Play app-signing certificate; Task 1 local signing file convention.
- Produces: v2 AdMob App ID, v2 banner unit ID, and a Maps API key with package/certificate restrictions; all production values remain local-only.

- [ ] **Step 1: Inspect existing AdMob association and policy state**

Read the existing app's package association, banner unit, and policy status. Preserve the existing app and unit. Create a distinct v2 app and banner unit if the existing identifiers cannot be associated to the new package.

- [ ] **Step 2: Prepare the v2 Maps key restriction**

Use the v2 package and Play app-signing certificate in the Android application restriction. Prefer a separate key if the old app's key is restricted to its original package. Before creating any new Google Cloud API credential in the browser, request the required action-time confirmation.

- [ ] **Step 3: Store the new production identifiers locally**

Set the v2 Ads App ID, banner unit ID, and Maps key in ignored `local.properties`; do not print the values or copy them into tracked templates.

- [ ] **Step 4: Verify the debug/production split**

Run `./gradlew processDebugManifest processReleaseManifest --no-daemon --console=plain` and inspect generated manifests/resources without revealing values.
Expected: debug uses the official sample Ads IDs; release resolves v2 production IDs and the v2 Maps key.

### Task 5: Build, upload, and validate the internal release

**Files:**
- Output only: `app/build/outputs/bundle/release/app-release.aab` (ignored release artifact).
- External: Play Console internal testing release and tester installation.

**Interfaces:**
- Consumes: Tasks 1–4 verified key, identity, Console app, and production configuration.
- Produces: signed v2 AAB accepted by Play Console and internal-track validation results; no production rollout yet.

- [ ] **Step 1: Run the complete local verification suite**

Run: `./gradlew testDebugUnitTest assembleDebug bundleRelease --no-daemon --console=plain`
Expected: all tests and tasks pass; the AAB is signed by the local upload key.

- [ ] **Step 2: Verify package, version, and signer on the artifact**

Inspect the AAB with `jarsigner` and `bundletool`; verify package `br.com.helpdev.consultaendereco`, `versionCode 1`, and the Task 1 upload certificate. Confirm no secret is embedded or written to logs.

- [ ] **Step 3: Upload to the internal track**

Create an internal testing release with the v2 AAB. Do not start a production rollout.

- [ ] **Step 4: Install from Play and exercise the release flows**

Verify install and launch, CEP lookup and not-found handling, text search, location permission states, map tiles/reverse geocoding, create/edit/delete saved places within v2, back/navigation behavior, and Ads behavior. Confirm no data transfer is implied from the old app.

- [ ] **Step 5: Resolve Play and Ads blockers before rollout**

Review Play validation, Data Safety, content rating, ads declaration, privacy-policy URL, target API, app access, and account verification. Recheck AdMob serving/policy state after the new Play listing is available.

### Task 6: Submit the v2 listing for production

**Files:**
- External: Play Console production release for the v2 listing.

**Interfaces:**
- Consumes: Task 5 successful internal validation and all required declarations.
- Produces: reviewed and published v2 Play listing; original listing remains unchanged.

- [ ] **Step 1: Confirm production readiness**

Recheck the signed artifact, package, Play App Signing certificates, Ads status, Maps restrictions, store listing, and policy declarations.

- [ ] **Step 2: Submit the production release**

Promote the validated v2 artifact through Play Console's review and production rollout. If a legally binding acceptance or unresolved user decision appears, stop for the user's direct action/input.

- [ ] **Step 3: Verify the public listing and post-release services**

Confirm the listing is available under the visible title **Consulta Endereço**, its package is the v2 ID, installation succeeds, and Ads/Maps remain healthy. Record remaining AdMob review or serving delays.
