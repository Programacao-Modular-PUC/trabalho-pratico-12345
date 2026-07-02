import { useState } from 'react';
import ResidenciaForm from '../../components/residencias/ResidenciaForm';
import ResidenciaHistorico from '../../components/residencias/ResidenciaHistorico';
import ResidenciaTable from '../../components/residencias/ResidenciaTable';
import PageHeader from '../../components/PageHeader';
import { useAlugueisByResidencia } from '../../hooks/useAluguel';
import { useConfirm } from '../../hooks/useConfirm';
import { useDeleteResidencia, useResidencias } from '../../hooks/useResidencia';
import { getApiErrorMessage } from '../../utils/apiError';

export default function ResidenciasPage() {
  const [editing, setEditing] = useState(null);
  const [historicoId, setHistoricoId] = useState(null);
  const confirmar = useConfirm();
  const residencias = useResidencias();
  const historico = useAlugueisByResidencia(historicoId);
  const deleteMutation = useDeleteResidencia();
  const selecionada = residencias.data?.find((item) => item.id === historicoId);

  async function handleDelete(id) {
    if (confirmar('Remover esta residência?')) await deleteMutation.mutateAsync(id);
  }

  return (
    <section>
      <PageHeader title="Residências" description="Cadastro e histórico dos locais de hospedagem" />
      <div className="grid gap-5 lg:grid-cols-[minmax(0,1fr)_420px]">
        <div className="rounded-md border border-slate-200 bg-white p-4">
          <h3 className="mb-4 text-base font-semibold text-slate-950">Lista de residências</h3>
          <ResidenciaTable query={residencias} onHistorico={setHistoricoId} onEdit={setEditing} onDelete={handleDelete} deletePending={deleteMutation.isPending} />
          {deleteMutation.error ? <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">{getApiErrorMessage(deleteMutation.error)}</div> : null}
        </div>
        <div className="grid gap-5"><ResidenciaForm editing={editing} onDone={() => setEditing(null)} /><ResidenciaHistorico residencia={selecionada} query={historico} /></div>
      </div>
    </section>
  );
}
