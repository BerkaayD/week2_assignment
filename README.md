# DevCluster

Jetpack Compose ile donanım ve sunucu kaynağı ayırma simülatörü.

## Çalıştırma

Android Studio'da bu klasörü aç: `D:\Projeler\week2_assignment`.
Gradle eşitlemesi tamamlandıktan sonra `app` yapılandırmasını ve bir Android cihazı seçip Run'a bas.
Minimum Android 7.0 / API 24; derleme SDK'sı 37, hedef SDK 36.
Mevcut projede AGP 9.2.1 ve Gradle 9.4.1 kullanılır. Gradle JDK olarak Android Studio'nun JBR'si seçilidir.

PowerShell'de, proje kökünde ve JAVA_HOME uygun JDK'yı gösterirken:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
# Bir cihaz/emülatör bağlıyken:
.\gradlew.bat :app:connectedDebugAndroidTest
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Kullanım

- İsim veya kategori üzerinden ara; Türkçe harfler ve büyük/küçük harf farkı desteklenir.
- Yatay kategori şeridinden filtre seç. Metin ve kategori filtresi birlikte uygulanır.
- **Kaynak ayır** stoktan bir adet düşer, birim fiyatı toplam harcamaya ekler.
- Stok sıfırsa düğme kapanır; işlem fonksiyonu da ayrıca sınırı kontrol eder.
- Kartın gövdesine dokunmak ayrıntıları açar. Ayrıntı açmak kaynak ayırmaz.
- **Sıfırla** onaydan sonra ayırmaları ve harcamayı sıfırlar. Filtreler korunur.
- Veriler yerel bir oturuma aittir; ödeme, sunucu bağlantısı veya kalıcı veritabanı yoktur.

## Dosyaların görevi

`app/src/main/java/com/example/week2_assignment/` altında:

| Dosya | Görevi |
|---|---|
| `MainActivity.kt` | Android giriş noktası; Compose içeriğini başlatır. |
| `data/HardwareItem.kt` | Değişmez model ve ödevin sekiz sabit kaynağı. |
| `data/HardwareOperations.kt` | Stok kontrolü, harcama, filtreleme; UI'dan bağımsız hesaplamalar. |
| `ui/DevClusterScreen.kt` | Merkezi durum, remember/rememberSaveable, filtre önbelleği ve LazyColumn. |
| `ui/HardwareComponents.kt` | Stateless arama çubuğu ve donanım kartı. |
| `ui/theme/Theme.kt` | Uygulamanın renkleri. |
