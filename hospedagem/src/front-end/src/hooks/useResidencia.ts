import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  createResidencia,
  deleteResidencia,
  getResidencia,
  listResidencias,
  updateResidencia,
} from '../services/residencia.service';
import type { ResidenciaPayload } from '../types/residencia.types';

export const residenciaKeys = {
  all: ['residencias'] as const,
  detail: (id: number) => ['residencias', id] as const,
};

export function useResidencias() {
  return useQuery({
    queryKey: residenciaKeys.all,
    queryFn: listResidencias,
  });
}

export function useResidencia(id: number) {
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
    mutationFn: ({ id, payload }: { id: number; payload: ResidenciaPayload }) => updateResidencia(id, payload),
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
