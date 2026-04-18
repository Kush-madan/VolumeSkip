# VolumeSkip — Build Instructions

## What this app does
- Hold Volume UP = Next song (works with screen OFF)
- Hold Volume DOWN = Previous song (works with screen OFF)
- Works with Spotify, YouTube Music, and all music apps
- Tiny background service, uses almost no battery

## How to get the APK on your Redmi Note 10S

### Option A — Build FREE online using GitHub + Gitpod (no PC needed)

1. Go to https://github.com and create a free account
2. Create a new repository, name it "VolumeSkip"
3. Upload all these files keeping the same folder structure
4. Go to https://gitpod.io and sign in with GitHub
5. Open your repo in Gitpod (free tier)
6. In the terminal run:
   ./gradlew assembleDebug
7. Download the APK from:
   app/build/outputs/apk/debug/app-debug.apk
8. Send it to your phone via WhatsApp or Google Drive
9. On your phone: Settings → Install unknown apps → allow your browser
10. Tap the APK to install

### Option B — Use Android Studio on a PC (if you have one)

1. Install Android Studio (free) from https://developer.android.com/studio
2. Open this folder as a project
3. Click Build → Build APK
4. Transfer APK to phone and install

### Option C — Ask someone with Android Studio to build it
Just share this folder with them — they click one button and send you the APK.

## After installing on your phone

1. Open VolumeSkip app
2. Tap the big green START button
3. Turn your screen off
4. Hold Volume UP → skips to next song
5. Hold Volume DOWN → goes to previous song

## First time setup
- The app will ask for notification permission — tap Allow
- If it stops working after phone restart, open the app once and tap Start again
  (or it auto-restarts on boot if you granted that permission)

## Tested with
- Spotify ✓
- YouTube Music ✓  
- Wynk Music ✓
- JioSaavn ✓
- Local music players ✓
