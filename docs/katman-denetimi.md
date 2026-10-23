# DevCluster — dört katman denetimi

23 Ekim 2026. Proje: `D:\Projeler\week2_assignment`.

**Sonuç: A, B, C ve D için istenen kod yapıları mevcut; Android 16 / API 36 emülatöründeki bütün cihaz testleri geçti.**

## A — Veri ve derleyici sözleşmesi

`HardwareItem.kt` içindeki model `@Immutable data class HardwareItem` olarak tanımlı. Beş alanın tamamı val; türleri String, Double ve Int. Ödevde verilen sekiz kayıt ve başlangıç stokları korunuyor.

Üretim kodunun Compose derleyici raporundan:

```text
stable class HardwareItem
restartable skippable ... fun HardwareSearchBar
restartable skippable ... fun HardwareCard
  stable item: HardwareItem
```

Bu rapor derleyicinin modeli kararlı gördüğünü ve alt bileşenlerin uygun koşullarda atlanabildiğini doğrular. Her durumda sıfır yeniden çizim veya bütün program için yarış durumu olmaması garantisi değildir.

## B — Durum ve önbellek

- Harcama: `remember(allocations) { totalCostFor(allocations) }`.
- Arama: `rememberSaveable { mutableStateOf("") }`.
- Ayırmalar küçük bir sayı listesi olarak kaydedilir; envanter ve harcama aynı kaynaktan türetilir.
- Arama algoritması: `remember(searchQuery, selectedCategory)` içinde sabit katalogda çalışır.
- Güncel stoklar: eşleşen donanım kimlikleri güncel envantere ayrı bir remember bloğunda eşlenir.

Denetimde yapılan iyileştirme: Önceki filtrede inventory de anahtardı; stok ayırınca isim/kategori araması yeniden çalışıyordu. Artık stok değişimi aramayı tekrar çalıştırmıyor. Kategori filtresi bulunduğu için selectedCategory anahtarını tutmak zorunlu; sadece searchQuery kullanmak kategori değişince eski sonucu gösterirdi.

Gerçek yön değiştirme testi arama kutusuna odaklanıp Raspberry yazar, cihazı yataya alır, Activity nesnesinin değiştiğini doğrular ve metnin korunduğunu kontrol eder. **Geçti.** Ayrı testte Pixel ayrıldıktan sonra Activity yeniden oluşturuldu; arama Pixel, stok 7 ve harcama $999.00 kaldı. **Geçti.**

## C — Liste

Liste LazyColumn ile çizilir; donanım satırları `items(filteredHardware, key = { it.id })` kullanır. UI öğelerini forEach ile üreten bir döngü yoktur. Kategori şeridi de LazyRow kullanır. key satır kimliğini korur; tek başına bütün yeniden hesaplamaları ortadan kaldırmaz.

## D — Stateless UI

HardwareCard ve HardwareSearchBar içinde remember, rememberSaveable veya mutableStateOf çağrısı yoktur. Görünüm verilerini parametre olarak alırlar. `onQueryChange`, `onAllocate` ve `onToggleDetails` olayları tek durum sahibi DevClusterScreen'e taşır.

Kartın içerik Modifier sırası `.clickable(...).padding(18.dp)` biçimindedir; iç boşluk tıklanabilir alanın içindedir.

## Test kanıtı

Son cihaz koşusu: flutter_emulator, Android 16, API 36. **7 test, 0 başarısız, 0 hata, 0 atlanan.** Bunların altısı aşağıdaki proje senaryoları, biri şablondan gelen paket testi.

| Senaryo | Sonuç |
|---|---|
| Arama yazılıyken Portrait → Landscape, yeni Activity ve korunmuş metin | Geçti |
| Arama, stok ve harcamanın Activity yeniden oluşturulunca korunması | Geçti |
| Kategori + sorgu birleşimi; stok güncellemesi ve yeniden oluşturma | Geçti |
| Son stokta düğmenin kapanması; sıfırlamayla stok/harcamanın geri dönmesi | Geçti |
| Başlangıçta stoksuz HHKB'nin ayrılamaması | Geçti |
| Boş arama sonucundan filtreleri temizleyerek sekiz kayda dönme | Geçti |

Altı JVM iş mantığı testi de geçti; şablonun örnek JUnit testiyle toplam 7/7. Android Lint hata bildirmedi; hedef SDK, yeni bağımlılık sürümleri ve kullanılmayan şablon kaynakları için 12 uyarı var.

İlk cihaz koşusunda Android açılışı tamamlanmadığından APK kurulamadı. Sonraki koşuda iki test seçicisi eski/birleşmiş UI düğümlerini aradığı için düzeltildi. Nihai tam koşu başarılıdır; başarısız testler kaldırılmadı veya atlanmadı.

## Sınav için doğru terimler

remember bir makro veya iş parçacığı kilidi değildir; composition içinde değer saklayan bir Composable fonksiyondur. rememberSaveable desteklenen değerleri Android'in kayıtlı durum mekanizmasıyla geri getirir; kalıcı veritabanı ve “RAM dışında disk alanı” ile aynı şey değildir. @Immutable değişmezliği kendiliğinden sağlamaz; bizim modelimiz bu sözleşmeyi val alanlar ve değişmez alan türleriyle yerine getirir.

Kaynaklar: [Compose durum yönetimi](https://developer.android.com/develop/ui/compose/state), [Compose performans önerileri](https://developer.android.com/develop/ui/compose/performance/bestpractices).
