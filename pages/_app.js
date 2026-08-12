import '../styles/globals.css';
import Head from 'next/head';
import Navbar from '../components/Navbar';

export default function App({ Component, pageProps }) {
  return (
    <>
      <Head>
        <title>Lexio — Hukuk Kataloğu, Simülasyon & HMGS Test Bankası</title>
        <meta name="description" content="Yargıtay kararlarından üretilmiş vakalar, kapsamlı hukuk ders kataloğu, HMGS ve Hakimlik uyumlu test soruları ile AI destekli pratik eğitimi." />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=DM+Sans:wght@300;400;500;600&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet" />
      </Head>
      <Navbar />
      <Component {...pageProps} />
    </>
  );
}
