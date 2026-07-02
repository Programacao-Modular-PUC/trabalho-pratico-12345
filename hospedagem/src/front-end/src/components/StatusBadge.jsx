export default function StatusBadge({ status }) {
  const cancelado = status === 'CANCELADO';
  const classes = cancelado
    ? 'border-red-200 bg-red-50 text-red-700'
    : 'border-emerald-200 bg-emerald-50 text-emerald-700';

  return (
    <span className={`inline-flex rounded-md border px-2 py-1 text-xs font-medium ${classes}`}>
      {cancelado ? 'Cancelado' : 'Ativo'}
    </span>
  );
}
