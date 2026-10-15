# Consulta Endereço v2 publication specification

## Goal

Publish a new Google Play app named **Consulta Endereço** under a new package identity, `br.com.helpdev.consultaendereco`, because the private key for the existing pre-Play-App-Signing listing has not been recovered. Preserve the old listing and its installed users. Use Play App Signing for the new app and keep only a separate upload key under the local `android-keys` directory. The user approved this package change after the initial v2 package was selected; Play Console confirmed it is available.

## User-approved decisions

- Create a separate app/listing with `applicationId` `br.com.helpdev.consultaendereco`.
- Keep the visible app title **Consulta Endereço**.
- Leave the current listing and package `zarelli.biff.consultaendereco` intact.
- Do not migrate old app data automatically; saved places remain in the old app's private storage.
- Use Google Play App Signing for the new app. Google manages the app-signing key; the developer holds a distinct local upload key.
- Store the upload keystore, password file, public upload certificate, and path/alias metadata in `/Volumes/gbz-ext/repositories/helpdev/android-keys`, following its existing conventions.

## App identity and build

- Set both Gradle `applicationId` and `namespace` to `br.com.helpdev.consultaendereco`; move Java source and test packages to match. Keep the old listing at `zarelli.biff.consultaendereco` unchanged.
- Start the new package's version sequence at `versionCode 1`; retain user-facing version name `3.0` unless Play Console validation requires a different value.
- Keep the existing modernization baseline (Java/XML, AndroidX, `minSdk 24`, `compileSdk/targetSdk 36`) and existing CEP behavior.
- Release builds use the local upload key. Debug builds keep Google's sample ad IDs and debug package suffix.
- Do not include signing secrets or production identifiers in Git.

## Signing-key handling

- Generate a strong local upload keystore and a private key alias specifically for Consulta Endereço v2.
- Match the key-folder convention with a named `.jks`, a hidden `.pass` file, a public upload certificate `.pem`, and a properties file that contains file paths and alias but no literal password.
- Restrict local secret file permissions and add only the new files to the key repository's local `.git/info/exclude`; preserve its existing dirty state and do not stage or commit key material.
- Register the public upload certificate in Play Console when configuring the new app. Never upload the private upload keystore.
- Allow Play Console to generate/manage the app-signing key for the new app; retrieve and record the public app-signing certificate after enrollment for Maps/API restrictions and future verification.
- Stop before accepting legally binding Console terms; the user must complete that acceptance.

## Ads, Maps, and developer verification

- Create a separate AdMob app record and banner unit for the v2 package if the existing app/unit cannot be correctly associated with the new package. Preserve the existing AdMob app and unit.
- Populate production AdMob IDs only in ignored local configuration. Confirm app association, serving status, test-banner behavior, and policy status before release.
- Restrict the Maps SDK key to the v2 package plus the Play app-signing certificate. Use a separate key when editing the existing key would broaden or risk the old app's access.
- Confirm package registration and developer account verification in Play Console for the new app.

## Play listing and rollout

- Create a new Play Console app with the same display name and the new package.
- Recheck store listing content, target API requirement, countries, app access, ads declaration, content rating, data safety, privacy policy, and other current Play declarations.
- Prepare a signed AAB and verify the package, version code, upload certificate, and Play App Signing configuration.
- Use internal testing first; verify install/update, app flows, CEP lookup, Maps/location, favorites, and test/production ad configuration before production rollout.
- Do not alter or unpublish the old listing as part of this work.

## Consequences and limitations

- The v2 app is a separate Play product. Existing users will not receive it as an in-place update, and installs, reviews, ratings, and local saved places do not transfer automatically.
- Existing ad, Maps, Play policy, and developer-verification states may need new v2-specific configuration; the old app's state does not establish readiness for v2.
- If Play Console presents a legally binding agreement, the user must accept it directly before work continues past that screen.
- Browser-driven publication and production rollout remain gated on a verified signed artifact and successful internal-track validation.

## References

- Android Developers, Sign your app: https://developer.android.com/studio/publish/app-signing
- Google Play Console Help, Use Play App Signing: https://support.google.com/googleplay/android-developer/answer/9842756?hl=en
