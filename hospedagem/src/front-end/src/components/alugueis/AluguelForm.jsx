import { useEffect, useMemo } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import ActionButton from '../ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../ApiState';
import FormField from '../FormField';
import { useCreateAluguel, useUpdateAluguel } from '../../hooks/useAluguel';
import { aluguelSchema } from '../../schemas/aluguelSchema';
import { getApiErrorMessage } from '../../utils/apiError';

function dateTimeValue(daysAhead = 0) {
  const date = new Date();
  date.setDate(date.getDate() + daysAhead);
  date.setHours(12, 0, 0, 0);
  const pad = (value) => String(value).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function valoresVazios() {
  return {
    clienteId: '',
    quartoId: '',
    dataEntrada: dateTimeValue(0),
    dataSaida: dateTimeValue(1),
    numeroDeHospedes: 1,
    solicitouBerco: false,
  };
}

function getQuartoLabel(quarto) {
  const residencia = quarto.residencia?.nome ? ` - ${quarto.residencia.nome}` : '';
  return `${quarto.tipo}${residencia}`;
}

export default function AluguelForm({ editing, onDone, clientesQuery, quartosQuery }) {
  const createMutation = useCreateAluguel();
  const updateMutation = useUpdateAluguel();
  const isSaving = createMutation.isPending || updateMutation.isPending;
  const mutationError = createMutation.error || updateMutation.error;
  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: zodResolver(aluguelSchema),
    defaultValues: valoresVazios(),
  });

  useEffect(() => {
    const padrao = valoresVazios();
    reset(editing ? {
      clienteId: editing.cliente?.id || '',
      quartoId: editing.quarto?.id || '',
      dataEntrada: editing.dataEntrada?.slice(0, 16) || padrao.dataEntrada,
      dataSaida: editing.dataSaida?.slice(0, 16) || padrao.dataSaida,
      numeroDeHospedes: editing.numeroDeHospedes || 1,
      solicitouBerco: Boolean(editing.solicitouBerco),
    } : padrao);
  }, [editing, reset]);

  const clientes = clientesQuery.data || [];
  const quartos = quartosQuery.data || [];
  const canRenderForm = useMemo(
    () => clientesQuery.isSuccess && quartosQuery.isSuccess && clientes.length > 0 && quartos.length > 0,
    [clientes.length, clientesQuery.isSuccess, quartos.length, quartosQuery.isSuccess],
  );

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
    reset(valoresVazios());
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
    return <EmptyState title="Cadastre cliente e quarto antes de criar aluguéis" />;
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="rounded-md border border-slate-200 bg-white p-4">
      <div className="mb-4 flex items-center justify-between gap-3">
        <h3 className="text-base font-semibold text-slate-950">{editing ? 'Editar aluguel' : 'Novo aluguel'}</h3>
        {editing ? <ActionButton variant="secondary" onClick={onDone}>Cancelar</ActionButton> : null}
      </div>
      <div className="grid gap-4 md:grid-cols-2">
        <FormField label="Cliente" error={errors.clienteId?.message}>
          <select {...register('clienteId')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm">
            <option value="">Selecione</option>
            {clientes.map((cliente) => <option key={cliente.id} value={cliente.id}>{cliente.nome}</option>)}
          </select>
        </FormField>
        <FormField label="Quarto" error={errors.quartoId?.message}>
          <select {...register('quartoId')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm">
            <option value="">Selecione</option>
            {quartos.map((quarto) => <option key={quarto.id} value={quarto.id}>{getQuartoLabel(quarto)}</option>)}
          </select>
        </FormField>
        <FormField label="Entrada" error={errors.dataEntrada?.message}>
          <input type="datetime-local" {...register('dataEntrada')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" />
        </FormField>
        <FormField label="Saída" error={errors.dataSaida?.message}>
          <input type="datetime-local" {...register('dataSaida')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" />
        </FormField>
        <FormField label="Hóspedes" error={errors.numeroDeHospedes?.message}>
          <input type="number" min="1" {...register('numeroDeHospedes')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" />
        </FormField>
        <label className="flex items-center gap-2 self-end rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-700">
          <input type="checkbox" {...register('solicitouBerco')} className="h-4 w-4" />
          Berço
        </label>
      </div>
      {mutationError ? <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">{getApiErrorMessage(mutationError)}</div> : null}
      <div className="mt-4"><ActionButton type="submit" disabled={isSaving}>{isSaving ? 'Salvando...' : 'Salvar'}</ActionButton></div>
    </form>
  );
}
