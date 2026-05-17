import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  createResidencia,
  deleteResidencia,
  getResidencia,
  listResidencias,
  updateResidencia,
} from '../services/residencia.service';

export const residenciaKeys = {
  all: ['residencias'],
  detail: (id) => ['residencias', id],
};

export function useResidencias() {
  return useQuery({
    queryKey: residenciaKeys.all,
    queryFn: listResidencias,
  });
}

export function useResidencia(id) {
  return useQuery({
    queryKey: residenciaKeys.detail(id),
    queryFn: () => getResidencia(id),
    enabled: Boolean(id),
  });
}

export function useCreateResidencia() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createResidencia,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: residenciaKeys.all }),
  });
}

export function useUpdateResidencia() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, payload }) => updateResidencia(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: residenciaKeys.all }),
  });
}

export function useDeleteResidencia() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteResidencia,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: residenciaKeys.all }),
  });
}

// CONVERSAO
// - Removidos: type-only import, anotacoes de tipo e assertions 'as const'.
// - Sem adaptacoes especiais.
// - Risco de runtime: chamadas com payload invalido nao sao barradas em compilacao.