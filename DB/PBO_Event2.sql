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

-- 5. Tabel Panitia (Relasi Event - Karyawan)
CREATE TABLE panitia (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    karyawan_id INT NOT NULL,
    jabatan VARCHAR(50),
    CONSTRAINT fk_panitia_event FOREIGN KEY (event_id) REFERENCES event(id),
    CONSTRAINT fk_panitia_karyawan FOREIGN KEY (karyawan_id) REFERENCES karyawan(id)
);

-- 6. Tabel Jadwal (Detail Event)
CREATE TABLE jadwal (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL, 
    nama_agenda VARCHAR(100) NOT NULL,
    pengisi_acara VARCHAR(100),
    waktu_mulai TIME, 
    waktu_selesai TIME,
    CONSTRAINT fk_jadwal_event FOREIGN KEY (event_id) REFERENCES event(id)
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
INSERT INTO venue (nama_venue, alamat, kapasitas) VALUES 
('Aula Utama', 'Lt 1 Gedung A', 200),
('Ruang Serbaguna', 'Lt 2 Gedung B', 150);

-- Isi Karyawan (Dengan Alamat dan Keahlian)
INSERT INTO karyawan (nama, alamat, keahlian, kontak) VALUES 
('Budi Santoso', 'Jl. Merpati No 10', 'Event Organizer', '08123456789'),
('Ani Wijaya', 'Jl. Kenari No 5', 'MC', '08234567890'),
('Joko Susilo', 'Jl. Elang No 8', 'Sound System', '08345678901');

-- Isi Peserta
INSERT INTO peserta (nama_peserta, email, no_hp) VALUES 
('Ahmad Rifai', 'ahmad@email.com', '08111222333'),
('Siti Nurhaliza', 'siti@email.com', '08222333444');

-- Isi Event (Dengan Pool Prize & Harga)
INSERT INTO event (nama_event, tanggal, pool_prize, harga_registrasi, venue_id) 
VALUES 
('Turnamen Mobile Legends', '2025-10-10', 5000000, 50000, 1),
('Workshop Digital Marketing', '2025-11-15', 0, 100000, 2);

-- Isi Panitia
INSERT INTO panitia (event_id, karyawan_id, jabatan) VALUES 
(1, 1, 'Ketua Panitia'),
(1, 2, 'Koordinator Acara'),
(2, 3, 'Teknisi');

-- Isi Jadwal (Sesuai dengan FrmJadwal)
INSERT INTO jadwal (event_id, nama_agenda, pengisi_acara, waktu_mulai, waktu_selesai) VALUES 
(1, 'Pembukaan', 'MC - Ani Wijaya', '08:00:00', '08:30:00'),
(1, 'Babak Penyisihan', 'Tim Juri', '08:30:00', '12:00:00'),
(1, 'Ishoma', NULL, '12:00:00', '13:00:00'),
(1, 'Babak Final', 'Tim Juri', '13:00:00', '16:00:00'),
(1, 'Penutupan & Pembagian Hadiah', 'MC - Ani Wijaya', '16:00:00', '17:00:00'),
(2, 'Registrasi Peserta', NULL, '08:00:00', '08:30:00'),
(2, 'Materi SEO & SEM', 'Dr. Bambang SE', '08:30:00', '10:30:00'),
(2, 'Coffee Break', NULL, '10:30:00', '11:00:00'),
(2, 'Social Media Marketing', 'Intan Permata', '11:00:00', '13:00:00');

-- Isi Event Peserta
INSERT INTO event_peserta (event_id, peserta_id, status_kehadiran) VALUES 
(1, 1, 'Terdaftar'),
(2, 2, 'Hadir');