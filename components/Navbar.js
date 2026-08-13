import Link from 'next/link';
import { useRouter } from 'next/router';
import styles from '../styles/Navbar.module.css';

export default function Navbar() {
  const router = useRouter();

  const isActive = (path) => router.pathname === path || router.pathname.startsWith(path + '/');

  return (
    <nav className={styles.navbar}>
      <div className={styles.logo} onClick={() => router.push('/')}>
        Lex<span>io</span>
        <span className={styles.badge}>Hukuk Akademi</span>
      </div>

      <div className={styles.links}>
        <Link href="/dersler" className={`${styles.link} ${isActive('/dersler') || isActive('/ders') ? styles.active : ''}`}>
          📚 Dersler
        </Link>
        <Link href="/vakalar" className={`${styles.link} ${isActive('/vakalar') || isActive('/vaka') || isActive('/simulasyon') ? styles.active : ''}`}>
          ⚖️ Davalar
        </Link>
        <Link href="/testler" className={`${styles.link} ${isActive('/testler') || isActive('/test') ? styles.active : ''}`}>
          📝 Testler
        </Link>
        <Link href="/akis" className={`${styles.link} ${isActive('/akis') ? styles.active : ''}`}>
          💬 Sosyal Akış
        </Link>
        <Link href="/blog" className={`${styles.link} ${isActive('/blog') ? styles.active : ''}`}>
          📰 Blog & Haberler
        </Link>
      </div>
    </nav>
  );
}
