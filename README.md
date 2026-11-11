# MobiStream – Live TV & Streaming

> "Streaming made mobile."

MobiStream is a modern, high-performance Android IPTV streaming application built with **Kotlin**, **Jetpack Compose (Material 3)**, and **AndroidX Media3 ExoPlayer**, following **Clean MVVM Architecture**.

---

## ⚖️ Disclaimer

> **This app does not host or stream any video content directly. It acts as a player for publicly available M3U playlists provided by the open-source iptv-org project.**
>
> All channel streams, logos, and trademarks belong to their respective copyright holders. MobiStream does not own, manage, or rebroadcast any television channels or content.

---

## 📱 Features

- **Media3 ExoPlayer Streaming**: Native HLS (`.m3u8`) low-latency live video player with on-screen swipe gestures (brightness on left, volume on right), auto landscape fullscreen, and stream health telemetry.
- **Accurate EPG Programming**: 24-hour electronic program guide matching authentic channel schedules (History TV18, PTC News, PTC Punjabi, Aaj Tak, Sports, Movies, Music, Kids).
- **Auto Focus Return Navigation**: Remembers and automatically scrolls the channel grid directly to the channel being watched upon exiting the player.
- **Personalization & Onboarding**: First-install onboarding prompting for permissions, existing backup check, up to 5 preferred languages (Punjabi priority, Hindi, English, etc.), and favorite interest categories.
- **Channels You May Like**: Curated personalized section displayed directly on Home based on chosen languages and categories.
- **Backup & Cloud Sync**:
  - **Google Drive Backup**: One-tap sync for favorites, pinned channels, and user preferences across devices.
  - **Portable JSON File Backup**: Import and export channel lists and settings via Android Storage Access Framework (SAF).
- **Adaptive Contrast Logos**: High-contrast adaptive tiles ensuring channel logos remain sharp and legible in both Midnight Dark and Cloud Light modes.
- **Dynamic 5-Slide Hero Carousel**:
  - Live News program with current broadcast backdrop (strictly first slide)
  - Live sports broadcasts with stadium action visuals
  - Rotating feature shows (refreshed every 30 minutes)
- **Device Location Awareness**: Automatically detects device country and prioritizes channels from that country, sorted by user's preferred languages.
- **Pinned & Favorites**: Pin priority channels to the top of Home and maintain an offline Room database favorites list.
- **Deep Exploration System**: Explore 11,000+ channels by Category (distinct icons for all 25+ categories), Country (A-Z), and Language (native script characters: ੴ, अ, Aa, اردو, etc.).
- **Global Instant Search & Multi-Filters**: Filter by category, country, language, quality (HD/FHD/SD), and live search keywords.
- **Theme Support**: Seamless toggle between Cloud Light and Midnight Dark (`#0E0A24`).

---

## 🛠️ Architecture & Tech Stack

- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** Model-View-ViewModel (MVVM) + Unidirectional Data Flow (StateFlow)
- **Video Engine:** AndroidX Media3 ExoPlayer + HLS extension
- **Database:** AndroidX Room Database with KSP
- **Networking:** Retrofit2 + OkHttp3 + Moshi
- **Image Loading:** Coil Compose
- **Typography:** Google Font Inter (bundled locally)
- **Permissions:** Runtime Location & Notification Permissions with graceful fallbacks
- **Data Portability:** Android Storage Access Framework (SAF) JSON import/export

---

## 📦 APK Download

Pre-built APKs are available in the repository:
- `apk/mobistream-v1.0.5.apk` (Latest: v1.0.5, Build 105)
- `apk/mobistream-v1.0.4.apk`
- `apk/mobistream-v1.0.3.apk`
- `apk/mobistream-v1.0.2.apk`
- `apk/mobistream-v1.0.1.apk`
- `apk/mobistream-v1.0.0.apk`

---

## 📄 MIT License

```
MIT License

Copyright (c) 2026 malhi3296 (https://github.com/malhi3296/)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
