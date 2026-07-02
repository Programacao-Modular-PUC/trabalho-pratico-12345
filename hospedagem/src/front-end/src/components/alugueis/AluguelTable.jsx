import ActionButton from '../ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../ApiState';
import StatusBadge from '../StatusBadge';
import { formatCurrency, formatDateTime, getApiErrorMessage } from '../../utils/apiError';

export default function AluguelTable({
  query,
  onEdit,
  onCancel,
  onDelete,
  onRecibo,
  onConfirmPagamento,
  cancelPending,
  deletePending,
  paymentPending,
}) {
  if (query.isLoading) return <LoadingState />;
  if (query.isError) return <ErrorState message={getApiErrorMessage(query.error)} onRetry={query.refetch} />;
  if (query.data.length === 0) return <EmptyState title="Nenhum aluguel cadastrado" />;

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead><tr className="text-left text-slate-500">
          <th className="py-2 pr-4 font-medium">Cliente</th><th className="py-2 pr-4 font-medium">Quarto</th>
          <th className="py-2 pr-4 font-medium">Período</th><th className="py-2 pr-4 font-medium">Diárias</th>
          <th className="py-2 pr-4 font-medium">Total</th><th className="py-2 pr-4 font-medium">Status</th>
          <th className="py-2 pr-4 font-medium">Pagamento</th><th className="py-2 text-right font-medium">Ações</th>
        </tr></thead>
        <tbody className="divide-y divide-slate-100">
          {query.data.map((aluguel) => {
            const cancelado = aluguel.status === 'CANCELADO';
            const pagamentoConfirmado = aluguel.pagamento?.status === 'CONFIRMADO';
            return (
              <tr key={aluguel.id}>
                <td className="py-3 pr-4 font-medium text-slate-950">{aluguel.cliente?.nome || '-'}</td>
                <td className="py-3 pr-4 text-slate-600">{aluguel.quarto?.tipo || '-'}</td>
                <td className="py-3 pr-4 text-slate-600">{formatDateTime(aluguel.dataEntrada)} a {formatDateTime(aluguel.dataSaida)}</td>
                <td className="py-3 pr-4 text-slate-600">{aluguel.numeroDeDiarias}</td>
                <td className="py-3 pr-4 font-medium text-slate-950">{formatCurrency(aluguel.valorTotal)}</td>
                <td className="py-3 pr-4"><StatusBadge status={aluguel.status || 'ATIVO'} /></td>
                <td className="py-3 pr-4 text-slate-600">{aluguel.pagamento?.status || 'PENDENTE'}</td>
                <td className="py-3"><div className="flex flex-wrap justify-end gap-2">
                  <ActionButton variant="secondary" onClick={() => onEdit(aluguel)} disabled={cancelado}>Editar</ActionButton>
                  <ActionButton variant="secondary" onClick={() => onCancel(aluguel.id)} disabled={cancelado || pagamentoConfirmado || cancelPending}>Cancelar</ActionButton>
                  <ActionButton variant="secondary" onClick={() => onRecibo(aluguel.id)}>Recibo</ActionButton>
                  <ActionButton variant="secondary" onClick={() => onConfirmPagamento(aluguel.pagamento?.id)} disabled={!aluguel.pagamento?.id || pagamentoConfirmado || paymentPending}>Confirmar pagamento</ActionButton>
                  <ActionButton variant="danger" onClick={() => onDelete(aluguel.id)} disabled={deletePending}>Remover</ActionButton>
                </div></td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
