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

## Ödev koşulları

| Koşul | Uygulama |
|---|---|
| `@Immutable`, bütün alanlar `val` | `HardwareItem` |
| `remember` | Harcama, kalan stok, güncel envanter ve kategori listesi |
| `rememberSaveable` | Arama, kategori, ayırma sayıları, açılan kart ve oturum mesajı |
| Filtre önbelleği | `remember(searchQuery, selectedCategory)` |
| LazyColumn ve benzersiz key | `items(filteredHardware, key = { it.id })` |
| Stateless alt bileşenler | `HardwareCard`, `HardwareSearchBar`; içinde yerel remember yok |
| State Down / Events Up | Parametreler ve `onQueryChange`, `onAllocate`, `onToggleDetails` |
| Modifier sırası | Kart içeriğinde `.clickable(...).padding(18.dp)` |

Filtre sabit katalog üzerinde çalışır ve yalnızca arama veya kategori değiştiğinde yeniden hesaplanır. Stok değişince arama tekrarlanmaz; bulunan kimliklerin güncel stokları ayrı bir remember bloğunda alınır. Kategori filtrelendiği için selectedCategory de anahtardır.

## Doğrulama — 23 Ekim 2026

- Debug APK derlemesi başarılı.
- Altı proje mantık testi ve şablondan gelen bir örnek JUnit testi geçti.
- Android 16 / API 36 flutter_emulator cihazında 7/7 test geçti (6 proje senaryosu + 1 örnek paket testi). Arama sırasında gerçek Activity yeniden oluşturularak yatay döndürme, stok/harcama restorasyonu, kategori ve sorgu birleşimi, son stok ve sıfırlama, sıfır stok, boş sonuç kontrol edildi.
- Android Lint başarılı, hata yok; şablon kaynakları, sürüm güncellemeleri ve hedef SDK hakkında uyarılar var.
- Emülatör Android Studio üzerinden başlatıldı; cihaz testleri ve dikey/yatay görsel inceleme tamamlandı. İlk komut satırı başlatmasındaki sanallaştırma sorunu bu yolla aşıldı. Windows özellikleri değiştirilmedi.

Hızlı manuel kontrol: Pixel ara → bir adet ayır → stok 7 ve harcama $999.00 olmalı → ekranı döndür → arama, stok ve harcama korunmalı. HHKB aramasında ayırma düğmesi kapalı olmalı. Threadripper bir kez ayrılabilmeli. Sıfırlama bütün stokları başlangıca döndürmeli.


Derleyici doğrulaması: HardwareItem stable; HardwareCard ve HardwareSearchBar restartable skippable. Raporları üretmek için: .\gradlew.bat :app:compileDebugKotlin -PcomposeReports=true --rerun-tasks. Ana derleme tamamlanınca app/build/compose-reports klasörünü okuyun; test derlemesi aynı isimli raporları değiştirebilir.

