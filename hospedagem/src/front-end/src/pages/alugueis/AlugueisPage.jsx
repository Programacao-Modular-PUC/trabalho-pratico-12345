import { useEffect, useMemo, useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import ActionButton from '../../components/ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../../components/ApiState';
import FormField from '../../components/FormField';
import PageHeader from '../../components/PageHeader';
import {
  useAlugueis,
  useCancelAluguel,
  useCreateAluguel,
  useDeleteAluguel,
  useUpdateAluguel,
} from '../../hooks/useAluguel';
import { useClientes } from '../../hooks/useCliente';
import { useQuartos } from '../../hooks/useQuarto';
import { formatCurrency, getApiErrorMessage } from '../../utils/apiError';

function dateValue(daysAhead = 0) {
  const date = new Date();
  date.setDate(date.getDate() + daysAhead);
  return date.toISOString().slice(0, 10);
}

const aluguelSchema = z
  .object({
    clienteId: z.coerce.number().min(1, 'Selecione um cliente.'),
    quartoId: z.coerce.number().min(1, 'Selecione um quarto.'),
    dataEntrada: z.string().min(1, 'Informe a data de entrada.'),
    dataSaida: z.string().min(1, 'Informe a data de saida.'),
    numeroDeHospedes: z.coerce.number().min(1, 'Informe pelo menos 1 hospede.'),
    solicitouBerco: z.boolean(),
  })
  .refine((values) => values.dataSaida > values.dataEntrada, {
    path: ['dataSaida'],
    message: 'A saida deve ser posterior a entrada.',
  });

const emptyValues = {
  clienteId: '',
  quartoId: '',
  dataEntrada: dateValue(0),
  dataSaida: dateValue(1),
  numeroDeHospedes: 1,
  solicitouBerco: false,
};

function getQuartoLabel(quarto) {
  const residencia = quarto.residencia?.nome ? ` - ${quarto.residencia.nome}` : '';
  return `${quarto.tipo}${residencia}`;
}

function StatusBadge({ status }) {
  const cancelado = status === 'CANCELADO';
  const classes = cancelado
    ? 'border-red-200 bg-red-50 text-red-700'
    : 'border-emerald-200 bg-emerald-50 text-emerald-700';

  return (
    <span className={`inline-flex rounded-md border px-2 py-1 text-xs font-medium ${classes}`}>
      {cancelado ? 'Cancelado' : 'Ativo'}
    </span>
  );
}

function AluguelForm({ editing, onDone, clientesQuery, quartosQuery }) {
  const createMutation = useCreateAluguel();
  const updateMutation = useUpdateAluguel();
  const isSaving = createMutation.isPending || updateMutation.isPending;
  const mutationError = createMutation.error || updateMutation.error;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(aluguelSchema),
    defaultValues: emptyValues,
  });

  useEffect(() => {
    if (editing) {
      reset({
        clienteId: editing.cliente?.id || '',
        quartoId: editing.quarto?.id || '',
        dataEntrada: editing.dataEntrada || dateValue(0),
        dataSaida: editing.dataSaida || dateValue(1),
        numeroDeHospedes: editing.numeroDeHospedes || 1,
        solicitouBerco: Boolean(editing.solicitouBerco),
      });
    } else {
      reset(emptyValues);
    }
  }, [editing, reset]);

  const clientes = clientesQuery.data || [];
  const quartos = quartosQuery.data || [];

  const canRenderForm = useMemo(() => {
    return clientesQuery.isSuccess && quartosQuery.isSuccess && clientes.length > 0 && quartos.length > 0;
  }, [clientes.length, clientesQuery.isSuccess, quartos.length, quartosQuery.isSuccess]);

  async function onSubmit(values) {
    const payload = {
      clienteId: Number(values.clienteId),
      quartoId: Number(values.quartoId),
      dataEntrada: values.dataEntrada,
      dataSaida: values.dataSaida,
      numeroDeHospedes: Number(values.numeroDeHospedes),
      solicitouBerco: Boolean(values.solicitouBerco),
    };

    if (editing) {
      await updateMutation.mutateAsync({ id: editing.id, payload });
    } else {
      await createMutation.mutateAsync(payload);
    }

    reset(emptyValues);
    onDone();
  }

  if (clientesQuery.isLoading || quartosQuery.isLoading) {
    return <LoadingState label="Carregando dados do aluguel..." />;
  }

  if (clientesQuery.isError) {
    return <ErrorState message={getApiErrorMessage(clientesQuery.error)} onRetry={clientesQuery.refetch} />;
  }

  if (quartosQuery.isError) {
    return <ErrorState message={getApiErrorMessage(quartosQuery.error)} onRetry={quartosQuery.refetch} />;
  }

  if (!canRenderForm) {
    return <EmptyState title="Cadastre cliente e quarto antes de criar alugueis" />;
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="rounded-md border border-slate-200 bg-white p-4">
      <div className="mb-4 flex items-center justify-between gap-3">
        <h3 className="text-base font-semibold text-slate-950">
          {editing ? 'Editar aluguel' : 'Novo aluguel'}
        </h3>
        {editing ? (
          <ActionButton variant="secondary" onClick={onDone}>
            Cancelar
          </ActionButton>
        ) : null}
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <FormField label="Cliente" error={errors.clienteId?.message}>
          <select
            {...register('clienteId')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          >
            <option value="">Selecione</option>
            {clientes.map((cliente) => (
              <option key={cliente.id} value={cliente.id}>
                {cliente.nome}
              </option>
            ))}
          </select>
        </FormField>

        <FormField label="Quarto" error={errors.quartoId?.message}>
          <select
            {...register('quartoId')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          >
            <option value="">Selecione</option>
            {quartos.map((quarto) => (
              <option key={quarto.id} value={quarto.id}>
                {getQuartoLabel(quarto)}
              </option>
            ))}
          </select>
        </FormField>

        <FormField label="Entrada" error={errors.dataEntrada?.message}>
          <input
            type="date"
            {...register('dataEntrada')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          />
        </FormField>

        <FormField label="Saida" error={errors.dataSaida?.message}>
          <input
            type="date"
            {...register('dataSaida')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          />
        </FormField>

        <FormField label="Hospedes" error={errors.numeroDeHospedes?.message}>
          <input
            type="number"
            min="1"
            {...register('numeroDeHospedes')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          />
        </FormField>

        <label className="flex items-center gap-2 self-end rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-700">
          <input type="checkbox" {...register('solicitouBerco')} className="h-4 w-4" />
          Berco
        </label>
      </div>

      {mutationError ? (
        <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">
          {getApiErrorMessage(mutationError)}
        </div>
      ) : null}

      <div className="mt-4">
        <ActionButton type="submit" disabled={isSaving}>
          {isSaving ? 'Salvando...' : 'Salvar'}
        </ActionButton>
      </div>
    </form>
  );
}

export default function AlugueisPage() {
  const [editing, setEditing] = useState(null);
  const alugueis = useAlugueis();
  const clientes = useClientes();
  const quartos = useQuartos();
  const deleteMutation = useDeleteAluguel();
  const cancelMutation = useCancelAluguel();

  async function handleDelete(id) {
    const confirmed = window.confirm('Remover este aluguel?');
    if (confirmed) {
      await deleteMutation.mutateAsync(id);
    }
  }

  async function handleCancel(id) {
    const confirmed = window.confirm('Cancelar este aluguel?');
    if (confirmed) {
      await cancelMutation.mutateAsync(id);
    }
  }

  return (
    <section>
      <PageHeader title="Alugueis" description="Reservas com valor calculado pelo backend" />

      <div className="grid gap-5 xl:grid-cols-[minmax(0,1fr)_460px]">
        <div className="rounded-md border border-slate-200 bg-white p-4">
          <h3 className="mb-4 text-base font-semibold text-slate-950">Lista de alugueis</h3>

          {alugueis.isLoading ? <LoadingState /> : null}
          {alugueis.isError ? (
            <ErrorState message={getApiErrorMessage(alugueis.error)} onRetry={alugueis.refetch} />
          ) : null}
          {alugueis.isSuccess && alugueis.data.length === 0 ? (
            <EmptyState title="Nenhum aluguel cadastrado" />
          ) : null}

          {alugueis.isSuccess && alugueis.data.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-slate-200 text-sm">
                <thead>
                  <tr className="text-left text-slate-500">
                    <th className="py-2 pr-4 font-medium">Cliente</th>
                    <th className="py-2 pr-4 font-medium">Quarto</th>
                    <th className="py-2 pr-4 font-medium">Periodo</th>
                    <th className="py-2 pr-4 font-medium">Hospedes</th>
                    <th className="py-2 pr-4 font-medium">Total</th>
                    <th className="py-2 pr-4 font-medium">Status</th>
                    <th className="py-2 text-right font-medium">Acoes</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {alugueis.data.map((aluguel) => {
                    const status = aluguel.status || 'ATIVO';
                    const cancelado = status === 'CANCELADO';

                    return (
                      <tr key={aluguel.id}>
                        <td className="py-3 pr-4 font-medium text-slate-950">{aluguel.cliente?.nome || '-'}</td>
                        <td className="py-3 pr-4 text-slate-600">{aluguel.quarto?.tipo || '-'}</td>
                        <td className="py-3 pr-4 text-slate-600">
                          {aluguel.dataEntrada} a {aluguel.dataSaida}
                        </td>
                        <td className="py-3 pr-4 text-slate-600">{aluguel.numeroDeHospedes}</td>
                        <td className="py-3 pr-4 font-medium text-slate-950">{formatCurrency(aluguel.valorTotal)}</td>
                        <td className="py-3 pr-4">
                          <StatusBadge status={status} />
                        </td>
                        <td className="py-3">
                          <div className="flex justify-end gap-2">
                            <ActionButton
                              variant="secondary"
                              onClick={() => setEditing(aluguel)}
                              disabled={cancelado}
                            >
                              Editar
                            </ActionButton>
                            <ActionButton
                              variant="secondary"
                              onClick={() => handleCancel(aluguel.id)}
                              disabled={cancelado || cancelMutation.isPending}
                            >
                              Cancelar
                            </ActionButton>
                            <ActionButton
                              variant="danger"
                              onClick={() => handleDelete(aluguel.id)}
                              disabled={deleteMutation.isPending}
                            >
                              Remover
                            </ActionButton>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : null}

          {deleteMutation.error ? (
            <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">
              {getApiErrorMessage(deleteMutation.error)}
            </div>
          ) : null}

          {cancelMutation.error ? (
            <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">
              {getApiErrorMessage(cancelMutation.error)}
            </div>
          ) : null}
        </div>

        <AluguelForm
          editing={editing}
          onDone={() => setEditing(null)}
          clientesQuery={clientes}
          quartosQuery={quartos}
        />
      </div>
    </section>
  );
}
