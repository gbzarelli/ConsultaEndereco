# Android build and release configuration

The project now builds with the Gradle wrapper. Java and XML sources remain in the legacy `src/` and `res/` folders by design. The build targets API 36 and supports API 24 and newer.

For a production release, copy `local.properties.example` to `local.properties` and configure:

- `MAPS_API_KEY`: a key restricted to the Android Maps SDK, this package name, and the release signing certificate SHA-1.
- `ADMOB_APP_ID`: the existing AdMob app ID for Address Finder Android.
- `ADMOB_BANNER_UNIT_ID`: the existing active banner unit; do not create a duplicate.

`local.properties` is ignored by Git. Debug builds always use Google’s sample AdMob app and banner IDs. Release builds stop before packaging if production configuration is incomplete.

Use `./gradlew testDebugUnitTest assembleDebug` for local validation. Release signing and Play upload require identifying the historical upload key and confirming the Play Console signing configuration first.
