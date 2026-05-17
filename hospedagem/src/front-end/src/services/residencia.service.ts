import { api } from '../lib/api';
import type { Residencia, ResidenciaPayload } from '../types/residencia.types';

export async function listResidencias(): Promise<Residencia[]> {
  const response = await api.get<Residencia[]>('/residencias');
  return response.data;
}

export async function getResidencia(id: number): Promise<Residencia> {
  const response = await api.get<Residencia>(`/residencias/${id}`);
  return response.data;
}

export async function createResidencia(payload: ResidenciaPayload): Promise<Residencia> {
  const response = await api.post<Residencia>('/residencias', payload);
  return response.data;
}

export async function updateResidencia(id: number, payload: ResidenciaPayload): Promise<Residencia> {
  const response = await api.put<Residencia>(`/residencias/${id}`, payload);
  return response.data;
}

export async function deleteResidencia(id: number): Promise<void> {
  await api.delete(`/residencias/${id}`);
}
