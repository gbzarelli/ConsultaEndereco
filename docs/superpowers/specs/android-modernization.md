# Consulta Endereço Android modernization spec

## Goal
Restore a buildable, maintainable Android release of Consulta Endereço that preserves the app's existing user-facing flows and can be submitted to Google Play under the existing package.

## Product behavior to preserve
- Search for addresses by text using Android Geocoder.
- Search Brazilian CEPs and display the resulting street, city, state, CEP, and country.
- Show the user's current location on Google Maps, resolve its address, and let the user save it to My Places.
- Create, edit, list, search, and remove saved places.
- Keep the current Portuguese and English resource sets and existing navigation layout.
- Keep banner ads, using the existing AdMob app and ad units once their production identifiers are verified.

## Technical baseline
- Keep Java and XML layouts; migrate from ADT/Android Support libraries to Gradle and AndroidX.
- Preserve application ID `zarelli.biff.consultaendereco` and the existing Play listing.
- Set `minSdk` to 24 and compile/target API 36.
- Use maintained Google Mobile Ads, Maps, and fused location SDKs.
- Debug builds use Google's sample AdMob app/unit IDs. Production IDs and the Maps key must come from local, ignored configuration; never commit credentials.
- Replace the Republica Virtual HTTP/XML CEP integration with ViaCEP HTTPS JSON and BrasilAPI HTTPS fallback.

## Acceptance criteria
- `./gradlew testDebugUnitTest` and `./gradlew assembleDebug` pass.
- The debug app installs and launches on the available API 35 emulator.
- Automated tests cover CEP normalization, both response formats, not-found responses, provider fallback, and formatted output.
- Manual/emulator checks cover text address search, CEP search, map/current location permission flow, saved-place persistence/edit/remove, navigation/back behavior, and test banner lifecycle.
- App bundle keeps the original package and increments `versionCode` beyond 7.
- Before production upload, the original release certificate is confirmed; Android developer verification registration is completed; Play Console app/signing state is resolved; Maps and AdMob production identifiers are confirmed; Play and AdMob policy status is clear.

## Explicit constraints and unresolved release inputs
- The Play Console says this app's releases are not signed by Play. The Android developer verification package record is Registered/Verified, but its certificate has not been matched to the historical release signer. The original signing certificate must be identified before any upload.
- `key_zarelli` and `key_criar` in `../android-keys` did not unlock with the candidates in `pass.txt`. Do not upload a private keystore key to any service without a separate action-time confirmation.
- The AdMob app and banner unit already exist, but the SDK App ID could not be read from the current AdMob settings page. Do not create duplicate app/ad units.
- The AdMob Policy Center reports `Play App Disabled`; confirm recovery after the Play listing is active again.
- The existing Maps key is hardcoded in the legacy resource file. Move it to local configuration and verify/restrict it before production.
- Do not submit or publish a release until signing, policy, and production configuration prerequisites are verified.
