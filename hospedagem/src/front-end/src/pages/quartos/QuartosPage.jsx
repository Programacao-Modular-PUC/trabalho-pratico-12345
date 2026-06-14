import { useEffect, useMemo, useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import ActionButton from '../../components/ActionButton';
import { EmptyState, ErrorState, LoadingState } from '../../components/ApiState';
import FormField from '../../components/FormField';
import PageHeader from '../../components/PageHeader';
import { useCreateQuarto, useDeleteQuarto, useQuartos, useUpdateQuarto } from '../../hooks/useQuarto';
import { useResidencias } from '../../hooks/useResidencia';
import { formatCurrency, getApiErrorMessage } from '../../utils/apiError';

const quartoSchema = z
  .object({
    tipo: z.enum(['INDIVIDUAL', 'DUPLO', 'FAMILIA']),
    valorBase: z.coerce.number().min(0, 'Informe um valor base valido.'),
    possuiAR: z.boolean(),
    possuiHidro: z.boolean(),
    residenciaId: z.coerce.number().min(1, 'Selecione uma residencia.'),
    numeroDeCamas: z.coerce.number().optional(),
    adicionalPorCama: z.coerce.number().optional(),
    tipoCama: z.enum(['CASAL', 'QUEEN', 'KING']).optional(),
    solicitouBerco: z.boolean(),
    quantidadeDeAmbientes: z.coerce.number().optional(),
    camasSolteiro: z.coerce.number().min(0).optional(),
    camasCasal: z.coerce.number().min(0).optional(),
    camasQueen: z.coerce.number().min(0).optional(),
    camasKing: z.coerce.number().min(0).optional(),
  })
  .superRefine((values, context) => {
    if (values.tipo === 'INDIVIDUAL' && (!values.numeroDeCamas || values.numeroDeCamas < 1)) {
      context.addIssue({
        code: 'custom',
        path: ['numeroDeCamas'],
        message: 'Informe pelo menos 1 cama.',
      });
    }

    if (values.tipo === 'DUPLO' && !values.tipoCama) {
      context.addIssue({
        code: 'custom',
        path: ['tipoCama'],
        message: 'Informe o tipo de cama.',
      });
    }

    if (values.tipo === 'FAMILIA') {
      const totalCamas =
        Number(values.camasSolteiro || 0) +
        Number(values.camasCasal || 0) +
        Number(values.camasQueen || 0) +
        Number(values.camasKing || 0);

      if (!values.quantidadeDeAmbientes || values.quantidadeDeAmbientes < 1) {
        context.addIssue({
          code: 'custom',
          path: ['quantidadeDeAmbientes'],
          message: 'Informe pelo menos 1 ambiente.',
        });
      }

      if (totalCamas < 1) {
        context.addIssue({
          code: 'custom',
          path: ['camasSolteiro'],
          message: 'Informe pelo menos 1 cama.',
        });
      }
    }
  });

const emptyValues = {
  tipo: 'INDIVIDUAL',
  valorBase: 0,
  possuiAR: false,
  possuiHidro: false,
  residenciaId: '',
  numeroDeCamas: 1,
  adicionalPorCama: 30,
  tipoCama: 'CASAL',
  solicitouBerco: false,
  quantidadeDeAmbientes: 1,
  camasSolteiro: 0,
  camasCasal: 0,
  camasQueen: 0,
  camasKing: 0,
};

function countCama(quarto, tipo) {
  return quarto?.listaDeCamas?.filter((cama) => cama === tipo).length || 0;
}

function buildListaDeCamas(values) {
  return [
    ...Array(Number(values.camasSolteiro || 0)).fill('SOLTEIRO'),
    ...Array(Number(values.camasCasal || 0)).fill('CASAL'),
    ...Array(Number(values.camasQueen || 0)).fill('QUEEN'),
    ...Array(Number(values.camasKing || 0)).fill('KING'),
  ];
}

function getQuartoResumo(quarto) {
  if (quarto.tipo === 'INDIVIDUAL') {
    return `${quarto.numeroDeCamas} cama(s)`;
  }

  if (quarto.tipo === 'DUPLO') {
    return `${quarto.tipoCama}${quarto.solicitouBerco ? ' + berco' : ''}`;
  }

  return `${quarto.capacidadeMaxima ?? 0} hospedes, ${quarto.quantidadeDeAmbientes} ambiente(s)`;
}

function QuartoForm({ editing, onDone, residenciasQuery }) {
  const createMutation = useCreateQuarto();
  const updateMutation = useUpdateQuarto();
  const isSaving = createMutation.isPending || updateMutation.isPending;
  const mutationError = createMutation.error || updateMutation.error;

  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(quartoSchema),
    defaultValues: emptyValues,
  });

  const tipo = watch('tipo');

  useEffect(() => {
    if (editing) {
      reset({
        tipo: editing.tipo,
        valorBase: editing.valorBase || 0,
        possuiAR: Boolean(editing.possuiAR),
        possuiHidro: Boolean(editing.possuiHidro),
        residenciaId: editing.residencia?.id || '',
        numeroDeCamas: editing.numeroDeCamas || 1,
        adicionalPorCama: editing.adicionalPorCama || 30,
        tipoCama: editing.tipoCama || 'CASAL',
        solicitouBerco: Boolean(editing.solicitouBerco),
        quantidadeDeAmbientes: editing.quantidadeDeAmbientes || 1,
        camasSolteiro: countCama(editing, 'SOLTEIRO'),
        camasCasal: countCama(editing, 'CASAL'),
        camasQueen: countCama(editing, 'QUEEN'),
        camasKing: countCama(editing, 'KING'),
      });
    } else {
      reset(emptyValues);
    }
  }, [editing, reset]);

  const residencias = residenciasQuery.data || [];

  const canRenderForm = useMemo(() => {
    return residenciasQuery.isSuccess && residencias.length > 0;
  }, [residenciasQuery.isSuccess, residencias.length]);

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
    }

    if (values.tipo === 'DUPLO') {
      payload.tipoCama = values.tipoCama;
      payload.solicitouBerco = Boolean(values.solicitouBerco);
    }

    if (values.tipo === 'FAMILIA') {
      payload.listaDeCamas = buildListaDeCamas(values);
      payload.quantidadeDeAmbientes = Number(values.quantidadeDeAmbientes);
    }

    if (editing) {
      await updateMutation.mutateAsync({ id: editing.id, payload });
    } else {
      await createMutation.mutateAsync(payload);
    }

    reset(emptyValues);
    onDone();
  }

  if (residenciasQuery.isLoading) {
    return <LoadingState label="Carregando residencias..." />;
  }

  if (residenciasQuery.isError) {
    return <ErrorState message={getApiErrorMessage(residenciasQuery.error)} onRetry={residenciasQuery.refetch} />;
  }

  if (!canRenderForm) {
    return <EmptyState title="Cadastre uma residencia antes de criar quartos" />;
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="rounded-md border border-slate-200 bg-white p-4">
      <div className="mb-4 flex items-center justify-between gap-3">
        <h3 className="text-base font-semibold text-slate-950">{editing ? 'Editar quarto' : 'Novo quarto'}</h3>
        {editing ? (
          <ActionButton variant="secondary" onClick={onDone}>
            Cancelar
          </ActionButton>
        ) : null}
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <FormField label="Tipo" error={errors.tipo?.message}>
          <select
            {...register('tipo')}
            disabled={Boolean(editing)}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900 disabled:bg-slate-100"
          >
            <option value="INDIVIDUAL">Individual</option>
            <option value="DUPLO">Duplo</option>
            <option value="FAMILIA">Familia</option>
          </select>
        </FormField>

        <FormField label="Residencia" error={errors.residenciaId?.message}>
          <select
            {...register('residenciaId')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          >
            <option value="">Selecione</option>
            {residencias.map((residencia) => (
              <option key={residencia.id} value={residencia.id}>
                {residencia.nome}
              </option>
            ))}
          </select>
        </FormField>

        <FormField label="Valor base" error={errors.valorBase?.message}>
          <input
            type="number"
            step="0.01"
            {...register('valorBase')}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
          />
        </FormField>

        <div className="grid grid-cols-2 gap-3">
          <label className="flex items-center gap-2 rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-700">
            <input type="checkbox" {...register('possuiAR')} className="h-4 w-4" />
            AR
          </label>
          <label className="flex items-center gap-2 rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-700">
            <input type="checkbox" {...register('possuiHidro')} className="h-4 w-4" />
            Hidro
          </label>
        </div>

        {tipo === 'INDIVIDUAL' ? (
          <>
            <FormField label="Numero de camas" error={errors.numeroDeCamas?.message}>
              <input
                type="number"
                min="1"
                {...register('numeroDeCamas')}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
              />
            </FormField>
            <FormField label="Adicional por cama" error={errors.adicionalPorCama?.message}>
              <input
                type="number"
                min="0"
                step="0.01"
                {...register('adicionalPorCama')}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
              />
            </FormField>
          </>
        ) : null}

        {tipo === 'DUPLO' ? (
          <>
            <FormField label="Tipo de cama" error={errors.tipoCama?.message}>
              <select
                {...register('tipoCama')}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
              >
                <option value="CASAL">Casal</option>
                <option value="QUEEN">Queen</option>
                <option value="KING">King</option>
              </select>
            </FormField>
            <label className="flex items-center gap-2 self-end rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-700">
              <input type="checkbox" {...register('solicitouBerco')} className="h-4 w-4" />
              Berco
            </label>
          </>
        ) : null}

        {tipo === 'FAMILIA' ? (
          <>
            <FormField label="Ambientes" error={errors.quantidadeDeAmbientes?.message}>
              <input
                type="number"
                min="1"
                {...register('quantidadeDeAmbientes')}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
              />
            </FormField>
            <FormField label="Camas solteiro" error={errors.camasSolteiro?.message}>
              <input
                type="number"
                min="0"
                {...register('camasSolteiro')}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
              />
            </FormField>
            <FormField label="Camas casal" error={errors.camasCasal?.message}>
              <input
                type="number"
                min="0"
                {...register('camasCasal')}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
              />
            </FormField>
            <FormField label="Camas queen" error={errors.camasQueen?.message}>
              <input
                type="number"
                min="0"
                {...register('camasQueen')}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
              />
            </FormField>
            <FormField label="Camas king" error={errors.camasKing?.message}>
              <input
                type="number"
                min="0"
                {...register('camasKing')}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
              />
            </FormField>
          </>
        ) : null}
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

export default function QuartosPage() {
  const [editing, setEditing] = useState(null);
  const [tipoFiltro, setTipoFiltro] = useState('');
  const quartos = useQuartos(tipoFiltro);
  const residencias = useResidencias();
  const deleteMutation = useDeleteQuarto();

  async function handleDelete(id) {
    const confirmed = window.confirm('Remover este quarto?');
    if (confirmed) {
      await deleteMutation.mutateAsync(id);
    }
  }

  return (
    <section>
      <PageHeader title="Quartos" description="Cadastro de unidades para aluguel" />

      <div className="grid gap-5 xl:grid-cols-[minmax(0,1fr)_460px]">
        <div className="rounded-md border border-slate-200 bg-white p-4">
          <div className="mb-4 flex flex-wrap items-end justify-between gap-3">
            <h3 className="text-base font-semibold text-slate-950">Lista de quartos</h3>
            <label className="min-w-48 text-sm text-slate-700">
              Tipo
              <select
                value={tipoFiltro}
                onChange={(event) => setTipoFiltro(event.target.value)}
                className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-900"
              >
                <option value="">Todos</option>
                <option value="INDIVIDUAL">Individual</option>
                <option value="DUPLO">Duplo</option>
                <option value="FAMILIA">Familia</option>
              </select>
            </label>
          </div>

          {quartos.isLoading ? <LoadingState /> : null}
          {quartos.isError ? (
            <ErrorState message={getApiErrorMessage(quartos.error)} onRetry={quartos.refetch} />
          ) : null}
          {quartos.isSuccess && quartos.data.length === 0 ? (
            <EmptyState title={tipoFiltro ? 'Nenhum quarto encontrado para este tipo' : 'Nenhum quarto cadastrado'} />
          ) : null}

          {quartos.isSuccess && quartos.data.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-slate-200 text-sm">
                <thead>
                  <tr className="text-left text-slate-500">
                    <th className="py-2 pr-4 font-medium">Tipo</th>
                    <th className="py-2 pr-4 font-medium">Residencia</th>
                    <th className="py-2 pr-4 font-medium">Valor base</th>
                    <th className="py-2 pr-4 font-medium">Detalhes</th>
                    <th className="py-2 text-right font-medium">Acoes</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {quartos.data.map((quarto) => (
                    <tr key={quarto.id}>
                      <td className="py-3 pr-4 font-medium text-slate-950">{quarto.tipo}</td>
                      <td className="py-3 pr-4 text-slate-600">{quarto.residencia?.nome || '-'}</td>
                      <td className="py-3 pr-4 text-slate-600">{formatCurrency(quarto.valorBase)}</td>
                      <td className="py-3 pr-4 text-slate-600">{getQuartoResumo(quarto)}</td>
                      <td className="py-3">
                        <div className="flex justify-end gap-2">
                          <ActionButton variant="secondary" onClick={() => setEditing(quarto)}>
                            Editar
                          </ActionButton>
                          <ActionButton
                            variant="danger"
                            onClick={() => handleDelete(quarto.id)}
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

        <QuartoForm editing={editing} onDone={() => setEditing(null)} residenciasQuery={residencias} />
      </div>
    </section>
  );
}
