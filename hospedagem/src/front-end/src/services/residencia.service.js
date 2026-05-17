import { api } from '../lib/api';

/** @returns {Promise<Object[]>} */
export async function listResidencias() {
  const response = await api.get('/residencias');
  return response.data;
}

/** @param {number} id @returns {Promise<Object>} */
export async function getResidencia(id) {
  const response = await api.get(`/residencias/${id}`);
  return response.data;
}

/** @param {Object} payload @returns {Promise<Object>} */
export async function createResidencia(payload) {
  const response = await api.post('/residencias', payload);
  return response.data;
}

/** @param {number} id @param {Object} payload @returns {Promise<Object>} */
export async function updateResidencia(id, payload) {
  const response = await api.put(`/residencias/${id}`, payload);
  return response.data;
}

/** @param {number} id @returns {Promise<void>} */
export async function deleteResidencia(id) {
  await api.delete(`/residencias/${id}`);
}

// CONVERSAO
// - Removidos: type-only import, anotacoes de tipo e generics de axios.
// - Adaptacao especial: JSDoc simples nos metodos publicos.
// - Risco de runtime: formato de payload/resposta nao e mais verificado em compilacao.