import { api } from '../lib/api';
import type { Cliente, ClientePayload } from '../types/cliente.types';

export async function listClientes(): Promise<Cliente[]> {
  const response = await api.get<Cliente[]>('/clientes');
  return response.data;
}

export async function getCliente(id: number): Promise<Cliente> {
  const response = await api.get<Cliente>(`/clientes/${id}`);
  return response.data;
}

export async function createCliente(payload: ClientePayload): Promise<Cliente> {
  const response = await api.post<Cliente>('/clientes', payload);
  return response.data;
}

export async function updateCliente(id: number, payload: ClientePayload): Promise<Cliente> {
  const response = await api.put<Cliente>(`/clientes/${id}`, payload);
  return response.data;
}

export async function deleteCliente(id: number): Promise<void> {
  await api.delete(`/clientes/${id}`);
}
