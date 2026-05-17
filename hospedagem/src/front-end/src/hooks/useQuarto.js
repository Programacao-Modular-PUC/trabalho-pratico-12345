import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  createQuarto,
  deleteQuarto,
  getQuarto,
  listQuartos,
  updateQuarto,
} from '../services/quarto.service';

export const quartoKeys = {
  all: ['quartos'],
  detail: (id) => ['quartos', id],
};

export function useQuartos() {
  return useQuery({
    queryKey: quartoKeys.all,
    queryFn: listQuartos,
  });
}

export function useQuarto(id) {
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
    mutationFn: ({ id, payload }) => updateQuarto(id, payload),
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

// CONVERSAO
// - Removidos: type-only import, anotacoes de tipo e assertions 'as const'.
// - Sem adaptacoes especiais.
// - Risco de runtime: chamadas com payload invalido nao sao barradas em compilacao.