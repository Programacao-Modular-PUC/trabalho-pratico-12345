import { EmptyState, ErrorState, LoadingState } from '../ApiState';
import StatusBadge from '../StatusBadge';
import { formatCurrency, formatDateTime, getApiErrorMessage } from '../../utils/apiError';

export default function ResidenciaHistorico({ residencia, query }) {
  if (!residencia) return <EmptyState title="Selecione uma residência para ver o histórico" />;
  return (
    <div className="rounded-md border border-slate-200 bg-white p-4">
      <h3 className="text-base font-semibold text-slate-950">Histórico de {residencia.nome}</h3>
      <div className="mt-4">
        {query.isLoading ? <LoadingState label="Carregando histórico..." /> : null}
        {query.isError ? <ErrorState message={getApiErrorMessage(query.error)} onRetry={query.refetch} /> : null}
        {query.isSuccess && query.data.length === 0 ? <EmptyState title="Nenhum aluguel nesta residência" /> : null}
        {query.isSuccess && query.data.length > 0 ? <div className="space-y-3">{query.data.map((aluguel) => (
          <div key={aluguel.id} className="rounded-md border border-slate-200 p-3 text-sm"><div className="flex justify-between gap-3"><span>{aluguel.cliente?.nome || '-'}</span><StatusBadge status={aluguel.status || 'ATIVO'} /></div><div className="mt-1 text-slate-600">{formatDateTime(aluguel.dataEntrada)} a {formatDateTime(aluguel.dataSaida)}</div><div className="mt-1 font-medium">{formatCurrency(aluguel.valorTotal)}</div></div>
        ))}</div> : null}
      </div>
    </div>
  );
}
