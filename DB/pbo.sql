--
-- PostgreSQL database dump
--

\restrict YFVLTAWafSd8SSbr5egfo6ClOoRqKIbARiBHhnWGZniyAEuR0sPgTc0myy88SAI

-- Dumped from database version 15.14
-- Dumped by pg_dump version 15.14

-- Started on 2025-12-18 13:33:58

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 221 (class 1259 OID 19317)
-- Name: event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event (
    id integer NOT NULL,
    nama_event character varying(100) NOT NULL,
    tanggal date NOT NULL,
    pool_prize bigint DEFAULT 0,
    harga_registrasi bigint DEFAULT 0,
    venue_id integer
);


ALTER TABLE public.event OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 19316)
-- Name: event_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_id_seq OWNER TO postgres;

--
-- TOC entry 3395 (class 0 OID 0)
-- Dependencies: 220
-- Name: event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.event_id_seq OWNED BY public.event.id;


--
-- TOC entry 225 (class 1259 OID 19360)
-- Name: event_peserta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_peserta (
    id integer NOT NULL,
    event_id integer NOT NULL,
    peserta_id integer NOT NULL,
    status_kehadiran character varying(50) DEFAULT 'Terdaftar'::character varying
);


ALTER TABLE public.event_peserta OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 19359)
-- Name: event_peserta_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.event_peserta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_peserta_id_seq OWNER TO postgres;

--
-- TOC entry 3396 (class 0 OID 0)
-- Dependencies: 224
-- Name: event_peserta_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.event_peserta_id_seq OWNED BY public.event_peserta.id;


--
-- TOC entry 227 (class 1259 OID 19378)
-- Name: jadwal; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.jadwal (
    id integer NOT NULL,
    event_id integer NOT NULL,
    panitia_id integer NOT NULL,
    nama_agenda character varying(100) NOT NULL,
    waktu_mulai time without time zone,
    waktu_selesai time without time zone
);


ALTER TABLE public.jadwal OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 19377)
-- Name: jadwal_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.jadwal_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.jadwal_id_seq OWNER TO postgres;

--
-- TOC entry 3397 (class 0 OID 0)
-- Dependencies: 226
-- Name: jadwal_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.jadwal_id_seq OWNED BY public.jadwal.id;


--
-- TOC entry 215 (class 1259 OID 19292)
-- Name: karyawan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.karyawan (
    id integer NOT NULL,
    nama character varying(100) NOT NULL,
    alamat text,
    keahlian character varying(100),
    kontak character varying(20)
);


ALTER TABLE public.karyawan OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 19291)
-- Name: karyawan_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.karyawan_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.karyawan_id_seq OWNER TO postgres;

--
-- TOC entry 3398 (class 0 OID 0)
-- Dependencies: 214
-- Name: karyawan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.karyawan_id_seq OWNED BY public.karyawan.id;


--
-- TOC entry 223 (class 1259 OID 19343)
-- Name: panitia; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.panitia (
    id integer NOT NULL,
    event_id integer NOT NULL,
    karyawan_id integer NOT NULL,
    jabatan character varying(50)
);


ALTER TABLE public.panitia OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 19342)
-- Name: panitia_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.panitia_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.panitia_id_seq OWNER TO postgres;

--
-- TOC entry 3399 (class 0 OID 0)
-- Dependencies: 222
-- Name: panitia_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.panitia_id_seq OWNED BY public.panitia.id;


--
-- TOC entry 219 (class 1259 OID 19310)
-- Name: peserta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.peserta (
    id integer NOT NULL,
    nama_peserta character varying(100) NOT NULL,
    email character varying(100),
    no_hp character varying(20)
);


ALTER TABLE public.peserta OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 19309)
-- Name: peserta_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.peserta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.peserta_id_seq OWNER TO postgres;

--
-- TOC entry 3400 (class 0 OID 0)
-- Dependencies: 218
-- Name: peserta_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.peserta_id_seq OWNED BY public.peserta.id;


--
-- TOC entry 217 (class 1259 OID 19301)
-- Name: venue; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.venue (
    id integer NOT NULL,
    nama_venue character varying(100) NOT NULL,
    alamat text,
    kapasitas integer
);


ALTER TABLE public.venue OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 19300)
-- Name: venue_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.venue_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.venue_id_seq OWNER TO postgres;

--
-- TOC entry 3401 (class 0 OID 0)
-- Dependencies: 216
-- Name: venue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.venue_id_seq OWNED BY public.venue.id;


--
-- TOC entry 3206 (class 2604 OID 19320)
-- Name: event id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event ALTER COLUMN id SET DEFAULT nextval('public.event_id_seq'::regclass);


--
-- TOC entry 3210 (class 2604 OID 19363)
-- Name: event_peserta id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_peserta ALTER COLUMN id SET DEFAULT nextval('public.event_peserta_id_seq'::regclass);


--
-- TOC entry 3212 (class 2604 OID 19381)
-- Name: jadwal id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jadwal ALTER COLUMN id SET DEFAULT nextval('public.jadwal_id_seq'::regclass);


--
-- TOC entry 3203 (class 2604 OID 19295)
-- Name: karyawan id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.karyawan ALTER COLUMN id SET DEFAULT nextval('public.karyawan_id_seq'::regclass);


--
-- TOC entry 3209 (class 2604 OID 19346)
-- Name: panitia id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.panitia ALTER COLUMN id SET DEFAULT nextval('public.panitia_id_seq'::regclass);


--
-- TOC entry 3205 (class 2604 OID 19313)
-- Name: peserta id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.peserta ALTER COLUMN id SET DEFAULT nextval('public.peserta_id_seq'::regclass);


--
-- TOC entry 3204 (class 2604 OID 19304)
-- Name: venue id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.venue ALTER COLUMN id SET DEFAULT nextval('public.venue_id_seq'::regclass);


--
-- TOC entry 3383 (class 0 OID 19317)
-- Dependencies: 221
-- Data for Name: event; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.event (id, nama_event, tanggal, pool_prize, harga_registrasi, venue_id) FROM stdin;
1	Turnamen Mobile Legends	2025-10-10	5000000	50000	1
2	tes	2025-12-18	200000	5000	1
\.


--
-- TOC entry 3387 (class 0 OID 19360)
-- Dependencies: 225
-- Data for Name: event_peserta; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.event_peserta (id, event_id, peserta_id, status_kehadiran) FROM stdin;
\.


--
-- TOC entry 3389 (class 0 OID 19378)
-- Dependencies: 227
-- Data for Name: jadwal; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.jadwal (id, event_id, panitia_id, nama_agenda, waktu_mulai, waktu_selesai) FROM stdin;
1	1	1	turu	10:02:22	13:05:00
2	1	1	tidur siang	13:32:00	15:00:00
\.


--
-- TOC entry 3377 (class 0 OID 19292)
-- Dependencies: 215
-- Data for Name: karyawan; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.karyawan (id, nama, alamat, keahlian, kontak) FROM stdin;
1	Budi Santoso	Jl. Merpati No 10	tidur	08123456789
\.


--
-- TOC entry 3385 (class 0 OID 19343)
-- Dependencies: 223
-- Data for Name: panitia; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.panitia (id, event_id, karyawan_id, jabatan) FROM stdin;
1	1	1	emsi
2	1	1	tukang
\.


--
-- TOC entry 3381 (class 0 OID 19310)
-- Dependencies: 219
-- Data for Name: peserta; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.peserta (id, nama_peserta, email, no_hp) FROM stdin;
\.


--
-- TOC entry 3379 (class 0 OID 19301)
-- Dependencies: 217
-- Data for Name: venue; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.venue (id, nama_venue, alamat, kapasitas) FROM stdin;
1	Aula Utama	Lt 1 Gedung A	200
\.


--
-- TOC entry 3402 (class 0 OID 0)
-- Dependencies: 220
-- Name: event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.event_id_seq', 2, true);


--
-- TOC entry 3403 (class 0 OID 0)
-- Dependencies: 224
-- Name: event_peserta_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.event_peserta_id_seq', 1, false);


--
-- TOC entry 3404 (class 0 OID 0)
-- Dependencies: 226
-- Name: jadwal_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.jadwal_id_seq', 2, true);


--
-- TOC entry 3405 (class 0 OID 0)
-- Dependencies: 214
-- Name: karyawan_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.karyawan_id_seq', 1, true);


--
-- TOC entry 3406 (class 0 OID 0)
-- Dependencies: 222
-- Name: panitia_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.panitia_id_seq', 2, true);


--
-- TOC entry 3407 (class 0 OID 0)
-- Dependencies: 218
-- Name: peserta_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.peserta_id_seq', 1, false);


--
-- TOC entry 3408 (class 0 OID 0)
-- Dependencies: 216
-- Name: venue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.venue_id_seq', 1, true);


--
-- TOC entry 3224 (class 2606 OID 19366)
-- Name: event_peserta event_peserta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_peserta
    ADD CONSTRAINT event_peserta_pkey PRIMARY KEY (id);


--
-- TOC entry 3220 (class 2606 OID 19324)
-- Name: event event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (id);


--
-- TOC entry 3226 (class 2606 OID 19383)
-- Name: jadwal jadwal_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jadwal
    ADD CONSTRAINT jadwal_pkey PRIMARY KEY (id);


--
-- TOC entry 3214 (class 2606 OID 19299)
-- Name: karyawan karyawan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.karyawan
    ADD CONSTRAINT karyawan_pkey PRIMARY KEY (id);


--
-- TOC entry 3222 (class 2606 OID 19348)
-- Name: panitia panitia_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.panitia
    ADD CONSTRAINT panitia_pkey PRIMARY KEY (id);


--
-- TOC entry 3218 (class 2606 OID 19315)
-- Name: peserta peserta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.peserta
    ADD CONSTRAINT peserta_pkey PRIMARY KEY (id);


--
-- TOC entry 3216 (class 2606 OID 19308)
-- Name: venue venue_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.venue
    ADD CONSTRAINT venue_pkey PRIMARY KEY (id);


--
-- TOC entry 3230 (class 2606 OID 19367)
-- Name: event_peserta fk_ep_event; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_peserta
    ADD CONSTRAINT fk_ep_event FOREIGN KEY (event_id) REFERENCES public.event(id);


--
-- TOC entry 3231 (class 2606 OID 19372)
-- Name: event_peserta fk_ep_peserta; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_peserta
    ADD CONSTRAINT fk_ep_peserta FOREIGN KEY (peserta_id) REFERENCES public.peserta(id);


--
-- TOC entry 3227 (class 2606 OID 19325)
-- Name: event fk_event_venue; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk_event_venue FOREIGN KEY (venue_id) REFERENCES public.venue(id);


--
-- TOC entry 3232 (class 2606 OID 19384)
-- Name: jadwal fk_jadwal_event; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jadwal
    ADD CONSTRAINT fk_jadwal_event FOREIGN KEY (event_id) REFERENCES public.event(id);


--
-- TOC entry 3233 (class 2606 OID 19389)
-- Name: jadwal fk_jadwal_panitia; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jadwal
    ADD CONSTRAINT fk_jadwal_panitia FOREIGN KEY (panitia_id) REFERENCES public.panitia(id);


--
-- TOC entry 3228 (class 2606 OID 19349)
-- Name: panitia fk_panitia_event; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.panitia
    ADD CONSTRAINT fk_panitia_event FOREIGN KEY (event_id) REFERENCES public.event(id);


--
-- TOC entry 3229 (class 2606 OID 19354)
-- Name: panitia fk_panitia_karyawan; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.panitia
    ADD CONSTRAINT fk_panitia_karyawan FOREIGN KEY (karyawan_id) REFERENCES public.karyawan(id);


-- Completed on 2025-12-18 13:33:58

--
-- PostgreSQL database dump complete
--

\unrestrict YFVLTAWafSd8SSbr5egfo6ClOoRqKIbARiBHhnWGZniyAEuR0sPgTc0myy88SAI

