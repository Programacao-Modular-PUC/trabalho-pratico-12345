import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  createQuarto,
  deleteQuarto,
  getQuarto,
  listQuartos,
  updateQuarto,
} from '../services/quarto.service';
import type { QuartoPayload } from '../types/quarto.types';

export const quartoKeys = {
  all: ['quartos'] as const,
  detail: (id: number) => ['quartos', id] as const,
};

export function useQuartos() {
  return useQuery({
    queryKey: quartoKeys.all,
    queryFn: listQuartos,
  });
}

export function useQuarto(id: number) {
  return useQuery({
    queryKey: quartoKeys.detail(id),
    queryFn: () => getQuarto(id),
    enabled: Boolean(id),
  });
}

export function useCreateQuarto() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createQuarto,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: quartoKeys.all }),
  });
}

export function useUpdateQuarto() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: QuartoPayload }) => updateQuarto(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: quartoKeys.all }),
  });
}

export function useDeleteQuarto() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteQuarto,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: quartoKeys.all }),
  });
}
