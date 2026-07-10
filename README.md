<div align="center">

# 🛍️ ShopIQ — Smart M-Commerce for Android

**A production-grade, Shopify-powered mobile commerce experience built with Jetpack Compose, Apollo GraphQL, and Clean Architecture.**

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](#)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-informational?style=for-the-badge)](#)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35-informational?style=for-the-badge)](#)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](#)

[Features](#-key-features) • [Architecture](#-architecture--patterns) • [Tech Stack](#-tech-stack) • [Screenshots](#-screenshots) • [Setup](#-installation--setup) • [Contributors](#-contributors)

</div>

---

## 📖 About

**ShopIQ** is a fully-featured m-commerce Android application built on top of the **Shopify Storefront & Admin APIs**. Designed during the intensive 2-week **JETS MobileX Challenge 2026** at **ITI Smart Village**, the app delivers a seamless shopping experience — from product discovery and AI-powered assistance to checkout and payment — all wrapped in a modern, theme-adaptive, bilingual interface.

---

## ✨ Key Features

### 🛒 E-Commerce Workflows
* **Product Catalog:** Browse by brand, category, or collection with shimmer loading states.
* **Product Details:** Variant selection (color/size), image gallery, and customer reviews via Shopify Metafields.
* **Smart Search:** Debounced search (300ms) with persisted history and multi-state UI.
* **Cart Management:** Optimistic UI updates with snapshot-based rollback on API failure.
* **Wishlist:** Locally persisted favorites with Room DB.
* **Order Flow:** Step-based checkout (Address → Payment → Draft Order → Confirmation).
* **Payment Integration:** Paymob SDK with Cash on Delivery fallback.
* **Promo Codes:** Discount application via Shopify Storefront Cart API.
* **Order History:** Full order listing with detailed order view.

### 🤖 AI Assistant & 💱 Multi-Currency
* **Gemini-Powered Chatbot:** In-app shopping assistant with persisted chat history.
* **Dynamic Currency:** Real-time exchange rate conversion and persisted currency preferences.

### 🌍 Localization & 🎨 Theme Adaptability
* **Dual-Language (En/Ar):** Full right-to-left (RTL) layout support with locale-aware typography (Cairo font for Arabic, default for English).
* **Dynamic Theming:** Complete Material3 Light & Dark color schemes that respect system preferences, including adaptive status and navigation bars.

### 📍 Location Services
* GPS-based location detection via `FusedLocationProviderClient`.
* Interactive **Google Maps Picker** with reverse geocoding.
* Address autocomplete with **500ms debounced** place suggestions.
* Camera-pan geocoding optimized with **800ms debounce** and coordinate thresholds to eliminate redundant API calls.

### 🔐 Security & Team DX
* API keys safely stored in `local.properties` and injected via `buildConfigField` — zero hardcoded secrets.
* **Shared debug Keystore** committed to the repo, allowing teammates to pull and build instantly without SHA-1 fingerprint registration.

---

## 🏛️ Architecture & Patterns

ShopIQ strictly follows **Clean Architecture** with multi-module separation to ensure scalability and maintainability:

```text
┌─────────────────────────────────────────────────┐
│                    :app                         │
│         Application, DI Graph, Manifest         │
├─────────────────────────────────────────────────┤
│                :presentation                    │
│    Compose UI · ViewModels · MVI Contracts      │
│    Navigation 3 · Theme · Util                  │
├─────────────────────────────────────────────────┤
│                  :domain                        │
│      Use Cases · Repository Interfaces          │
│      Models · Business Logic                    │
├─────────────────────────────────────────────────┤
│                   :data                         │
│    Repository Impls · Apollo GraphQL · Ktor     │
│    Room DB · DataStore · Firebase · Location    │
└─────────────────────────────────────────────────┘
```

### Design Patterns

| Pattern | Usage |
| :--- | :--- |
| **MVI (Model-View-Intent)** | Every screen uses `Contract` classes with sealed `Intent`, `State` data class, and `SideEffect` sealed class. |
| **Repository Pattern** | Domain-layer interfaces acting as boundaries for data-layer implementations. |
| **Use Cases** | 50+ single-responsibility use cases organized tightly by feature. |
| **Dependency Injection** | Koin modules separated cleanly by layer: `appModule`, `domainModule`, `dataModule`, `presentationModule`. |
| **Optimistic Updates** | Cart quantity changes apply instantly to the UI; rollback triggered on API failure via state snapshots. |
| **Debounce Optimization** | Strategically applied to search (300ms), address suggestions (500ms), and map geocoding (800ms). |

---

## 🛠️ Tech Stack

### Core Frameworks
![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material3](https://img.shields.io/badge/Material3-Dynamic%20Theming-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)

### Networking & Data
![Apollo GraphQL](https://img.shields.io/badge/Apollo%20GraphQL-4.1.1-311C87?style=for-the-badge&logo=apollographql&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-2.3.12-087CFA?style=for-the-badge&logo=ktor&logoColor=white)
![Room](https://img.shields.io/badge/Room-2.8.4-4285F4?style=for-the-badge&logo=android&logoColor=white)
![DataStore](https://img.shields.io/badge/DataStore-1.1.1-4285F4?style=for-the-badge&logo=android&logoColor=white)

### Architecture & DI
![Koin](https://img.shields.io/badge/Koin-4.0.1-F57C37?style=for-the-badge&logo=koin&logoColor=white)
![Navigation 3](https://img.shields.io/badge/Navigation%203-1.1.3-4285F4?style=for-the-badge&logo=android&logoColor=white)
![KSP](https://img.shields.io/badge/KSP-2.3.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)

### Integrations
![Google Maps](https://img.shields.io/badge/Google%20Maps-19.0.0-4285F4?style=for-the-badge&logo=googlemaps&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-34.15.0-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Gemini AI](https://img.shields.io/badge/Gemini%20AI-0.9.0-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)
![Paymob](https://img.shields.io/badge/Paymob%20SDK-1.9.2-1A73E8?style=for-the-badge)

---

## 📸 Screenshots

### 🌞 Light Theme — English (LTR)

| Home | Product Details | Cart | Checkout |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/en_light_home.png" width="220" alt="Home"/> | <img src="screenshots/en_light_product_details.png" width="220" alt="Product Details"/> | <img src="screenshots/en_light_cart.png" width="220" alt="Cart"/> | <img src="screenshots/en_light_checkout.png" width="220" alt="Checkout"/> |
| **Search** | **Orders** | **Profile** | **AI Chat** |
| <img src="screenshots/en_light_search.png" width="220" alt="Search"/> | <img src="screenshots/en_light_orders.png" width="220" alt="Orders"/> | <img src="screenshots/en_light_profile.png" width="220" alt="Profile"/> | <img src="screenshots/en_light_ai_chat.png" width="220" alt="AI Chat"/> |

### 🌙 Dark Theme — English (LTR)

| Home | Product Details | Cart | Checkout |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/en_dark_home.png" width="220" alt="Home Dark"/> | <img src="screenshots/en_dark_product_details.png" width="220" alt="Product Details Dark"/> | <img src="screenshots/en_dark_cart.png" width="220" alt="Cart Dark"/> | <img src="screenshots/en_dark_checkout.png" width="220" alt="Checkout Dark"/> |

### 🌍 Arabic (RTL) Showcase

| الرئيسية (Home Light) | تفاصيل المنتج (Product Dark) | السلة (Cart Light) | الدفع (Checkout Dark) |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/ar_light_home.png" width="220" alt="Home Arabic"/> | <img src="screenshots/ar_dark_product_details.png" width="220" alt="Product Details Arabic Dark"/> | <img src="screenshots/ar_light_cart.png" width="220" alt="Cart Arabic"/> | <img src="screenshots/ar_dark_checkout.png" width="220" alt="Checkout Arabic Dark"/> |

---

## 🚀 Installation & Setup

### Prerequisites
* **Android Studio** Meerkat (2025.1+) or newer
* **JDK 17**
* **Min SDK 26** (Android 8.0+)

### 1. Clone the Repository
```bash
git clone [https://github.com/YOUR_USERNAME/ShopIQ.git](https://github.com/YOUR_USERNAME/ShopIQ.git)
cd ShopIQ
```

### 2. Configure API Keys
Create a `local.properties` file in the project root with the following keys. *(Note: `local.properties` is git-ignored. Never commit your API keys).*

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

# ─── Facebook & Paymob ────────────────────────────────
facebook.app.id=YOUR_FACEBOOK_APP_ID
facebook.client.token=YOUR_FACEBOOK_CLIENT_TOKEN
paymob.public.key=YOUR_PAYMOB_PUBLIC_KEY
paymob.secret.key=YOUR_PAYMOB_SECRET_KEY
paymob.integration.id=YOUR_PAYMOB_INTEGRATION_ID
paymob.api.key=YOUR_PAYMOB_API_KEY

# ─── Gemini AI ────────────────────────────────────────
gemini.api.key=YOUR_GEMINI_API_KEY
```
*Ensure you also place your `google-services.json` in the `/data` module directory for Firebase functionality.*

### 3. Build & Run
The project includes a **shared debug keystore** (`app/shared_debug.jks`) allowing all team members to build without registering individual SHA-1 fingerprints for Firebase and Google Maps.

```bash
# Sync Gradle
./gradlew build
```
Or open the project in Android Studio and click **Run**.

---

## 🧪 GraphQL Schema

The app consumes **two Shopify API services** configured via Apollo Kotlin:

| Service | API Endpoint | Operations | Purpose |
| :--- | :--- | :--- | :--- |
| `shopify` | Admin API | 8 queries | Managing Products, Collections, Categories, and Reviews. |
| `shopify-storefront` | Storefront API | 12 operations | Customer Auth, Cart CRUD operations, and Order placements. |

---

## 👥 Contributors

| Name | Role |
| :--- | :--- |
| **Aziza Helmy** | Senior Supervisor & Mentor |
| **Abdullh Mohamed** | Android Developer |
| **Abdelrahamn Waheed** | Android Developer |
| **Shrief Ashraf** | Android Developer |


> *Built during the **JETS MobileX Challenge 2026** at **ITI Smart Village** (June 27 – July 9, 2026).*

---

## 📄 License

```text
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
