import { useEffect, useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import ActionButton from '../../components/ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../../components/ApiState';
import FormField from '../../components/FormField';
import PageHeader from '../../components/PageHeader';
import {
  useClientes,
  useCreateCliente,
  useDeleteCliente,
  useUpdateCliente,
} from '../../hooks/useCliente';
import { getApiErrorMessage } from '../../utils/apiError';

const clienteSchema = z.object({
  nome: z.string().min(1, 'Informe o nome.'),
  cpf: z.string().min(1, 'Informe o CPF.'),
  email: z.string().email('Email invalido.').or(z.literal('')).optional(),
  telefone: z.string().optional(),
});

const emptyValues = {
  nome: '',
  cpf: '',
  email: '',
  telefone: '',
};

function ClienteForm({ editing, onDone }) {
  const createMutation = useCreateCliente();
  const updateMutation = useUpdateCliente();
  const isSaving = createMutation.isPending || updateMutation.isPending;
  const mutationError = createMutation.error || updateMutation.error;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(clienteSchema),
    defaultValues: emptyValues,
  });

  useEffect(() => {
    if (editing) {
      reset({
        nome: editing.nome || '',
        cpf: editing.cpf || '',
        email: editing.email || '',
        telefone: editing.telefone || '',
      });
    } else {
      reset(emptyValues);
    }
  }, [editing, reset]);

  async function onSubmit(values) {
    const payload = {
      nome: values.nome,
      cpf: values.cpf,
      email: values.email || '',
      telefone: values.telefone || '',
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
          {editing ? 'Editar cliente' : 'Novo cliente'}
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

        <FormField label="CPF" error={errors.cpf?.message}>
          <input
            {...register('cpf')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          />
        </FormField>

        <FormField label="Email" error={errors.email?.message}>
          <input
            {...register('email')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          />
        </FormField>

        <FormField label="Telefone" error={errors.telefone?.message}>
          <input
            {...register('telefone')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          />
        </FormField>
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

export default function ClientesPage() {
  const [editing, setEditing] = useState(null);
  const clientes = useClientes();
  const deleteMutation = useDeleteCliente();

  async function handleDelete(id) {
    const confirmed = window.confirm('Remover este cliente?');
    if (confirmed) {
      await deleteMutation.mutateAsync(id);
    }
  }

  return (
    <section>
      <PageHeader title="Clientes" description="Cadastro de hospedes" />

      <div className="grid gap-5 lg:grid-cols-[minmax(0,1fr)_420px]">
        <div className="rounded-md border border-slate-200 bg-white p-4">
          <h3 className="mb-4 text-base font-semibold text-slate-950">Lista de clientes</h3>

          {clientes.isLoading ? <LoadingState /> : null}
          {clientes.isError ? (
            <ErrorState message={getApiErrorMessage(clientes.error)} onRetry={clientes.refetch} />
          ) : null}
          {clientes.isSuccess && clientes.data.length === 0 ? (
            <EmptyState title="Nenhum cliente cadastrado" />
          ) : null}

          {clientes.isSuccess && clientes.data.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-slate-200 text-sm">
                <thead>
                  <tr className="text-left text-slate-500">
                    <th className="py-2 pr-4 font-medium">Nome</th>
                    <th className="py-2 pr-4 font-medium">CPF</th>
                    <th className="py-2 pr-4 font-medium">Email</th>
                    <th className="py-2 pr-4 font-medium">Telefone</th>
                    <th className="py-2 text-right font-medium">Acoes</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {clientes.data.map((cliente) => (
                    <tr key={cliente.id}>
                      <td className="py-3 pr-4 font-medium text-slate-950">{cliente.nome}</td>
                      <td className="py-3 pr-4 text-slate-600">{cliente.cpf}</td>
                      <td className="py-3 pr-4 text-slate-600">{cliente.email || '-'}</td>
                      <td className="py-3 pr-4 text-slate-600">{cliente.telefone || '-'}</td>
                      <td className="py-3">
                        <div className="flex justify-end gap-2">
                          <ActionButton variant="secondary" onClick={() => setEditing(cliente)}>
                            Editar
                          </ActionButton>
                          <ActionButton
                            variant="danger"
                            onClick={() => handleDelete(cliente.id)}
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

        <ClienteForm editing={editing} onDone={() => setEditing(null)} />
      </div>
    </section>
  );
}
