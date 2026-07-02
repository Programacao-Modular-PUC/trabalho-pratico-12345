import ActionButton from './ActionButton';
import { ErrorState, LoadingState } from './ApiState';
import { formatCurrency, formatDateTime, getApiErrorMessage } from '../utils/apiError';

export default function ReciboModal({ reciboQuery, onClose }) {
  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-950/40 p-4">
      <div className="w-full max-w-md rounded-md bg-white p-5 shadow-xl">
        <div className="mb-4 flex items-center justify-between gap-3">
          <h3 className="text-lg font-semibold text-slate-950">Recibo</h3>
          <ActionButton variant="secondary" onClick={onClose}>Fechar</ActionButton>
        </div>

        {reciboQuery.isLoading ? <LoadingState label="Gerando recibo..." /> : null}
        {reciboQuery.isError ? (
          <ErrorState message={getApiErrorMessage(reciboQuery.error)} onRetry={reciboQuery.refetch} />
        ) : null}
        {reciboQuery.isSuccess ? (
          <div className="space-y-3 rounded-md border border-slate-200 bg-slate-50 p-4 text-sm text-slate-700">
            <p>Data e horário de entrada: {formatDateTime(reciboQuery.data.dataEntrada)}</p>
            <p>Data e horário de saída: {formatDateTime(reciboQuery.data.dataSaida)}</p>
            <p>Número de diárias: {reciboQuery.data.numeroDeDiarias}</p>
            <p className="font-semibold">Total a pagar: {formatCurrency(reciboQuery.data.totalAPagar)}</p>
          </div>
        ) : null}
      </div>
    </div>
  );
}
