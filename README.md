# TeslaPhoneHUD

Android app that turns your phone into a Head-Up Display (HUD) for Tesla vehicles by placing it on the dashboard.

## Features

- Connects to Tesla vehicle via the unofficial Tesla API (owner-api or Fleet API)
- Displays:
  - Current speed
  - Navigation: destination, time remaining, distance remaining, arrival time
  - Vehicle metrics: battery %, range (toggleable)
  - Heading / compass
- Black background with high-contrast bright primary colors (green/cyan/white) for optimal HUD reflection
- Horizontally mirrored display so the reflection on the windshield appears correctly oriented
- Fullscreen immersive mode, keep screen on
- Option to show/hide vehicle metrics panel

## Limitations

- Full turn-by-turn instructions and real-time next maneuver are not fully exposed by the Tesla API. The app shows route summary from `active_route_*` fields in `drive_state`.
- Speed limits and speed camera warnings require additional data sources (e.g., phone location + OSM/Overpass or commercial APIs). Currently stubbed for future integration.
- Requires a valid Tesla access token. Obtaining tokens involves Tesla OAuth (client ID/secret are restricted; many use reverse-engineered flows or third-party tools).
- Polling `vehicle_data` is rate-limited; for production use Fleet Telemetry streaming is preferred.

## Setup

1. Clone the repo.
2. Open in Android Studio (Giraffe or later recommended).
3. Sync Gradle.
4. Obtain a Tesla access token (see below).
5. Enter token in the app settings and select your vehicle.
6. Place phone on dashboard face-up toward windshield, enable HUD/mirror mode.
7. Drive (responsibly — do not interact while driving).

## Getting a Tesla Access Token

Tesla does not publicly offer easy app developer registration for personal use. Common approaches:

- Use tools like TeslaAuth, or browser developer tools while logged into Tesla account to capture the access token from the official app/web.
- Refer to unofficial documentation: https://tesla-api.timdorr.com/
- For Fleet API: register at developer.tesla.com (may require business use).

**Warning**: Using unofficial APIs may violate Tesla's terms. Use at your own risk. Tokens expire; refresh logic is partially implemented.

## Building

- Min SDK: 26
- Target SDK: 34
- Language: Kotlin + Jetpack Compose

## Disclaimer

This is an independent open-source project. Not affiliated with Tesla, Inc. Drive safely. The app is for informational purposes; rely on the vehicle's own displays for critical information.

## License

MIT
