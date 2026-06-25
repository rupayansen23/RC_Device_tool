# H12 Pro USB Debug Testing

Use this project like a normal Android app running directly on the H12 Pro controller.

## H12 Pro Setup

1. Open Android settings on the H12 Pro.
2. Open About device.
3. Tap Build number 7 times.
4. Open Developer options.
5. Enable USB debugging.
6. Connect the H12 Pro to the PC with a USB data cable.
7. Accept the RSA debugging prompt on the H12 Pro screen.

## Install And Launch

From the project root:

```powershell
.\scripts\install-h12pro.ps1
```

Manual commands:

```powershell
adb devices
.\gradlew.bat :app:installDebug
adb shell am start -n com.example.devicetoolv1.debug/com.example.devicetoolv1.MainActivity
```

## Notes

- The app is locked to landscape for controller-style use.
- The screen stays awake while the app is open.
- Debug builds install as `com.example.devicetoolv1.debug`, so they can coexist with a future release build.
- USB debugging only installs and launches the app. Accessing H12 Pro transmitter internals still requires an SDK, service, serial device, or protocol exposed by the controller firmware.
