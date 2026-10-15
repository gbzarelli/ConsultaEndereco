# Android modernization implementation plan

Spec: `docs/superpowers/specs/android-modernization.md`

## Global constraints
- Preserve package ID, Play listing, saved data format, and all named app flows.
- Keep Java/XML UI; do not convert to Compose.
- Target API 36, minimum API 24.
- Do not commit keystores, passwords, Maps keys, AdMob App ID, or release artifacts.
- Do not submit/publish to Play until the historical signing certificate and production app identifiers are verified.

## Task 1 — Establish a reproducible Android Gradle build
- Add Gradle Wrapper, root settings/build files, and an Android application module configured for existing `src/`, `res/`, and manifest paths.
- Set `applicationId` to `zarelli.biff.consultaendereco`, `minSdk 24`, `compileSdk/targetSdk 36`, and raise version code above 7 only in the release-ready final task.
- Migrate Support Library imports and XML widget names to AndroidX; add AppCompat, Fragment, DrawerLayout, Google Maps, fused-location, and Google Mobile Ads dependencies.
- Replace deprecated `ActionBarActivity`/drawer APIs with supported AndroidX equivalents; retain screen behavior.
- Expected: `./gradlew testDebugUnitTest` and `./gradlew assembleDebug` pass.

## Task 2 — Fix and test CEP lookup
- Add unit tests first for formatted/unformatted CEP input, invalid input, ViaCEP JSON, BrasilAPI JSON, not-found handling, provider fallback, and address formatting.
- Replace the HTTP/XML endpoint with HTTPS requests, explicit connect/read timeouts, UTF-8 JSON parsing, and a fallback provider.
- Return a no-results state for unknown CEPs; preserve the existing error state for network failures.
- Expected: focused CEP tests and the full debug unit-test task pass.

## Task 3 — Modernize location and Maps
- Replace `LocationClient` with `FusedLocationProviderClient`; request location only after runtime permission is granted and handle coarse-only, denied, disabled, and unavailable Google Play services states.
- Replace deprecated map fragment access with `getMapAsync` and preserve the current location, camera, refresh, reverse-geocoding, and favorite behavior.
- Move the Maps key from tracked resources to ignored local configuration; keep a harmless placeholder for debug builds without a configured key and require a real value for release builds.
- Expected: debug build passes; emulator checks verify map rendering, permission flow, location refresh, and save-to-favorites.

## Task 4 — Modernize AdMob integration
- Initialize the current Google Mobile Ads SDK once in the application lifecycle; supply the official sample App ID/unit IDs to debug builds and existing production IDs through local configuration.
- Add robust banner load/failure behavior and lifecycle forwarding; avoid loading production ads during development tests.
- Verify the existing AdMob app, unit, App ID, and `Play App Disabled` issue in AdMob; do not create duplicate units.
- Expected: debug build passes and emulator test banners load without crashing; production identifiers are validated before release.

## Task 5 — Android 16 behavior and full regression validation
- Add/update automated tests for CEP and any extracted pure logic.
- Run unit tests, assemble debug/release, lint only if configured, and install on API 35; use API 36 emulator for edge-to-edge and predictive-back checks if available.
- Manually check all flows listed in the spec, including process recreation and favorites persistence.
- Expected: clean build and regression checklist; record any unavailable hardware/account-only verification explicitly.

## Task 6 — Prepare and verify Play Console release prerequisites
- Determine the original signing certificate from the historical key/release and register its SHA-256 public certificate for Android developer verification.
- Confirm developer account verification and Play listing publication state; confirm whether Play App Signing enrollment is appropriate while preserving update compatibility.
- Refresh Play policy/data-safety/store declarations, create a signed AAB with `versionCode > 7`, and validate it with bundle/signing checks.
- Test through internal track before production. Submit/publish only after prerequisites and user-authorized security-sensitive steps are confirmed.
- After Play reactivation, verify app status and wait/check AdMob policy recovery; follow up with AdMob support if restriction remains beyond the documented sync window.
