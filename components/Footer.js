import Link from 'next/link';
import styles from '../styles/Footer.module.css';

export default function Footer() {
  return (
    <footer className={styles.footer}>
      <span>© {new Date().getFullYear()} Lexio</span>
      <Link href="/gizlilik" className={styles.link}>Gizlilik Politikası</Link>
    </footer>
  );
}
