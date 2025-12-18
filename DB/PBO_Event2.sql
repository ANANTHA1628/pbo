-- --- HAPUS TABEL LAMA (Urutan penting agar tidak error FK) ---
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
    alamat TEXT,
    keahlian VARCHAR (100),            
    kontak VARCHAR(20)
);

-- 2. Tabel Venue (Master)
CREATE TABLE venue (
    id SERIAL PRIMARY KEY,
    nama_venue VARCHAR(100) NOT NULL,
    alamat TEXT,
    kapasitas INT
);

-- 3. Tabel Peserta (Master)
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
    pool_prize BIGINT DEFAULT 0,       
    harga_registrasi BIGINT DEFAULT 0,
    venue_id INT, 
    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id) REFERENCES venue(id)
);

-- 5. Tabel Jadwal (Detail Event)
CREATE TABLE jadwal (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL, 
    panitia_id INT NOT NULL,
    nama_agenda VARCHAR(100) NOT NULL,
    waktu_mulai TIME, 
    waktu_selesai TIME,
    CONSTRAINT fk_jadwal_event FOREIGN KEY (event_id) REFERENCES event(id),
    CONSTRAINT fk_jadwal_panitia FOREIGN KEY (panitia_id) REFERENCES panitia(id)
);

-- 6. Tabel Panitia (Relasi Event - Karyawan)
CREATE TABLE panitia (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    karyawan_id INT NOT NULL,
    jabatan VARCHAR(50),
    CONSTRAINT fk_panitia_event FOREIGN KEY (event_id) REFERENCES event(id),
    CONSTRAINT fk_panitia_karyawan FOREIGN KEY (karyawan_id) REFERENCES karyawan(id)
);

-- 7. Tabel Event Peserta (Relasi Event - Peserta)
CREATE TABLE event_peserta (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    peserta_id INT NOT NULL,
    status_kehadiran VARCHAR(50) DEFAULT 'Terdaftar',
    CONSTRAINT fk_ep_event FOREIGN KEY (event_id) REFERENCES event(id),
    CONSTRAINT fk_ep_peserta FOREIGN KEY (peserta_id) REFERENCES peserta(id)
);

-- --- DATA DUMMY AWAL ---

-- Isi Venue
INSERT INTO venue (nama_venue, alamat, kapasitas) VALUES ('Aula Utama', 'Lt 1 Gedung A', 200);

-- Isi Karyawan (Dengan Alamat)
INSERT INTO karyawan (nama, alamat, kontak) VALUES ('Budi Santoso', 'Jl. Merpati No 10', '08123456789');

-- Isi Event (Dengan Pool Prize & Harga)
INSERT INTO event (nama_event, tanggal, pool_prize, harga_registrasi, venue_id) 
VALUES ('Turnamen Mobile Legends', '2025-10-10', 5000000, 50000, 1);