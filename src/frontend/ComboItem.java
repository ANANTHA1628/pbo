package frontend;

/**
 * Class ComboItem
 * Deskripsi: Class helper untuk menyimpan pasangan Key (String) dan Value
 * (Integer)
 * pada komponen JComboBox.
 * Digunakan agar ComboBox bisa menampilkan Teks (misal: Nama Venue)
 * tapi menyimpan ID (misal: venue_id) di baliknya.
 */
public class ComboItem {
    private String key;
    private int value;

    // Kustruktor
    public ComboItem(String key, int value) {
        this.key = key;
        this.value = value;
    }

    // Getter untuk mendapatkan nilai ID (Value)
    public int getValue() {
        return value;
    }

    // Override toString agar yang muncul di ComboBox adalah Key (Nama/Teks)
    @Override
    public String toString() {
        return key;
    }
}