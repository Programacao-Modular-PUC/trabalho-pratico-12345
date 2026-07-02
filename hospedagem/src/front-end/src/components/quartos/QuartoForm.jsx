import { useEffect, useMemo } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import ActionButton from '../ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../ApiState';
import FormField from '../FormField';
import { useCreateQuarto, useUpdateQuarto } from '../../hooks/useQuarto';
import { quartoSchema } from '../../schemas/quartoSchema';
import { getApiErrorMessage } from '../../utils/apiError';
import { buildListaDeCamas, countCama } from '../../utils/quarto';

const emptyValues = {
  tipo: 'INDIVIDUAL', valorBase: 0, possuiAR: false, possuiHidro: false, residenciaId: '',
  numeroDeCamas: 1, adicionalPorCama: 30, tipoCama: 'CASAL', possuiBerco: false,
  quantidadeDeAmbientes: 1, camasSolteiro: 0, camasCasal: 0, camasQueen: 0, camasKing: 0,
};

function CampoNumero({ label, name, register, error, min = '0', step }) {
  return (
    <FormField label={label} error={error}>
      <input type="number" min={min} step={step} {...register(name)} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" />
    </FormField>
  );
}

export default function QuartoForm({ editing, onDone, residenciasQuery }) {
  const createMutation = useCreateQuarto();
  const updateMutation = useUpdateQuarto();
  const isSaving = createMutation.isPending || updateMutation.isPending;
  const mutationError = createMutation.error || updateMutation.error;
  const { register, handleSubmit, reset, watch, formState: { errors } } = useForm({
    resolver: zodResolver(quartoSchema), defaultValues: emptyValues,
  });
  const tipo = watch('tipo');

  useEffect(() => {
    reset(editing ? {
      tipo: editing.tipo,
      valorBase: editing.valorBase || 0,
      possuiAR: Boolean(editing.possuiAR),
      possuiHidro: Boolean(editing.possuiHidro),
      residenciaId: editing.residencia?.id || '',
      numeroDeCamas: editing.numeroDeCamas || 1,
      adicionalPorCama: editing.adicionalPorCama || 30,
      tipoCama: editing.tipoCama || 'CASAL',
      possuiBerco: Boolean(editing.possuiBerco),
      quantidadeDeAmbientes: editing.quantidadeDeAmbientes || 1,
      camasSolteiro: countCama(editing, 'SOLTEIRO'), camasCasal: countCama(editing, 'CASAL'),
      camasQueen: countCama(editing, 'QUEEN'), camasKing: countCama(editing, 'KING'),
    } : emptyValues);
  }, [editing, reset]);

  const residencias = residenciasQuery.data || [];
  const canRenderForm = useMemo(
    () => residenciasQuery.isSuccess && residencias.length > 0,
    [residencias.length, residenciasQuery.isSuccess],
  );

  async function onSubmit(values) {
    const payload = {
      tipo: values.tipo,
      valorBase: Number(values.valorBase),
      possuiAR: Boolean(values.possuiAR),
      possuiHidro: Boolean(values.possuiHidro),
      residenciaId: Number(values.residenciaId),
    };
    if (values.tipo === 'INDIVIDUAL') {
      payload.numeroDeCamas = Number(values.numeroDeCamas);
      payload.adicionalPorCama = Number(values.adicionalPorCama || 30);
    } else if (values.tipo === 'DUPLO') {
      payload.tipoCama = values.tipoCama;
      payload.possuiBerco = Boolean(values.possuiBerco);
    } else {
      payload.listaDeCamas = buildListaDeCamas(values);
      payload.quantidadeDeAmbientes = Number(values.quantidadeDeAmbientes);
    }
    if (editing) await updateMutation.mutateAsync({ id: editing.id, payload });
    else await createMutation.mutateAsync(payload);
    reset(emptyValues);
    onDone();
  }

  if (residenciasQuery.isLoading) return <LoadingState label="Carregando residências..." />;
  if (residenciasQuery.isError) return <ErrorState message={getApiErrorMessage(residenciasQuery.error)} onRetry={residenciasQuery.refetch} />;
  if (!canRenderForm) return <EmptyState title="Cadastre uma residência antes de criar quartos" />;

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="rounded-md border border-slate-200 bg-white p-4">
      <div className="mb-4 flex items-center justify-between gap-3">
        <h3 className="text-base font-semibold text-slate-950">{editing ? 'Editar quarto' : 'Novo quarto'}</h3>
        {editing ? <ActionButton variant="secondary" onClick={onDone}>Cancelar</ActionButton> : null}
      </div>
      <div className="grid gap-4 md:grid-cols-2">
        <FormField label="Tipo" error={errors.tipo?.message}>
          <select {...register('tipo')} disabled={Boolean(editing)} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm disabled:bg-slate-100">
            <option value="INDIVIDUAL">Individual</option><option value="DUPLO">Duplo</option><option value="FAMILIA">Família</option>
          </select>
        </FormField>
        <FormField label="Residência" error={errors.residenciaId?.message}>
          <select {...register('residenciaId')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm">
            <option value="">Selecione</option>{residencias.map((item) => <option key={item.id} value={item.id}>{item.nome}</option>)}
          </select>
        </FormField>
        <CampoNumero label="Valor base" name="valorBase" register={register} error={errors.valorBase?.message} step="0.01" />
        <div className="grid grid-cols-2 gap-3">
          <label className="flex items-center gap-2 rounded-md border border-slate-300 px-3 py-2 text-sm"><input type="checkbox" {...register('possuiAR')} /> AR</label>
          <label className="flex items-center gap-2 rounded-md border border-slate-300 px-3 py-2 text-sm"><input type="checkbox" {...register('possuiHidro')} /> Hidro</label>
        </div>
        {tipo === 'INDIVIDUAL' ? <>
          <CampoNumero label="Número de camas" name="numeroDeCamas" register={register} error={errors.numeroDeCamas?.message} min="1" />
          <CampoNumero label="Adicional por cama" name="adicionalPorCama" register={register} error={errors.adicionalPorCama?.message} step="0.01" />
        </> : null}
        {tipo === 'DUPLO' ? <>
          <FormField label="Tipo de cama" error={errors.tipoCama?.message}>
            <select {...register('tipoCama')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"><option value="CASAL">Casal</option><option value="QUEEN">Queen</option><option value="KING">King</option></select>
          </FormField>
          <label className="flex items-center gap-2 self-end rounded-md border border-slate-300 px-3 py-2 text-sm"><input type="checkbox" {...register('possuiBerco')} /> Oferece berço</label>
        </> : null}
        {tipo === 'FAMILIA' ? <>
          <CampoNumero label="Ambientes" name="quantidadeDeAmbientes" register={register} error={errors.quantidadeDeAmbientes?.message} min="1" />
          <CampoNumero label="Camas solteiro" name="camasSolteiro" register={register} error={errors.camasSolteiro?.message} />
          <CampoNumero label="Camas casal" name="camasCasal" register={register} />
          <CampoNumero label="Camas queen" name="camasQueen" register={register} />
          <CampoNumero label="Camas king" name="camasKing" register={register} />
        </> : null}
      </div>
      {mutationError ? <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">{getApiErrorMessage(mutationError)}</div> : null}
      <div className="mt-4"><ActionButton type="submit" disabled={isSaving}>{isSaving ? 'Salvando...' : 'Salvar'}</ActionButton></div>
    </form>
  );
}
