import { api } from '../lib/api';
import type { Aluguel, AluguelPayload } from '../types/aluguel.types';

export async function listAlugueis(): Promise<Aluguel[]> {
  const response = await api.get<Aluguel[]>('/alugueis');
  return response.data;
}

export async function getAluguel(id: number): Promise<Aluguel> {
  const response = await api.get<Aluguel>(`/alugueis/${id}`);
  return response.data;
}

export async function createAluguel(payload: AluguelPayload): Promise<Aluguel> {
  const response = await api.post<Aluguel>('/alugueis', payload);
  return response.data;
}

export async function updateAluguel(id: number, payload: AluguelPayload): Promise<Aluguel> {
  const response = await api.put<Aluguel>(`/alugueis/${id}`, payload);
  return response.data;
}

export async function deleteAluguel(id: number): Promise<void> {
  await api.delete(`/alugueis/${id}`);
}
