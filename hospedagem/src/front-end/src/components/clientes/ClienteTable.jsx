import ActionButton from '../ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../ApiState';
import { getApiErrorMessage } from '../../utils/apiError';

export default function ClienteTable({ query, onHistorico, onEdit, onDelete, deletePending }) {
  if (query.isLoading) return <LoadingState />;
  if (query.isError) return <ErrorState message={getApiErrorMessage(query.error)} onRetry={query.refetch} />;
  if (query.data.length === 0) return <EmptyState title="Nenhum cliente cadastrado" />;
  return (
    <div className="overflow-x-auto"><table className="min-w-full divide-y divide-slate-200 text-sm">
      <thead><tr className="text-left text-slate-500">
        <th className="py-2 pr-4 font-medium">Nome</th><th className="py-2 pr-4 font-medium">CPF</th>
        <th className="py-2 pr-4 font-medium">E-mail</th><th className="py-2 pr-4 font-medium">Telefone</th>
        <th className="py-2 text-right font-medium">Ações</th>
      </tr></thead>
      <tbody className="divide-y divide-slate-100">{query.data.map((cliente) => (
        <tr key={cliente.id}>
          <td className="py-3 pr-4 font-medium text-slate-950">{cliente.nome}</td><td className="py-3 pr-4 text-slate-600">{cliente.cpf}</td>
          <td className="py-3 pr-4 text-slate-600">{cliente.email || '-'}</td><td className="py-3 pr-4 text-slate-600">{cliente.telefone || '-'}</td>
          <td className="py-3"><div className="flex justify-end gap-2">
            <ActionButton variant="secondary" onClick={() => onHistorico(cliente.id)}>Histórico</ActionButton>
            <ActionButton variant="secondary" onClick={() => onEdit(cliente)}>Editar</ActionButton>
            <ActionButton variant="danger" onClick={() => onDelete(cliente.id)} disabled={deletePending}>Remover</ActionButton>
          </div></td>
        </tr>
      ))}</tbody>
    </table></div>
  );
}
