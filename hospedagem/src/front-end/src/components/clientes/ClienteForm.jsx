import { useEffect } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import ActionButton from '../ActionButton';
import FormField from '../FormField';
import { useCreateCliente, useUpdateCliente } from '../../hooks/useCliente';
import { clienteSchema } from '../../schemas/clienteSchema';
import { getApiErrorMessage } from '../../utils/apiError';

const emptyValues = { nome: '', cpf: '', email: '', telefone: '' };

export default function ClienteForm({ editing, onDone }) {
  const createMutation = useCreateCliente();
  const updateMutation = useUpdateCliente();
  const isSaving = createMutation.isPending || updateMutation.isPending;
  const mutationError = createMutation.error || updateMutation.error;
  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: zodResolver(clienteSchema), defaultValues: emptyValues,
  });

  useEffect(() => {
    reset(editing ? {
      nome: editing.nome || '', cpf: editing.cpf || '',
      email: editing.email || '', telefone: editing.telefone || '',
    } : emptyValues);
  }, [editing, reset]);

  async function onSubmit(values) {
    const payload = { ...values, email: values.email || '', telefone: values.telefone || '' };
    if (editing) await updateMutation.mutateAsync({ id: editing.id, payload });
    else await createMutation.mutateAsync(payload);
    reset(emptyValues);
    onDone();
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="rounded-md border border-slate-200 bg-white p-4">
      <div className="mb-4 flex items-center justify-between gap-3">
        <h3 className="text-base font-semibold text-slate-950">{editing ? 'Editar cliente' : 'Novo cliente'}</h3>
        {editing ? <ActionButton variant="secondary" onClick={onDone}>Cancelar</ActionButton> : null}
      </div>
      <div className="grid gap-4 md:grid-cols-2">
        <FormField label="Nome" error={errors.nome?.message}><input {...register('nome')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" /></FormField>
        <FormField label="CPF" error={errors.cpf?.message}><input {...register('cpf')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" /></FormField>
        <FormField label="E-mail" error={errors.email?.message}><input {...register('email')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" /></FormField>
        <FormField label="Telefone" error={errors.telefone?.message}><input {...register('telefone')} className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm" /></FormField>
      </div>
      {mutationError ? <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">{getApiErrorMessage(mutationError)}</div> : null}
      <div className="mt-4"><ActionButton type="submit" disabled={isSaving}>{isSaving ? 'Salvando...' : 'Salvar'}</ActionButton></div>
    </form>
  );
}
