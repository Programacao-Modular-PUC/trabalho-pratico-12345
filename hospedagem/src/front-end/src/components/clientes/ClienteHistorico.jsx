import { EmptyState, ErrorState, LoadingState } from '../ApiState';
import StatusBadge from '../StatusBadge';
import { formatCurrency, formatDateTime, getApiErrorMessage } from '../../utils/apiError';

export default function ClienteHistorico({ cliente, historicoQuery }) {
  if (!cliente) return <EmptyState title="Selecione um cliente para ver o histórico" />;
  return (
    <div className="rounded-md border border-slate-200 bg-white p-4">
      <h3 className="text-base font-semibold text-slate-950">Histórico de {cliente.nome}</h3>
      <div className="mt-4">
        {historicoQuery.isLoading ? <LoadingState label="Carregando histórico..." /> : null}
        {historicoQuery.isError ? <ErrorState message={getApiErrorMessage(historicoQuery.error)} onRetry={historicoQuery.refetch} /> : null}
        {historicoQuery.isSuccess && historicoQuery.data.length === 0 ? <EmptyState title="Nenhum aluguel para este cliente" /> : null}
        {historicoQuery.isSuccess && historicoQuery.data.length > 0 ? (
          <div className="overflow-x-auto"><table className="min-w-full divide-y divide-slate-200 text-sm">
            <thead><tr className="text-left text-slate-500"><th className="py-2 pr-4 font-medium">Quarto</th><th className="py-2 pr-4 font-medium">Período</th><th className="py-2 pr-4 font-medium">Total</th><th className="py-2 font-medium">Status</th></tr></thead>
            <tbody className="divide-y divide-slate-100">{historicoQuery.data.map((aluguel) => (
              <tr key={aluguel.id}><td className="py-3 pr-4 text-slate-600">{aluguel.quarto?.tipo || '-'}</td><td className="py-3 pr-4 text-slate-600">{formatDateTime(aluguel.dataEntrada)} a {formatDateTime(aluguel.dataSaida)}</td><td className="py-3 pr-4 font-medium text-slate-950">{formatCurrency(aluguel.valorTotal)}</td><td className="py-3"><StatusBadge status={aluguel.status || 'ATIVO'} /></td></tr>
            ))}</tbody>
          </table></div>
        ) : null}
      </div>
    </div>
  );
}
