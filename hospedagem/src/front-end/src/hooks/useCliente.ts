import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  createCliente,
  deleteCliente,
  getCliente,
  listClientes,
  updateCliente,
} from '../services/cliente.service';
import type { ClientePayload } from '../types/cliente.types';

export const clienteKeys = {
  all: ['clientes'] as const,
  detail: (id: number) => ['clientes', id] as const,
};

export function useClientes() {
  return useQuery({
    queryKey: clienteKeys.all,
    queryFn: listClientes,
  });
}

export function useCliente(id: number) {
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
    mutationFn: ({ id, payload }: { id: number; payload: ClientePayload }) => updateCliente(id, payload),
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
