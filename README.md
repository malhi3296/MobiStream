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

- **Media3 ExoPlayer Streaming**: Native HLS (`.m3u8`) low-latency live video player with custom touch controls, fullscreen toggle, aspect ratio controls (Fit/Zoom/Fill), and stream health monitoring.
- **Dynamic Slideshow**:
  - Live News program showcase with breaking news backdrop
  - Major global live sports broadcasts with stadium action visuals
  - Rotating trending programs and shows
- **Device Location Awareness**: Automatically detects device country and localizes content to highlight channels popular in your country.
- **Pinned & Favorites**: Pin your top-priority channels and save unlimited favorites with offline Room Database persistence.
- **Deep Filter System**: Filter thousands of global channels by Country, Language, and Category/Genre.
- **Global Instant Search**: Search channels, networks, and genres in real time.
- **Theme Support**: Seamless switching between clean light mode and Midnight Dark mode (`#0E0A24`).
- **Offline Persistence**: Room database caching channels, favorites, pinned states, and playback settings across app restarts.

---

## 🛠️ Architecture & Tech Stack

- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** Model-View-ViewModel (MVVM) + Unidirectional Data Flow (StateFlow)
- **Video Engine:** AndroidX Media3 ExoPlayer + HLS extension
- **Database:** AndroidX Room Database with KSP
- **Networking:** Retrofit2 + OkHttp3 + Moshi
- **Image Loading:** Coil Compose
- **Typography:** Google Font Inter (bundled locally)
- **Permissions:** Runtime Location Permissions for country-aware channel discovery

---

## 📦 APK Download

Pre-built APK is available in the repository:
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
