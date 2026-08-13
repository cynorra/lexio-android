import { useState, useEffect } from 'react';
import { SYSTEM_NOTIFICATIONS, requestBrowserNotificationPermission } from '../lib/notifications';

export default function NotificationToast() {
  const [notifications, setNotifications] = useState(SYSTEM_NOTIFICATIONS);
  const [closedIds, setClosedIds] = useState([]);

  useEffect(() => {
    // Request browser push permission on mount
    requestBrowserNotificationPermission();
  }, []);

  const dismissNotification = (id) => {
    setClosedIds(prev => [...prev, id]);
  };

  const activeNotifs = notifications.filter(n => !closedIds.includes(n.id));

  if (activeNotifs.length === 0) return null;

  return (
    <div style={{
      position: 'fixed',
      bottom: '1.5rem',
      right: '1.5rem',
      zIndex: 9999,
      display: 'flex',
      flexDirection: 'column',
      gap: '0.8rem',
      maxWidth: '380px',
      width: 'calc(100% - 3rem)'
    }}>
      {activeNotifs.map(notif => {
        const isWarning = notif.type === 'warning';
        const isSuccess = notif.type === 'success';

        return (
          <div
            key={notif.id}
            style={{
              background: 'rgba(22, 28, 42, 0.95)',
              backdropFilter: 'blur(10px)',
              border: `1px solid ${isWarning ? '#FFA000' : isSuccess ? '#4CAF50' : '#D4AF37'}`,
              borderLeft: `5px solid ${isWarning ? '#FFA000' : isSuccess ? '#4CAF50' : '#D4AF37'}`,
              borderRadius: '10px',
              padding: '1rem 1.2rem',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.4)',
              color: '#F9F6F0',
              animation: 'fadeInUp 0.3s ease'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
              <span style={{
                fontWeight: 700,
                fontSize: '0.92rem',
                color: isWarning ? '#FFD54F' : isSuccess ? '#81C784' : '#D4AF37'
              }}>
                {notif.title}
              </span>
              <button
                onClick={() => dismissNotification(notif.id)}
                style={{
                  background: 'transparent',
                  border: 'none',
                  color: 'rgba(249, 246, 240, 0.5)',
                  cursor: 'pointer',
                  fontSize: '1.1rem',
                  lineHeight: 1
                }}
              >
                ×
              </button>
            </div>
            <p style={{ margin: 0, fontSize: '0.85rem', color: 'rgba(249, 246, 240, 0.85)', lineHeight: 1.45 }}>
              {notif.message}
            </p>
            <span style={{ fontSize: '0.72rem', color: 'rgba(249, 246, 240, 0.4)', marginTop: '0.4rem', display: 'block' }}>
              {notif.timestamp}
            </span>
          </div>
        );
      })}
    </div>
  );
}
