# 🛍️ ShopIQ — Smart M-Commerce for Android

**A production-grade, Shopify-powered mobile commerce experience built with Jetpack Compose, Apollo GraphQL, and Clean Architecture.**

[Features](#-key-features) • [Architecture](#-architecture--patterns) • [Tech Stack](#-tech-stack) • [Screenshots](#-screenshots) • [Setup](#-installation--setup) • [Contributors](#-contributors)

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-informational?style=for-the-badge)
![Target SDK](https://img.shields.io/badge/Target%20SDK-35-informational?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

---

## 📖 About

**ShopIQ** is a fully-featured m-commerce Android application built on top of the **Shopify Storefront & Admin APIs**. Designed during the intensive 2-week **JETS MobileX Challenge 2026** at **ITI Smart Village**, the app delivers a seamless shopping experience — from product discovery and AI-powered assistance to checkout and payment — all wrapped in a modern, theme-adaptive, bilingual interface.

---

## ✨ Key Features

### 🛒 E-Commerce Workflows

- **Product Catalog** — Browse by brand, category, or collection with shimmer loading states
- **Product Details** — Variant selection (color/size), image gallery, and customer reviews via Shopify Metafields
- **Smart Search** — Debounced search (300ms) with persisted history and multi-state UI
- **Cart Management** — Optimistic UI updates with snapshot-based rollback on API failure
- **Wishlist** — Locally persisted favorites with Room DB
- **Order Flow** — Step-based checkout (Address → Payment → Draft Order → Confirmation)
- **Payment Integration** — Paymob SDK with Cash on Delivery fallback
- **Promo Codes** — Discount application via Shopify Storefront Cart API
- **Order History** — Full order listing with detailed order view

### 🤖 AI Assistant

- **Gemini-Powered Chatbot** — In-app shopping assistant with persisted chat history

### 🌍 Dual-Language Support

- **English & Arabic (RTL)** — Full right-to-left layout support with locale-aware typography (Cairo font for Arabic, default for English)
- Dynamic locale switching from the profile settings

### 🎨 Theme Adaptability

- **Light & Dark Mode** — Complete Material3 color schemes for both themes
- Persistent theme preference via `SharedPreferences`
- System-adaptive status bar and navigation bar styling

### 📍 Location Services

- GPS-based location detection via `FusedLocationProviderClient`
- Interactive **Google Maps Picker** with reverse geocoding
- Address autocomplete with **500ms debounced** place suggestions
- Camera-pan geocoding optimized with **800ms debounce** + coordinate threshold to eliminate redundant API calls

### 🔐 Security & Team DX

- All API keys stored in `local.properties` and injected via `buildConfigField` — zero hardcoded secrets
- **Shared debug Keystore** committed to the repo — teammates pull and build instantly without SHA-1 fingerprint registration

### 💱 Multi-Currency

- Currency selection with real-time exchange rate conversion
- Persisted currency preference

---

## 🏛️ Architecture & Patterns

ShopIQ follows **Clean Architecture** with a strict multi-module separation:

```
┌─────────────────────────────────────────────────┐
│                    :app                          │
│         Application, DI Graph, Manifest         │
├─────────────────────────────────────────────────┤
│               :presentation                     │
│    Compose UI · ViewModels · MVI Contracts      │
│    Navigation 3 · Theme · Util                  │
├─────────────────────────────────────────────────┤
│                 :domain                         │
│     Use Cases · Repository Interfaces           │
│     Models · Business Logic                     │
├─────────────────────────────────────────────────┤
│                  :data                          │
│   Repository Impls · Apollo GraphQL · Ktor      │
│   Room DB · DataStore · Firebase · Location     │
└─────────────────────────────────────────────────┘
```

### Design Patterns

| Pattern | Usage |
|---|---|
| **MVI** (Model-View-Intent) | Every screen uses `Contract` classes with sealed `Intent`, `State` data class, and `SideEffect` sealed class |
| **Repository** | Domain-layer interfaces, data-layer implementations |
| **Use Cases** | 50+ single-responsibility use cases organized by feature |
| **Dependency Injection** | Koin modules: `appModule`, `domainModule`, `dataModule`, `presentationModule` |
| **Optimistic Updates** | Cart quantity changes apply instantly, rollback on API failure via snapshots |
| **Debounce** | Applied to search (300ms), address suggestions (500ms), and map geocoding (800ms) |

---

## 🛠️ Tech Stack

### Core

![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material3](https://img.shields.io/badge/Material3-Dynamic%20Theming-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

### Networking & API

![Apollo GraphQL](https://img.shields.io/badge/Apollo%20GraphQL-4.1.1-311C87?style=for-the-badge&logo=apollographql&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-2.3.12-087CFA?style=for-the-badge&logo=ktor&logoColor=white)
![OkHttp](https://img.shields.io/badge/OkHttp-4.12.0-3E4348?style=for-the-badge&logo=square&logoColor=white)
![Shopify](https://img.shields.io/badge/Shopify%20API-Storefront%20%2B%20Admin-96BF48?style=for-the-badge&logo=shopify&logoColor=white)

### Data & Persistence

![Room](https://img.shields.io/badge/Room-2.8.4-4285F4?style=for-the-badge&logo=android&logoColor=white)
![DataStore](https://img.shields.io/badge/DataStore-1.1.1-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-34.15.0-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

### Navigation & DI

![Navigation 3](https://img.shields.io/badge/Navigation%203-1.1.3-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Koin](https://img.shields.io/badge/Koin-4.0.1-F57C37?style=for-the-badge&logo=koin&logoColor=white)
![KSP](https://img.shields.io/badge/KSP-2.3.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)

### Maps, Location & Payments

![Google Maps](https://img.shields.io/badge/Google%20Maps-19.0.0-4285F4?style=for-the-badge&logo=googlemaps&logoColor=white)
![Maps Compose](https://img.shields.io/badge/Maps%20Compose-6.1.0-34A853?style=for-the-badge&logo=googlemaps&logoColor=white)
![Play Location](https://img.shields.io/badge/Play%20Location-21.4.0-34A853?style=for-the-badge&logo=google&logoColor=white)
![Paymob](https://img.shields.io/badge/Paymob%20SDK-1.9.2-1A73E8?style=for-the-badge)

### Auth & AI

![Google Sign-In](https://img.shields.io/badge/Google%20Sign--In-Credential%20Manager-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Facebook Login](https://img.shields.io/badge/Facebook%20Login-18.2.3-1877F2?style=for-the-badge&logo=facebook&logoColor=white)
![Gemini AI](https://img.shields.io/badge/Gemini%20AI-0.9.0-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)

### Image Loading & UI

![Coil](https://img.shields.io/badge/Coil-2.7.0-2196F3?style=for-the-badge)
![Shimmer](https://img.shields.io/badge/Shimmer-1.4.0-CCCCCC?style=for-the-badge)
![Splash Screen](https://img.shields.io/badge/Splash%20Screen-1.0.1-4285F4?style=for-the-badge&logo=android&logoColor=white)

---

## 📸 Screenshots

> Add your screenshots to the `screenshots/` directory and update the paths below.

### 🌞 Light Theme — English

| Home | Product Details | Cart | Checkout |
|:---:|:---:|:---:|:---:|
| ![Home](screenshots/en_light_home.png) | ![Product Details](screenshots/en_light_product_details.png) | ![Cart](screenshots/en_light_cart.png) | ![Checkout](screenshots/en_light_checkout.png) |

| Search | Orders | Profile | AI Chat |
|:---:|:---:|:---:|:---:|
| ![Search](screenshots/en_light_search.png) | ![Orders](screenshots/en_light_orders.png) | ![Profile](screenshots/en_light_profile.png) | ![AI Chat](screenshots/en_light_ai_chat.png) |

### 🌙 Dark Theme — English

| Home | Product Details | Cart | Checkout |
|:---:|:---:|:---:|:---:|
| ![Home](screenshots/en_dark_home.png) | ![Product Details](screenshots/en_dark_product_details.png) | ![Cart](screenshots/en_dark_cart.png) | ![Checkout](screenshots/en_dark_checkout.png) |

### 🌍 Arabic (RTL) — Light Theme

| الرئيسية | تفاصيل المنتج | السلة | الدفع |
|:---:|:---:|:---:|:---:|
| ![Home](screenshots/ar_light_home.png) | ![Product Details](screenshots/ar_light_product_details.png) | ![Cart](screenshots/ar_light_cart.png) | ![Checkout](screenshots/ar_light_checkout.png) |

### 🌙 Arabic (RTL) — Dark Theme

| الرئيسية | تفاصيل المنتج | السلة | الدفع |
|:---:|:---:|:---:|:---:|
| ![Home](screenshots/ar_dark_home.png) | ![Product Details](screenshots/ar_dark_product_details.png) | ![Cart](screenshots/ar_dark_cart.png) | ![Checkout](screenshots/ar_dark_checkout.png) |

---

## 🚀 Installation & Setup

### Prerequisites

- **Android Studio** Meerkat (2025.1+) or newer
- **JDK 17**
- **Min SDK 26** (Android 8.0+)
- A **Shopify Partner** account with Storefront & Admin API access

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/ShopIQ.git
cd ShopIQ
```

### 2. Configure API Keys

Create or edit `local.properties` in the project root with the following keys:

```properties
# ─── Shopify API ───────────────────────────────────────
shopify.storefront.access.token=YOUR_STOREFRONT_ACCESS_TOKEN
shopify.admin.access.token=YOUR_ADMIN_ACCESS_TOKEN
shopify.api.key=YOUR_API_KEY
shopify.api.secret.key=YOUR_API_SECRET_KEY
shopify.store.domain=YOUR_STORE.myshopify.com

# ─── Google Services ──────────────────────────────────
google.web.client.id=YOUR_GOOGLE_WEB_CLIENT_ID
MAPS_API_KEY=YOUR_MAPS_API_KEY

# ─── Firebase ─────────────────────────────────────────
# Place your google-services.json in the /data module

# ─── Facebook ─────────────────────────────────────────
facebook.app.id=YOUR_FACEBOOK_APP_ID
facebook.client.token=YOUR_FACEBOOK_CLIENT_TOKEN

# ─── Paymob ──────────────────────────────────────────
paymob.public.key=YOUR_PAYMOB_PUBLIC_KEY
paymob.secret.key=YOUR_PAYMOB_SECRET_KEY
paymob.integration.id=YOUR_PAYMOB_INTEGRATION_ID
paymob.api.key=YOUR_PAYMOB_API_KEY

# ─── Gemini AI ────────────────────────────────────────
gemini.api.key=YOUR_GEMINI_API_KEY
```

> ⚠️ **Note:** `local.properties` is git-ignored. Never commit API keys.

### 3. Keystore (Shared Debug)

The project includes a **shared debug keystore** (`app/shared_debug.jks`) so all team members can build without registering individual SHA-1 fingerprints for Firebase and Google Maps. No additional signing setup is required for debug builds.

### 4. Build & Run

```bash
# Sync Gradle
./gradlew build

# Or simply open in Android Studio and click ▶️ Run
```

---

## 📂 Project Structure

```
ShopIQ/
├── app/                          # Application module — DI graph, manifest, shared keystore
├── presentation/                 # UI layer — Compose screens, ViewModels, MVI contracts, theme
│   ├── navigation/               #   Navigation 3 setup (NavDisplay + entryProvider)
│   ├── screens/                  #   Feature screens (17 feature packages)
│   │   ├── home/
│   │   ├── products/
│   │   ├── search/
│   │   ├── cart/
│   │   ├── checkout/ + payment/
│   │   ├── orders/ + orderdetails/
│   │   ├── profile/
│   │   ├── address/
│   │   ├── ai/
│   │   ├── auth/
│   │   ├── wishlist/
│   │   └── ...
│   └── ui/theme/                 #   Material3 Light/Dark color schemes, typography
├── domain/                       # Business logic — Use cases, repository interfaces, models
│   └── usecases/                 #   50+ use cases organized by feature
├── data/                         # Data layer — API clients, DB, repository implementations
│   ├── graphql/
│   │   ├── admin/                #   8 Admin API queries + schema
│   │   └── storefront/           #   12 Storefront API operations + schema
│   └── repositories/             #   Repository implementations
└── gradle/
    └── libs.versions.toml        # Version catalog — single source of truth for dependencies
```

---

## 🧪 GraphQL Schema

The app consumes **two Shopify API services** configured via Apollo Kotlin:

| Service | API | Operations | Purpose |
|---|---|---|---|
| `shopify` | Admin API | 8 queries | Products, Collections, Categories, Reviews |
| `shopify-storefront` | Storefront API | 12 operations | Cart CRUD, Customer Auth, Orders |

---

## 👥 Contributors

| Name | Role |
|---|---|
| **Abdullh Mohamed Abd El Majeed** | Android Developer |
| **Aziza Helmy** | Senior Supervisor & Mentor |

> Built during the **JETS MobileX Challenge 2026** at **ITI Smart Village** (June 27 – July 9, 2026)

---

## 📄 License

```
MIT License

Copyright (c) 2026 ShopIQ Team

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
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

**⭐ Star this repo if you found it useful!**
