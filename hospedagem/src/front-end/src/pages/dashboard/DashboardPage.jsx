import { Link } from 'react-router-dom';
import { ErrorState, LoadingState } from '../../components/ApiState';
import PageHeader from '../../components/PageHeader';
import { useAlugueis } from '../../hooks/useAluguel';
import { useClientes } from '../../hooks/useCliente';
import { useQuartos } from '../../hooks/useQuarto';
import { useResidencias } from '../../hooks/useResidencia';
import { getApiErrorMessage } from '../../utils/apiError';

function SummaryCard({ title, value, to, isLoading, error }) {
  if (isLoading) {
    return <LoadingState label={`Carregando ${title.toLowerCase()}...`} />;
  }

  if (error) {
    return <ErrorState message={getApiErrorMessage(error)} />;
  }

  return (
    <Link
      to={to}
      className="rounded-md border border-slate-200 bg-white p-5 transition hover:border-slate-300 hover:shadow-sm"
    >
      <div className="text-sm font-medium text-slate-500">{title}</div>
      <div className="mt-3 text-3xl font-semibold tracking-normal text-slate-950">{value}</div>
    </Link>
  );
}

export default function DashboardPage() {
  const residencias = useResidencias();
  const quartos = useQuartos();
  const clientes = useClientes();
  const alugueis = useAlugueis();

  return (
    <section>
      <PageHeader title="Painel" description="Resumo dos registros do sistema" />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <SummaryCard
          title="Residencias"
          value={residencias.data?.length ?? 0}
          to="/residencias"
          isLoading={residencias.isLoading}
          error={residencias.error}
        />
        <SummaryCard
          title="Quartos"
          value={quartos.data?.length ?? 0}
          to="/quartos"
          isLoading={quartos.isLoading}
          error={quartos.error}
        />
        <SummaryCard
          title="Clientes"
          value={clientes.data?.length ?? 0}
          to="/clientes"
          isLoading={clientes.isLoading}
          error={clientes.error}
        />
        <SummaryCard
          title="Alugueis"
          value={alugueis.data?.length ?? 0}
          to="/alugueis"
          isLoading={alugueis.isLoading}
          error={alugueis.error}
        />
      </div>
    </section>
  );
}
