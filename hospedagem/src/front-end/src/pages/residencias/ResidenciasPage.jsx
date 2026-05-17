import { useEffect, useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import ActionButton from '../../components/ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../../components/ApiState';
import FormField from '../../components/FormField';
import PageHeader from '../../components/PageHeader';
import {
  useCreateResidencia,
  useDeleteResidencia,
  useResidencias,
  useUpdateResidencia,
} from '../../hooks/useResidencia';
import { getApiErrorMessage } from '../../utils/apiError';

const residenciaSchema = z.object({
  nome: z.string().min(1, 'Informe o nome.'),
  endereco: z.string().min(1, 'Informe o endereco.'),
  descricao: z.string().optional(),
});

const emptyValues = {
  nome: '',
  endereco: '',
  descricao: '',
};

function ResidenciaForm({ editing, onDone }) {
  const createMutation = useCreateResidencia();
  const updateMutation = useUpdateResidencia();
  const isSaving = createMutation.isPending || updateMutation.isPending;
  const mutationError = createMutation.error || updateMutation.error;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(residenciaSchema),
    defaultValues: emptyValues,
  });

  useEffect(() => {
    if (editing) {
      reset({
        nome: editing.nome || '',
        endereco: editing.endereco || '',
        descricao: editing.descricao || '',
      });
    } else {
      reset(emptyValues);
    }
  }, [editing, reset]);

  async function onSubmit(values) {
    const payload = {
      nome: values.nome,
      endereco: values.endereco,
      descricao: values.descricao || '',
    };

    if (editing) {
      await updateMutation.mutateAsync({ id: editing.id, payload });
    } else {
      await createMutation.mutateAsync(payload);
    }

    reset(emptyValues);
    onDone();
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="rounded-md border border-slate-200 bg-white p-4">
      <div className="mb-4 flex items-center justify-between gap-3">
        <h3 className="text-base font-semibold text-slate-950">
          {editing ? 'Editar residencia' : 'Nova residencia'}
        </h3>
        {editing ? (
          <ActionButton variant="secondary" onClick={onDone}>
            Cancelar
          </ActionButton>
        ) : null}
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <FormField label="Nome" error={errors.nome?.message}>
          <input
            {...register('nome')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          />
        </FormField>

        <FormField label="Endereco" error={errors.endereco?.message}>
          <input
            {...register('endereco')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          />
        </FormField>

        <div className="md:col-span-2">
          <FormField label="Descricao" error={errors.descricao?.message}>
            <textarea
              {...register('descricao')}
              rows={3}
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
            />
          </FormField>
        </div>
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

export default function ResidenciasPage() {
  const [editing, setEditing] = useState(null);
  const residencias = useResidencias();
  const deleteMutation = useDeleteResidencia();

  async function handleDelete(id) {
    const confirmed = window.confirm('Remover esta residencia?');
    if (confirmed) {
      await deleteMutation.mutateAsync(id);
    }
  }

  return (
    <section>
      <PageHeader title="Residencias" description="Cadastro de locais de hospedagem" />

      <div className="grid gap-5 lg:grid-cols-[minmax(0,1fr)_420px]">
        <div className="rounded-md border border-slate-200 bg-white p-4">
          <h3 className="mb-4 text-base font-semibold text-slate-950">Lista de residencias</h3>

          {residencias.isLoading ? <LoadingState /> : null}
          {residencias.isError ? (
            <ErrorState message={getApiErrorMessage(residencias.error)} onRetry={residencias.refetch} />
          ) : null}
          {residencias.isSuccess && residencias.data.length === 0 ? (
            <EmptyState title="Nenhuma residencia cadastrada" />
          ) : null}

          {residencias.isSuccess && residencias.data.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-slate-200 text-sm">
                <thead>
                  <tr className="text-left text-slate-500">
                    <th className="py-2 pr-4 font-medium">Nome</th>
                    <th className="py-2 pr-4 font-medium">Endereco</th>
                    <th className="py-2 pr-4 font-medium">Quartos</th>
                    <th className="py-2 text-right font-medium">Acoes</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {residencias.data.map((residencia) => (
                    <tr key={residencia.id}>
                      <td className="py-3 pr-4 font-medium text-slate-950">{residencia.nome}</td>
                      <td className="py-3 pr-4 text-slate-600">{residencia.endereco}</td>
                      <td className="py-3 pr-4 text-slate-600">{residencia.quartos?.length ?? 0}</td>
                      <td className="py-3">
                        <div className="flex justify-end gap-2">
                          <ActionButton variant="secondary" onClick={() => setEditing(residencia)}>
                            Editar
                          </ActionButton>
                          <ActionButton
                            variant="danger"
                            onClick={() => handleDelete(residencia.id)}
                            disabled={deleteMutation.isPending}
                          >
                            Remover
                          </ActionButton>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : null}

          {deleteMutation.error ? (
            <div className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800">
              {getApiErrorMessage(deleteMutation.error)}
            </div>
          ) : null}
        </div>

        <ResidenciaForm editing={editing} onDone={() => setEditing(null)} />
      </div>
    </section>
  );
}
