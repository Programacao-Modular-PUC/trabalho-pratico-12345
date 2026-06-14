import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
    cancelAluguel,
    createAluguel,
    deleteAluguel,
    getAluguel,
    listAlugueis,
    listAlugueisByCliente,
    updateAluguel,
} from '../services/aluguel.service';

export const aluguelKeys = {
    all: ['alugueis'],
    byCliente: (clienteId) => ['alugueis', 'cliente', clienteId],
    detail: (id) => ['alugueis', id],
};

export function useAlugueis() {
    return useQuery({
        queryKey: aluguelKeys.all,
        queryFn: listAlugueis,
    });
}

export function useAluguel(id) {
    return useQuery({
        queryKey: aluguelKeys.detail(id),
        queryFn: () => getAluguel(id),
        enabled: Boolean(id),
    });
}

export function useAlugueisByCliente(clienteId) {
    return useQuery({
        queryKey: aluguelKeys.byCliente(clienteId),
        queryFn: () => listAlugueisByCliente(clienteId),
        enabled: Boolean(clienteId),
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
        mutationFn: ({ id, payload }) => updateAluguel(id, payload),
        onSuccess: () => queryClient.invalidateQueries({ queryKey: aluguelKeys.all }),
    });
}

export function useCancelAluguel() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: cancelAluguel,
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

// CONVERSAO
// - Removidos: type-only import, anotacoes de tipo e assertions 'as const'.
// - Sem adaptacoes especiais.
// - Risco de runtime: chamadas com payload invalido nao sao barradas em compilacao.
