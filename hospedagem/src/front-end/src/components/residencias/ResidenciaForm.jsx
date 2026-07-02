import { useEffect } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import ActionButton from '../ActionButton';
import FormField from '../FormField';
import { useCreateResidencia, useUpdateResidencia } from '../../hooks/useResidencia';
import { residenciaSchema } from '../../schemas/residenciaSchema';
import { getApiErrorMessage } from '../../utils/apiError';

const emptyValues = { nome: '', endereco: '', descricao: '' };

export default function ResidenciaForm({ editing, onDone }) {
  const createMutation = useCreateResidencia();
  const updateMutation = useUpdateResidencia();
  const isSaving = createMutation.isPending || updateMutation.isPending;
  const mutationError = createMutation.error || updateMutation.error;
  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: zodResolver(residenciaSchema), defaultValues: emptyValues,
  });

  useEffect(() => {
    reset(editing ? {
      nome: editing.nome || '', endereco: editing.endereco || '', descricao: editing.descricao || '',
    } : emptyValues);
  }, [editing, reset]);

  async function onSubmit(values) {
    const payload = { ...values, descricao: values.descricao || '' };
    if (editing) await updateMutation.mutateAsync({ id: editing.id, payload });
    else await createMutation.mutateAsync(payload);
    reset(emptyValues);
    onDone();
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="rounded-md border border-slate-200 bg-white p-4">
      <div className="mb-4 flex items-center justify-between gap-3"><h3 className="text-base font-semibold text-slate-950">{editing ? 'Editar residência' : 'Nova residência'}</h3>{editing ? <ActionButton variant="secondary" onClick={onDone}>Cancelar</ActionButton> : null}</div>
      <div className="grid gap-4 md:grid-cols-2">
        <FormField label="Nome" error={errors.nome?.message}><input {...register('nome')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" /></FormField>
        <FormField label="Endereço" error={errors.endereco?.message}><input {...register('endereco')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" /></FormField>
        <div className="md:col-span-2"><FormField label="Descrição" error={errors.descricao?.message}><textarea {...register('descricao')} rows={3} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" /></FormField></div>
      </div>
      {mutationError ? <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">{getApiErrorMessage(mutationError)}</div> : null}
      <div className="mt-4"><ActionButton type="submit" disabled={isSaving}>{isSaving ? 'Salvando...' : 'Salvar'}</ActionButton></div>
    </form>
  );
}
