
# Ancat Anket Uygulaması

## Genel Bakış
Ancat, Kotlin ve Java kullanılarak geliştirilen bir anket uygulamasıdır. Kullanıcıların çeşitli soru türleri ile anketler oluşturmasına ve bu anketleri PDF dosyası olarak kaydetmesine olanak tanır.

## Özellikler
- **Anket Oluşturma**: Kullanıcılar çoktan seçmeli, derecelendirme ve basit sorular ekleyebilir.
    - **Basit Sorular**: Temel metin soruları ekleyin.
    - **Derecelendirme Soruları**: Derecelendirme ölçeği ile sorular ekleyin.
    - **Çoktan Seçmeli Sorular**: Birden fazla seçeneği olan sorular ekleyin.
- **PDF Oluşturma**: Anket verilerini JSON formatından PDF'ye dönüştürün.
    - **JSON Okuma**: JSON dosyalarından anket verilerini okuyun.
    - **PDF Sayfası Oluşturma**: PDF belgesi için sayfalar oluşturun.
    - **PDF Kaydetme**: Oluşturulan PDF dosyasını kaydedin.

## Kullanılan Teknolojiler
- **Kotlin**: Android geliştirme için ana dil.
- **Java**: Bazı arka plan işlevleri için kullanılır.
- **Gradle**: Derleme otomasyon aracı.
- **Jetpack Compose**: Modern kullanıcı arayüzü oluşturma aracı.
- **Hilt**: Bağımlılık enjeksiyonu kütüphanesi.
- **Navigation Component**: Uygulama içi gezinmeyi yönetir.

