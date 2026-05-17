import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
    createCliente,
    deleteCliente,
    getCliente,
    listClientes,
    updateCliente,
} from '../services/cliente.service';

export const clienteKeys = {
    all: ['clientes'],
    detail: (id) => ['clientes', id],
};

export function useClientes() {
    return useQuery({
        queryKey: clienteKeys.all,
        queryFn: listClientes,
    });
}

export function useCliente(id) {
    return useQuery({
        queryKey: clienteKeys.detail(id),
        queryFn: () => getCliente(id),
        enabled: Boolean(id),
    });
}

export function useCreateCliente() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCliente,
        onSuccess: () => queryClient.invalidateQueries({ queryKey: clienteKeys.all }),
    });
}

export function useUpdateCliente() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, payload }) => updateCliente(id, payload),
        onSuccess: () => queryClient.invalidateQueries({ queryKey: clienteKeys.all }),
    });
}

export function useDeleteCliente() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: deleteCliente,
        onSuccess: () => queryClient.invalidateQueries({ queryKey: clienteKeys.all }),
    });
}

// CONVERSAO
// - Removidos: type-only import, anotacoes de tipo e assertions 'as const'.
// - Sem adaptacoes especiais.
// - Risco de runtime: chamadas com payload invalido nao sao barradas em compilacao.