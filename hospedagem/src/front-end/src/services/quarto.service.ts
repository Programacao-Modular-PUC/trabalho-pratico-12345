import { api } from '../lib/api';
import type { Quarto, QuartoPayload } from '../types/quarto.types';

export async function listQuartos(): Promise<Quarto[]> {
  const response = await api.get<Quarto[]>('/quartos');
  return response.data;
}

export async function getQuarto(id: number): Promise<Quarto> {
  const response = await api.get<Quarto>(`/quartos/${id}`);
  return response.data;
}

export async function createQuarto(payload: QuartoPayload): Promise<Quarto> {
  const response = await api.post<Quarto>('/quartos', payload);
  return response.data;
}

export async function updateQuarto(id: number, payload: QuartoPayload): Promise<Quarto> {
  const response = await api.put<Quarto>(`/quartos/${id}`, payload);
  return response.data;
}

export async function deleteQuarto(id: number): Promise<void> {
  await api.delete(`/quartos/${id}`);
}
