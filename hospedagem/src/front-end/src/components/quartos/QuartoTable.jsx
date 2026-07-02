import ActionButton from '../ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../ApiState';
import { formatCurrency, getApiErrorMessage } from '../../utils/apiError';
import { getQuartoResumo } from '../../utils/quarto';

export default function QuartoTable({ query, tipoFiltro, onEdit, onDelete, deletePending }) {
  if (query.isLoading) return <LoadingState />;
  if (query.isError) return <ErrorState message={getApiErrorMessage(query.error)} onRetry={query.refetch} />;
  if (query.data.length === 0) {
    return <EmptyState title={tipoFiltro ? 'Nenhum quarto encontrado para este tipo' : 'Nenhum quarto cadastrado'} />;
  }
  return (
    <div className="overflow-x-auto"><table className="min-w-full divide-y divide-slate-200 text-sm">
      <thead><tr className="text-left text-slate-500">
        <th className="py-2 pr-4 font-medium">Tipo</th><th className="py-2 pr-4 font-medium">Residência</th>
        <th className="py-2 pr-4 font-medium">Valor base</th><th className="py-2 pr-4 font-medium">Detalhes</th>
        <th className="py-2 text-right font-medium">Ações</th>
      </tr></thead>
      <tbody className="divide-y divide-slate-100">{query.data.map((quarto) => (
        <tr key={quarto.id}>
          <td className="py-3 pr-4 font-medium text-slate-950">{quarto.tipo}</td>
          <td className="py-3 pr-4 text-slate-600">{quarto.residencia?.nome || '-'}</td>
          <td className="py-3 pr-4 text-slate-600">{formatCurrency(quarto.valorBase)}</td>
          <td className="py-3 pr-4 text-slate-600">{getQuartoResumo(quarto)}</td>
          <td className="py-3"><div className="flex justify-end gap-2">
            <ActionButton variant="secondary" onClick={() => onEdit(quarto)}>Editar</ActionButton>
            <ActionButton variant="danger" onClick={() => onDelete(quarto.id)} disabled={deletePending}>Remover</ActionButton>
          </div></td>
        </tr>
      ))}</tbody>
    </table></div>
  );
}
