-- =============================================================================
-- SCRIPT SETUP DATABASE PBO_EVENT
-- Deskripsi: Script ini digunakan untuk membuat struktur database manajemen event.
--            Script ini akan menghapus tabel lama (jika ada) dan membuat ulang
--            dengan struktur yang rapi dan relasi yang benar.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. BAGIAN MEMBERSIHKAN TABEL LAMA (DROP TABLES)
-- -----------------------------------------------------------------------------
-- Menghapus tabel dengan urutan yang benar untuk menghindari error foreign key.
DROP TABLE IF EXISTS event_peserta CASCADE;
DROP TABLE IF EXISTS panitia CASCADE;
DROP TABLE IF EXISTS jadwal CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS karyawan CASCADE;
DROP TABLE IF EXISTS venue CASCADE;
DROP TABLE IF EXISTS peserta CASCADE;


-- -----------------------------------------------------------------------------
-- 2. BAGIAN PEMBUATAN TABEL (CREATE TABLES)
-- -----------------------------------------------------------------------------

-- A. Tabel Master: KARYAWAN
-- Menyimpan data pegawai atau staf internal.
CREATE TABLE karyawan (
    id SERIAL PRIMARY KEY,              -- ID Unik Karyawan (Auto-increment)
    nama VARCHAR(100) NOT NULL,         -- Nama Lengkap
    alamat TEXT,                        -- Alamat Tinggal
    kontak VARCHAR(20),                 -- Nomor Telepon/HP
    keahlian VARCHAR(100)               -- Keahlian/Spesialisasi (Ditambahkan untuk sinkronisasi dengan Backend)
);

-- B. Tabel Master: VENUE
-- Menyimpan data lokasi/tempat pelaksanaan event.
CREATE TABLE venue (
    id SERIAL PRIMARY KEY,              -- ID Unik Venue
    nama_venue VARCHAR(100) NOT NULL,   -- Nama Tempat
    alamat TEXT,                        -- Alamat Lengkap Venue
    kapasitas INT                       -- Daya Tampung Orang
);

-- C. Tabel Master: PESERTA
-- Menyimpan data orang yang mendaftar ke event.
CREATE TABLE peserta (
    id SERIAL PRIMARY KEY,              -- ID Unik Peserta
    nama_peserta VARCHAR(100) NOT NULL, -- Nama Lengkap Peserta
    email VARCHAR(100),                 -- Alamat Email
    no_hp VARCHAR(20)                   -- Nomor Handphone
);

-- D. Tabel Transaksi Utama: EVENT
-- Menyimpan data acara yang diadakan.
CREATE TABLE event (
    id SERIAL PRIMARY KEY,              -- ID Unik Event
    nama_event VARCHAR(100) NOT NULL,   -- Nama Acara
    tanggal DATE NOT NULL,              -- Tanggal Pelaksanaan
    pool_prize BIGINT DEFAULT 0,        -- Total Hadiah (jika ada kompetisi)
    harga_registrasi BIGINT DEFAULT 0,  -- Biaya Pendaftaran
    venue_id INT,                       -- Lokasi Event (Foreign Key ke Venue)
    
    -- Menghubungkan kolom venue_id dengan id di tabel venue
    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id) REFERENCES venue(id) ON DELETE SET NULL
);

-- E. Tabel Detail: JADWAL
-- Menyimpan susunan acara (rundown) untuk setiap event.
CREATE TABLE jadwal (
    id SERIAL PRIMARY KEY,              -- ID Unik Jadwal
    event_id INT NOT NULL,              -- ID Event (Foreign Key)
    nama_agenda VARCHAR(100) NOT NULL,  -- Nama Kegiatan (misal: "Pembukaan")
    waktu_mulai VARCHAR(50),            -- Jam Mulai (disimpan sebagai teks, cth: "08:00")
    waktu_selesai VARCHAR(50),          -- Jam Selesai
    pengisi_acara VARCHAR(100),         -- Nama Pembicara/MC/Band
    
    -- Link ke tabel Event
    CONSTRAINT fk_jadwal_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
);

-- F. Tabel Relasi: PANITIA
-- Menghubungkan Event dengan Karyawan yang bertugas (Many-to-Many).
CREATE TABLE panitia (
    id SERIAL PRIMARY KEY,              -- ID Unik Kepanitiaan
    event_id INT NOT NULL,              -- ID Event
    karyawan_id INT NOT NULL,           -- ID Karyawan yang bertugas
    jabatan VARCHAR(50),                -- Peran (misal: "Ketua Pelaksana", "Logistik")
    
    -- Constraint Foreign Key
    CONSTRAINT fk_panitia_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
    CONSTRAINT fk_panitia_karyawan FOREIGN KEY (karyawan_id) REFERENCES karyawan(id) ON DELETE CASCADE
);

-- G. Tabel Relasi: EVENT_PESERTA
-- Mencatat pendaftaran peserta ke event tertentu (Many-to-Many).
CREATE TABLE event_peserta (
    id SERIAL PRIMARY KEY,              -- ID Transaksi Pendaftaran
    event_id INT NOT NULL,              -- ID Event yang diikuti
    peserta_id INT NOT NULL,            -- ID Peserta yang mendaftar
    status_kehadiran VARCHAR(50) DEFAULT 'Terdaftar', -- Status (Terdaftar/Hadir/Batal)
    
    -- Constraint Foreign Key
    CONSTRAINT fk_ep_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
    CONSTRAINT fk_ep_peserta FOREIGN KEY (peserta_id) REFERENCES peserta(id) ON DELETE CASCADE
);


-- -----------------------------------------------------------------------------
-- 3. BAGIAN DATA DUMMY (SEEDING DATA)
-- -----------------------------------------------------------------------------
-- Mengisi data awal agar database tidak kosong saat pertama kali dijalankan.

-- 1. Insert Data Venue
INSERT INTO venue (nama_venue, alamat, kapasitas) VALUES 
('Aula Utama', 'Lt 1 Gedung A', 200),
('Ruang Rapat VIP', 'Lt 2 Gedung B', 50);

-- 2. Insert Data Karyawan
INSERT INTO karyawan (nama, alamat, kontak, keahlian) VALUES 
('Budi Santoso', 'Jl. Merpati No 10', '08123456789', 'Manajemen Event'),
('Siti Aminah', 'Jl. Kenari No 5', '08567890123', 'Administrasi & Keuangan');

-- 3. Insert Data Peserta
INSERT INTO peserta (nama_peserta, email, no_hp) VALUES 
('Ahmad Dani', 'ahmad@mail.com', '08111222333');

-- 4. Insert Data Event
-- (Menggunakan Venue ID 1)
INSERT INTO event (nama_event, tanggal, pool_prize, harga_registrasi, venue_id) VALUES 
('Turnamen Mobile Legends', '2025-10-10', 5000000, 50000, 1),
('Workshop Koding', '2025-11-20', 0, 100000, 1);

-- 5. Insert Data Jadwal
-- (Untuk Event ID 1)
INSERT INTO jadwal (event_id, nama_agenda, waktu_mulai, waktu_selesai, pengisi_acara) VALUES 
(1, 'Registrasi Ulang', '08:00', '09:00', 'Panitia'),
(1, 'Babak Penyisihan', '09:00', '12:00', 'Refree');

-- 6. Insert Data Panitia
-- (Budi menjadi Ketua di Event 1)
INSERT INTO panitia (event_id, karyawan_id, jabatan) VALUES 
(1, 1, 'Ketua Pelaksana');

-- 7. Insert Data Keikutsertaan Peserta
-- (Ahmad mendaftar ke Event 1)
INSERT INTO event_peserta (event_id, peserta_id, status_kehadiran) VALUES 
(1, 1, 'Terdaftar');

-- =============================================================================
-- AKHIR DARI SCRIPT
-- =============================================================================

