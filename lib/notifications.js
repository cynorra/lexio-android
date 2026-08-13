/**
 * Web Notification & Alert Toast Manager
 * Handles both In-App Banner Alerts and Browser Push Notifications (even when tab/app is closed).
 */

export function requestBrowserNotificationPermission() {
  if (typeof window !== 'undefined' && 'Notification' in window) {
    if (Notification.permission === 'default') {
      Notification.requestPermission().then(permission => {
        if (permission === 'granted') {
          sendBrowserPushNotification(
            '🔔 Lexio Hukuk Bildirimleri Aktif!',
            'Günün yeni testleri ve Yargıtay emsal karar duyurularını anlık olarak alacaksınız.'
          );
        }
      });
    }
  }
}

export function sendBrowserPushNotification(title, body) {
  if (typeof window !== 'undefined' && 'Notification' in window && Notification.permission === 'granted') {
    try {
      new Notification(title, {
        body,
        icon: '/play_store_icon.png',
        badge: '/play_store_icon.png'
      });
    } catch (e) {
      console.error('Web push error:', e);
    }
  }
}

export const SYSTEM_NOTIFICATIONS = [
  {
    id: 'notif-1',
    type: 'warning',
    title: '⚠️ HMGS & Hakimlik Sınav Hatırlatması',
    message: '2026 HMGS sınav barajı %70 olarak uygulanacaktır. Günlük testlerinizi aksatmayın!',
    timestamp: 'Az önce'
  },
  {
    id: 'notif-2',
    type: 'info',
    title: '⚖️ Yargıtay Yeni Emsal Karar Yayımlandı',
    message: 'Ceza Genel Kurulu 2021/1-142 E. sayılı kasten yaralama - öldürmeye teşebbüs ayrım kararı veritabanına eklendi.',
    timestamp: '1 saat önce'
  },
  {
    id: 'notif-3',
    type: 'success',
    title: '🔥 Günün Ortak Sınavı Hazır',
    message: 'Bugünün 5 şıklı ÖSYM & HMGS uyumlu yeni soruları arka planda hazırlandı. (+100 Puan)',
    timestamp: 'Bugün'
  }
];
