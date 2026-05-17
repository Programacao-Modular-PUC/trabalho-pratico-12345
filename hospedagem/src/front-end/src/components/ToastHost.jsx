import { useEffect, useState } from 'react';

export default function ToastHost() {
  const [toast, setToast] = useState(null);

  useEffect(() => {
    function handleToast(event) {
      setToast(event.detail);
      window.setTimeout(() => setToast(null), 3500);
    }

    window.addEventListener('app:toast', handleToast);
    return () => window.removeEventListener('app:toast', handleToast);
  }, []);

  if (!toast) {
    return null;
  }

  const isError = toast.type === 'error';

  return (
    <div className="fixed bottom-4 right-4 z-50 max-w-sm rounded-md border bg-white px-4 py-3 text-sm shadow-lg">
      <div className={isError ? 'font-medium text-red-800' : 'font-medium text-slate-900'}>
        {toast.message}
      </div>
    </div>
  );
}
