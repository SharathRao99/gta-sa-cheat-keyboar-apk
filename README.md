# SA CheatBoard

Personal-use helper for GTA San Andreas on Android: searchable cheat list, floating cheat overlay,
and a compact keyboard (IME) that types codes as real key events. No internet permission, no ads, no libraries.

## Install (no PC needed)
Open the **Releases** page of this repo on your phone, download `SA-CheatBoard.apk`, and install it
(allow "install unknown apps" for your browser when asked). It is a debug-signed build of the source in this repo.

## Build yourself
1. Open this folder in Android Studio (Giraffe or newer), let Gradle sync.
2. Run on your phone (USB debugging), or Build > Build APK(s) and sideload `app-debug.apk`.

## Setup on the phone
1. Open the app > **1 Overlay permission** > allow.
2. **2 Enable keyboard** > turn on "SA CheatBoard keyboard".
3. In the game, tap a text field / open the game's keyboard prompt, or use **3 Switch keyboard** to select SA CheatBoard.
4. **4 Start overlay** > the "SA" bubble appears over the game. Drag by its header, tap SA to expand, tap a cheat to type it.

## Behavior
- Tap = type the full cheat phrase (the mobile version uses phrases, not the short PC codes).
- Status line in the overlay says "keyboard connected" when the game accepts input, otherwise taps copy the code to the clipboard.
- Star a cheat to add it to the ★ tab.

## Debugging with logcat
Connect the phone to a computer (USB debugging) and filter by tag: `adb logcat -s SACheatBoard`
(Android Studio: Logcat > filter `tag:SACheatBoard`). Package name: `com.sharath.sacheatboard`.

## Known limits
- Android only lets a keyboard (IME) send keys. If the game never requests text input, the keyboard may not attach; then
  use a Bluetooth/USB keyboard with the overlay as a reference list.
- Some cheats can fail missions or disable achievements. Notes are shown in the list.
- A few codes differ between sources; if one doesn't work, try the other variant. "Cheat Activated" confirms success.
