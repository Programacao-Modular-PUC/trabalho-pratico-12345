export function LoadingState({ label = 'Carregando dados...' }) {
  return (
    <div className="rounded-md border border-slate-200 bg-white p-4 text-sm text-slate-600">
      {label}
    </div>
  );
}

export function ErrorState({ message, onRetry }) {
  return (
    <div className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-800">
      <div className="font-medium">Falha ao carregar</div>
      <div className="mt-1">{message}</div>
      {onRetry ? (
        <button
          type="button"
          onClick={onRetry}
          className="mt-3 rounded-md bg-red-700 px-3 py-2 text-sm font-medium text-white hover:bg-red-800"
        >
          Tentar novamente
        </button>
      ) : null}
    </div>
  );
}

export function EmptyState({ title = 'Nenhum registro encontrado', description }) {
  return (
    <div className="rounded-md border border-dashed border-slate-300 bg-white p-6 text-center">
      <div className="text-sm font-medium text-slate-800">{title}</div>
      {description ? <div className="mt-1 text-sm text-slate-500">{description}</div> : null}
    </div>
  );
}
