-- Hapus tabel lama jika ada agar bersih (urutan drop penting karena foreign key)
DROP TABLE IF EXISTS event_peserta;
DROP TABLE IF EXISTS panitia;
DROP TABLE IF EXISTS jadwal;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS karyawan;
DROP TABLE IF EXISTS venue;
DROP TABLE IF EXISTS peserta;

-- 1. Tabel Karyawan (Master)
CREATE TABLE karyawan (
    id SERIAL PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    kontak VARCHAR(20)
);

-- 2. Tabel Venue (Master)
CREATE TABLE venue (
    id SERIAL PRIMARY KEY,
    nama_venue VARCHAR(100) NOT NULL,
    alamat TEXT,
    kapasitas INT
);

-- 3. Tabel Peserta (Master Data Peserta)
CREATE TABLE peserta (
    id SERIAL PRIMARY KEY,
    nama_peserta VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    no_hp VARCHAR(20)
);

-- 4. Tabel Event (Transaksi Utama)
CREATE TABLE event (
    id SERIAL PRIMARY KEY,
    nama_event VARCHAR(100) NOT NULL,
    tanggal DATE NOT NULL,
    venue_id INT, -- Relasi ke Venue
    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id) REFERENCES venue(id)
);

-- 5. Tabel Jadwal (Detail Event)
CREATE TABLE jadwal (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL, -- Relasi ke Event
    nama_agenda VARCHAR(100) NOT NULL,
    waktu_mulai VARCHAR(50), -- Simpan string jam (misal: "08:00") agar simpel
    waktu_selesai VARCHAR(50),
    CONSTRAINT fk_jadwal_event FOREIGN KEY (event_id) REFERENCES event(id)
);

-- 6. Tabel Panitia (Relasi Many-to-Many: Event & Karyawan)
CREATE TABLE panitia (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    karyawan_id INT NOT NULL,
    jabatan VARCHAR(50),
    CONSTRAINT fk_panitia_event FOREIGN KEY (event_id) REFERENCES event(id),
    CONSTRAINT fk_panitia_karyawan FOREIGN KEY (karyawan_id) REFERENCES karyawan(id)
);

-- 7. Tabel Event Peserta (Relasi Many-to-Many: Event & Peserta)
CREATE TABLE event_peserta (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    peserta_id INT NOT NULL,
    status_kehadiran VARCHAR(50) DEFAULT 'Terdaftar',
    CONSTRAINT fk_ep_event FOREIGN KEY (event_id) REFERENCES event(id),
    CONSTRAINT fk_ep_peserta FOREIGN KEY (peserta_id) REFERENCES peserta(id)
);

-- Data Dummy Awal (Opsional)
INSERT INTO venue (nama_venue, alamat, kapasitas) VALUES ('Aula Utama', 'Lt 1', 200);
INSERT INTO karyawan (nama, kontak) VALUES ('Budi Santoso', '08123456789');