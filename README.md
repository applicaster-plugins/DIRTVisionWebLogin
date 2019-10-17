# Inplayer Login

## Getting Started

Setup your Plugin dev environment as described here: https://developer-zapp.applicaster.com/dev-env/android.html

Clone the project from github, cd to the Android folder, and open in Android Studio

## Description

There is 1 plugins with corresponding gradle modules:

- `inplayer-login-plugin`: authentication logic & UI;

`app` module is used as a local sample that links both login and viewer plugins via the configuration json.
 
## Deployment

1. Update version for desired module to deploy:
```
$MODULE/gradle.properties
$MODULE/plugin-manifest.json
```
2. Deploy from the shell
```
./gradlew --no-daemon $MODULE:build $MODULE:bintrayUpload --no-configure-on-demand --no-parallel && zappifest publish --manifest $MODULE/plugin-manifest.json --access-token $YOUR_ZAPP_ACCESS_TOKEN
```
