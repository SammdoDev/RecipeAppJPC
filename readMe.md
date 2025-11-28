# Recipe App 🍳

Aplikasi resep masakan Android dengan fitur autentikasi Firebase, tampilan resep, detail lengkap, dan terjemahan otomatis menggunakan Google Translate API.

## 📱 Screenshots

| Welcome Screen | Home Screen | Detail Recipe Screen |
|:-------------:|:-----------:|:-------------------:|
| <img src="https://github.com/SammdoDev/RecipeAppJPC/blob/master/app/src/main/res/drawable/image_1.jpg" width="260"> | <img src="https://github.com/SammdoDev/RecipeAppJPC/blob/master/app/src/main/res/drawable/image_2.jpg" width="260"> | <img src="https://github.com/SammdoDev/RecipeAppJPC/blob/master/app/src/main/res/drawable/image_3.jpg" width="260"> |

### 1. Welcome Screen
- Login dengan Google Sign-In
- Tampilan welcome yang menarik
- Fitur unggulan: 10,000+ Recipes, Easy Search, Save Favorites

### 2. Home Screen
- Greeting dengan nama user dan foto profil
- Search bar untuk mencari resep
- Filter kategori (All, Beef, Chicken, Dessert, Side)
- Grid layout resep dengan gambar menarik
- Tombol translate untuk mengubah bahasa
- Badge kategori pada setiap resep
- Label asal masakan (Canadian, Syrian, dll)

### 3. Detail Recipe Screen
- Gambar full-screen resep
- Nama resep
- Kategori dan asal masakan
- Daftar ingredients lengkap dengan takaran
- Tombol translate untuk terjemahan
- Tombol back untuk kembali ke home

## ✨ Fitur Utama

| Kategori | Fitur | Deskripsi |
|----------|-------|-----------|
| 🔐 **Authentication** | Google Sign-In | Login menggunakan Firebase Google Sign-In |
| | User Profile | Menampilkan profil user (nama & foto) |
| | Session Management | Pengelolaan sesi login otomatis |
| 🏠 **Home Screen** | Search | Pencarian resep real-time |
| | Filter Kategori | Filter berdasarkan kategori masakan |
| | Grid Layout | Tampilan grid dengan gambar resep menarik |
| | Recipe Info | Informasi kategori dan asal resep |
| | Translation | Fitur translate multi-bahasa |
| 📖 **Detail Recipe** | Full Information | Informasi lengkap resep |
| | Ingredients List | Daftar bahan dengan takaran detail |
| | Recipe Origin | Kategori dan asal masakan |
| | Translate | Fitur translate untuk bahan dan instruksi |

## 🚀 Tech Stack

| Kategori | Teknologi | Detail |
|----------|-----------|--------|
| **Platform** | Android Native | - |
| **Language** | Kotlin | - |
| **IDE** | Android Studio | - |
| **Authentication** | Firebase Authentication | Google Sign-In |
| **Database** | Firebase Firestore | Cloud database |
| **Translation** | Google Cloud Translation API | Multi-language support |
| **UI Components** | Material Design 3 | Modern UI/UX |
| | RecyclerView | List management |
| | CardView | Card-based layout |

## 📋 Fitur yang Diimplementasikan

| Status | Fitur |
|:------:|-------|
| ✅ | Google Sign-In Authentication |
| ✅ | Home screen dengan daftar resep |
| ✅ | Search functionality |
| ✅ | Filter kategori resep |
| ✅ | Detail resep dengan ingredients |
| ✅ | Google Translate integration |
| ✅ | User profile display |
| ✅ | Responsive UI dengan Material Design |

## 🎨 Design Pattern & Architecture

| Pattern | Implementasi |
|---------|--------------|
| **MVVM** | Model-View-ViewModel architecture |
| **Repository Pattern** | Data management layer |
| **Coroutines** | Async operations |
| **LiveData** | Observable data pattern |

## 🔑 API Keys Required

| No | Requirement | Keterangan |
|----|-------------|------------|
| 1 | Firebase Configuration | File `google-services.json` |
| 2 | Google Translate API Key | Untuk fitur terjemahan |
| 3 | SHA-1 Fingerprint | Untuk Google Sign-In |

## 📦 Dependencies

```gradle
dependencies {
    // Firebase
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.android.gms:play-services-auth'
    
    // Google Translate
    implementation 'com.google.cloud:google-cloud-translate'
    
    // UI
    implementation 'com.google.android.material:material'
    implementation 'androidx.recyclerview:recyclerview'
    implementation 'androidx.cardview:cardview'
    
    // Image Loading
    implementation 'com.github.bumptech.glide:glide'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services'
}
```

## 🛠️ Setup Instructions

### 1. Clone Repository
```bash
git clone https://github.com/SammdoDev/RecipeAppJPC.git
cd RecipeAppJPC
```

### 2. Firebase Setup
1. Buat project di [Firebase Console](https://console.firebase.google.com/)
2. Download `google-services.json`
3. Letakkan file di folder `app/`
4. Enable Google Sign-In di Authentication
5. Tambahkan SHA-1 fingerprint

### 3. Google Translate API
1. Buat project di [Google Cloud Console](https://console.cloud.google.com/)
2. Enable Cloud Translation API
3. Buat API Key
4. Tambahkan ke project

### 4. Build & Run
```bash
./gradlew build
./gradlew installDebug
```

## 📱 Minimum Requirements

| Requirement | Minimum |
|-------------|---------|
| **Android Version** | Android 7.0 (API 24) |
| **RAM** | 2 GB |
| **Storage** | 100 MB |
| **Internet** | Required |

## 🎯 Future Improvements

| Priority | Feature |
|----------|---------|
| 🔴 High | Add favorite recipes feature |
| 🔴 High | Implement offline mode |
| 🟡 Medium | Add cooking timer |
| 🟡 Medium | Recipe rating system |
| 🟢 Low | Share recipe feature |
| 🟢 Low | Dark mode support |

## 📄 License

```
MIT License

Copyright (c) 2024 SammdoDev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## 👨‍💻 Developer

**SammdoDev**

Dibuat dengan ❤️ menggunakan Kotlin dan Android Studio

---

⭐ Jangan lupa beri star jika project ini bermanfaat!
