# SA CheatBoard

Personal-use helper for GTA San Andreas on Android: searchable cheat list, floating cheat overlay,
and a compact keyboard (IME) that types codes as real key events. No internet permission, no ads, its only library is the open-source Shizuku API.

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
- Tap = type short code, long-press = type the other variant (phrase). Switch default in the app (button at the top).
- Status line in the overlay says "keyboard connected" when the game accepts input, otherwise taps copy the code to the clipboard.
- Star a cheat to add it to the ★ tab.

## Make cheats actually work in the game (Shizuku, Android 11+)
The game ignores normal keyboard apps, so the app can send real key events through Shizuku:
1. Install **Shizuku** (Play Store, or its official GitHub releases).
2. Settings > Developer options > turn on **Wireless debugging**, connect to Wi-Fi.
3. Open Shizuku > "Start via Wireless debugging" > pair using the pairing-code notification.
4. In SA CheatBoard tap **5 Shizuku** and allow access. The overlay status will read "Shizuku ready".
5. Open the game, expand the overlay, tap a cheat. Repeat step 3 start after each phone reboot.

## Known limits
- Android only lets a keyboard (IME) send keys. If the game never requests text input, the keyboard may not attach; then
  use a Bluetooth/USB keyboard with the overlay as a reference list.
- Some cheats can fail missions or disable achievements. Notes are shown in the list.
- A few codes differ between sources; if one doesn't work, try the other variant. "Cheat Activated" confirms success.
