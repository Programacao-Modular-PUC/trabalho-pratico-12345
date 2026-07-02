import { useState } from 'react';
import ClienteForm from '../../components/clientes/ClienteForm';
import ClienteHistorico from '../../components/clientes/ClienteHistorico';
import ClienteTable from '../../components/clientes/ClienteTable';
import PageHeader from '../../components/PageHeader';
import { useAlugueisByCliente } from '../../hooks/useAluguel';
import { useClientes, useDeleteCliente } from '../../hooks/useCliente';
import { useConfirm } from '../../hooks/useConfirm';
import { getApiErrorMessage } from '../../utils/apiError';

export default function ClientesPage() {
  const [editing, setEditing] = useState(null);
  const [clienteHistoricoId, setClienteHistoricoId] = useState(null);
  const confirmar = useConfirm();
  const clientes = useClientes();
  const historico = useAlugueisByCliente(clienteHistoricoId);
  const deleteMutation = useDeleteCliente();
  const clienteSelecionado = clientes.data?.find((cliente) => cliente.id === clienteHistoricoId);

  async function handleDelete(id) {
    if (confirmar('Remover este cliente?')) {
      await deleteMutation.mutateAsync(id);
      if (clienteHistoricoId === id) setClienteHistoricoId(null);
    }
  }

  return (
    <section>
      <PageHeader title="Clientes" description="Cadastro de hóspedes" />
      <div className="grid gap-5 lg:grid-cols-[minmax(0,1fr)_420px]">
        <div className="rounded-md border border-slate-200 bg-white p-4">
          <h3 className="mb-4 text-base font-semibold text-slate-950">Lista de clientes</h3>
          <ClienteTable query={clientes} onHistorico={setClienteHistoricoId} onEdit={setEditing} onDelete={handleDelete} deletePending={deleteMutation.isPending} />
          {deleteMutation.error ? <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">{getApiErrorMessage(deleteMutation.error)}</div> : null}
        </div>
        <div className="grid gap-5">
          <ClienteForm editing={editing} onDone={() => setEditing(null)} />
          <ClienteHistorico cliente={clienteSelecionado} historicoQuery={historico} />
        </div>
      </div>
    </section>
  );
}
