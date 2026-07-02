import ActionButton from '../ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../ApiState';
import { getApiErrorMessage } from '../../utils/apiError';

export default function ResidenciaTable({ query, onHistorico, onEdit, onDelete, deletePending }) {
  if (query.isLoading) return <LoadingState />;
  if (query.isError) return <ErrorState message={getApiErrorMessage(query.error)} onRetry={query.refetch} />;
  if (query.data.length === 0) return <EmptyState title="Nenhuma residência cadastrada" />;
  return (
    <div className="overflow-x-auto"><table className="min-w-full divide-y divide-slate-200 text-sm">
      <thead><tr className="text-left text-slate-500"><th className="py-2 pr-4 font-medium">Nome</th><th className="py-2 pr-4 font-medium">Endereço</th><th className="py-2 pr-4 font-medium">Quartos</th><th className="py-2 text-right font-medium">Ações</th></tr></thead>
      <tbody className="divide-y divide-slate-100">{query.data.map((residencia) => (
        <tr key={residencia.id}><td className="py-3 pr-4 font-medium text-slate-950">{residencia.nome}</td><td className="py-3 pr-4 text-slate-600">{residencia.endereco}</td><td className="py-3 pr-4 text-slate-600">{residencia.quartos?.length ?? 0}</td><td className="py-3"><div className="flex justify-end gap-2"><ActionButton variant="secondary" onClick={() => onHistorico(residencia.id)}>Histórico</ActionButton><ActionButton variant="secondary" onClick={() => onEdit(residencia)}>Editar</ActionButton><ActionButton variant="danger" onClick={() => onDelete(residencia.id)} disabled={deletePending}>Remover</ActionButton></div></td></tr>
      ))}</tbody>
    </table></div>
  );
}
