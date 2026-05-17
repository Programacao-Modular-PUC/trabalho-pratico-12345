import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  createAluguel,
  deleteAluguel,
  getAluguel,
  listAlugueis,
  updateAluguel,
} from '../services/aluguel.service';
import type { AluguelPayload } from '../types/aluguel.types';

export const aluguelKeys = {
  all: ['alugueis'] as const,
  detail: (id: number) => ['alugueis', id] as const,
};

export function useAlugueis() {
  return useQuery({
    queryKey: aluguelKeys.all,
    queryFn: listAlugueis,
  });
}

export function useAluguel(id: number) {
  return useQuery({
    queryKey: aluguelKeys.detail(id),
    queryFn: () => getAluguel(id),
    enabled: Boolean(id),
  });
}

export function useCreateAluguel() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createAluguel,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: aluguelKeys.all }),
  });
}

export function useUpdateAluguel() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: AluguelPayload }) => updateAluguel(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: aluguelKeys.all }),
  });
}

export function useDeleteAluguel() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteAluguel,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: aluguelKeys.all }),
  });
}
