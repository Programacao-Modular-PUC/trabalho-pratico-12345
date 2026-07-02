import { useState } from 'react';
import QuartoForm from '../../components/quartos/QuartoForm';
import QuartoTable from '../../components/quartos/QuartoTable';
import PageHeader from '../../components/PageHeader';
import { useConfirm } from '../../hooks/useConfirm';
import { useDeleteQuarto, useQuartos } from '../../hooks/useQuarto';
import { useResidencias } from '../../hooks/useResidencia';
import { getApiErrorMessage } from '../../utils/apiError';

export default function QuartosPage() {
  const [editing, setEditing] = useState(null);
  const [tipoFiltro, setTipoFiltro] = useState('');
  const confirmar = useConfirm();
  const quartos = useQuartos(tipoFiltro);
  const residencias = useResidencias();
  const deleteMutation = useDeleteQuarto();

  async function handleDelete(id) {
    if (confirmar('Remover este quarto?')) await deleteMutation.mutateAsync(id);
  }

  return (
    <section>
      <PageHeader title="Quartos" description="Cadastro de unidades para aluguel" />
      <div className="grid gap-5 xl:grid-cols-[minmax(0,1fr)_460px]">
        <div className="rounded-md border border-slate-200 bg-white p-4">
          <div className="mb-4 flex flex-wrap items-end justify-between gap-3">
            <h3 className="text-base font-semibold text-slate-950">Lista de quartos</h3>
            <label className="min-w-48 text-sm text-slate-700">Tipo
              <select value={tipoFiltro} onChange={(event) => setTipoFiltro(event.target.value)} className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm">
                <option value="">Todos</option><option value="INDIVIDUAL">Individual</option><option value="DUPLO">Duplo</option><option value="FAMILIA">Família</option>
              </select>
            </label>
          </div>
          <QuartoTable query={quartos} tipoFiltro={tipoFiltro} onEdit={setEditing} onDelete={handleDelete} deletePending={deleteMutation.isPending} />
          {deleteMutation.error ? <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">{getApiErrorMessage(deleteMutation.error)}</div> : null}
        </div>
        <QuartoForm editing={editing} onDone={() => setEditing(null)} residenciasQuery={residencias} />
      </div>
    </section>
  );
}
