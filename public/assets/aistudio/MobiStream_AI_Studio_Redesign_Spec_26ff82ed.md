# MobiStream v1.0.4 – UI Redesign Brief (paste into Google AI Studio)

## Instruction to the AI
Redesign ONLY the look of every screen. Do NOT change features, logic, data, navigation order, permissions, or any text. Keep all existing strings exactly. Support Dark and Light themes (user toggle + system default). Use the tokens below. Attached assets: `logo-blue-512.png` (app logo), `upi-qr.png` (UPI QR for the Developer Profile screen).

## Design tokens
| Token | Dark | Light |
|---|---|---|
| background | #161C2A | #F6F9FE |
| surface/card | #212B3D | #FFFFFF |
| border | #33415C | #D3E2F4 |
| selected surface | #1D3558 | #E3F0FF |
| accent | #3B9BF5 | #1473D6 |
| accent text/link | #7FBFFF | #0B5CB5 |
| text | #F1F5FB | #0E2342 |
| muted text | #B8C3D6 | #51627C |
| text on accent | #06152B | #FFFFFF |
| nav pill | #243049 | #FFFFFF |
| live badge | #E0263F with white text | same |
| hero/image gradient | #1E6FD9 → #0F2F5C, white text | same |
Channel tile colours: #1E6FD9 #0E7C86 #3B5BDB #0B5FA5 #2563EB #0F766E #4F46E5 #0369A1 #1D4ED8 (white initials).
Font: Inter. Radius: cards 18, buttons 16, chips and search 999, icon buttons 14. Minimum tap target 44px. Line icons, no emoji.

## Shared components
- Floating pill bottom nav (inset 14px, 68px high, radius 34): Home, Explore, Favourite, Profile.
- Search pill with round accent menu button (Home, Explore).
- Chips (selected = accent fill), cards, accent primary button, tonal secondary button, toggle switch.
- Top bar: logo + "MOBISTREAM" / "Streaming made mobile", buttons: theme, filter, settings.

## Screens (keep every label)
1. **Splash** – logo, MOBISTREAM, "Streaming made mobile", progress bar.
2. **Onboarding 1** "Choose App Appearance" – Dark Mode (Recommended): "Midnight purple styling optimized for low-light streaming" (rename colour wording only if the app text changes); Light Mode: "High-contrast clean and crisp daytime interface". Button "Next: Choose Languages".
3. **Onboarding 2** "Choose Preferred Languages" – "Pick up to 5 languages. Live TV streams in your chosen languages will be prioritized at the top of your feed." Chips: Hindi, English, Punjabi, Urdu, Tamil, Telugu, Bengali, Malayalam, Kannada, Marathi, Gujarati, Arabic, Spanish, French, German, Russian. Button "Next: Choose Categories".
4. **Onboarding 3** "What do you love watching?" – Chips: Music, Movies, Sports, News, Series, Animation, Kids, Documentary, Comedy, Entertainment, Education, Auto, Business, Lifestyle, Science, Travel, Cooking, Weather. Button "Next: Permissions & Backup".
5. **Onboarding 4 / Permissions** "Ready to Stream" – Device Permissions: Location ("Automatically shows top TV channels from your country"), Notifications ("Stream updates and scheduled sports / live events"); "Have an existing backup?" – "Import Existing Backup (.json)"; button "Start Watching MobiStream".
6. **Home** – search "Search 1000+ live channels, sports, news..."; hero banner with LIVE + "Play Now"; "Pinned & Favourites" (empty: "Pin your favorite live channels" / "Tap the pin or heart icon on any channel to pin it here at the top."); "Popular in <country>"; "Channels You May Like"; "Popular Global Channels"; "Trending Now"; Categories chips; card "Discover 11,000+ Channels" + "Explore All Channels".
7. **Explore** – "Search 11,000+ channels by name..."; Categories, Countries, Languages sections with "See all".
8. **Search** – "Artists, songs, channels or live streams..."; category chips; result rows with LIVE badge and favorite heart.
9. **Favourites** – "My Favorites", "N channels saved in offline database", "Category Folders" chips, channel rows.
10. **Player (portrait)** – video area with Back, LIVE, FHD, **Lock**, **Favorite**, centre Pause, CC, Aspect ratio, Fullscreen; below: NOW PLAYING, channel name, Mute, Favorite, programme title + description + progress, "Related Live Channels".
11. **Player (full screen, landscape)** – Back + channel name + LIVE/FHD top-left; Favorite, Lock, CC, Aspect ratio, Exit fullscreen top-right; centre Pause; left brightness slider and right volume slider (existing swipe gestures); programme info + progress at the bottom.
12. **Player locked** – all controls and gestures disabled; only an Unlock button (right edge) and the hint "Controls locked · tap the lock to unlock".
13. **Settings & Preferences** – sections: Appearance (Midnight Dark Mode, Cloud Light Mode), Streaming Playback (Buffering Latency: Low Latency / Stable Buffer), Data & Storage (Refresh Channel Playlists, Sync Now), Personalization (Preferred Languages up to 5, Favorite Categories), Backup & Restore (Export File / Import File), About MobiStream (Version 1.0.4 (Build 104), Developer Profile – Open Link, IPTV Data Source: iptv-org).
14. **Developer Profile** – Arwinder Singh, Android & Streaming Systems Engineer, GitHub, PayPal; Library Overview (Channels, Favorites, Pinned); "Scan & Support via UPI" with `upi-qr.png`, UPI ID 8288909927@airtel, "Copy UPI ID"; Device Backup & Restore (Export Backup / Import Backup); About MobiStream text; "Version 1.0.4 (Build 104) • Streaming made mobile."

## Player behaviour to wire
- Favorite: toggles the existing favourite state (filled red heart = saved); same state everywhere.
- Lock: UI-only state; hides controls and ignores touch/gestures until Unlock is tapped. Keep screen awake and playback running.
- Fullscreen: rotate to landscape; Exit fullscreen returns to portrait.
