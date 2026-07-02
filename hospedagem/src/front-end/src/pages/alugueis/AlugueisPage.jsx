import { useState } from 'react';
import AluguelForm from '../../components/alugueis/AluguelForm';
import AluguelTable from '../../components/alugueis/AluguelTable';
import PageHeader from '../../components/PageHeader';
import ReciboModal from '../../components/ReciboModal';
import { useAlugueis, useCancelAluguel, useDeleteAluguel, useReciboAluguel } from '../../hooks/useAluguel';
import { useClientes } from '../../hooks/useCliente';
import { useConfirm } from '../../hooks/useConfirm';
import { useConfirmPagamento } from '../../hooks/usePagamento';
import { useQuartos } from '../../hooks/useQuarto';
import { getApiErrorMessage } from '../../utils/apiError';

export default function AlugueisPage() {
  const [editing, setEditing] = useState(null);
  const [reciboId, setReciboId] = useState(null);
  const confirmar = useConfirm();
  const alugueis = useAlugueis();
  const clientes = useClientes();
  const quartos = useQuartos();
  const deleteMutation = useDeleteAluguel();
  const cancelMutation = useCancelAluguel();
  const paymentMutation = useConfirmPagamento();
  const recibo = useReciboAluguel(reciboId);

  async function executar(mensagem, mutation, id) {
    if (confirmar(mensagem)) await mutation.mutateAsync(id);
  }

  const erro = deleteMutation.error || cancelMutation.error || paymentMutation.error;

  return (
    <section>
      <PageHeader title="Aluguéis" description="Reservas, pagamentos e recibos" />
      <div className="grid gap-5 xl:grid-cols-[minmax(0,1fr)_460px]">
        <div className="rounded-md border border-slate-200 bg-white p-4">
          <h3 className="mb-4 text-base font-semibold text-slate-950">Lista de aluguéis</h3>
          <AluguelTable
            query={alugueis}
            onEdit={setEditing}
            onCancel={(id) => executar('Cancelar este aluguel?', cancelMutation, id)}
            onDelete={(id) => executar('Remover este aluguel?', deleteMutation, id)}
            onRecibo={setReciboId}
            onConfirmPagamento={(id) => executar('Confirmar este pagamento?', paymentMutation, id)}
            cancelPending={cancelMutation.isPending}
            deletePending={deleteMutation.isPending}
            paymentPending={paymentMutation.isPending}
          />
          {erro ? <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">{getApiErrorMessage(erro)}</div> : null}
        </div>
        <AluguelForm editing={editing} onDone={() => setEditing(null)} clientesQuery={clientes} quartosQuery={quartos} />
      </div>
      {reciboId ? <ReciboModal reciboQuery={recibo} onClose={() => setReciboId(null)} /> : null}
    </section>
  );
}
