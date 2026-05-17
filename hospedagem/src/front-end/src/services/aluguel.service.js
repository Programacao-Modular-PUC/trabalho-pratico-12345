import { api } from '../lib/api';

/** @returns {Promise<Object[]>} */
export async function listAlugueis() {
  const response = await api.get('/alugueis');
  return response.data;
}

/** @param {number} id @returns {Promise<Object>} */
export async function getAluguel(id) {
  const response = await api.get(`/alugueis/${id}`);
  return response.data;
}

/** @param {Object} payload @returns {Promise<Object>} */
export async function createAluguel(payload) {
  const response = await api.post('/alugueis', payload);
  return response.data;
}

/** @param {number} id @param {Object} payload @returns {Promise<Object>} */
export async function updateAluguel(id, payload) {
  const response = await api.put(`/alugueis/${id}`, payload);
  return response.data;
}

/** @param {number} id @returns {Promise<void>} */
export async function deleteAluguel(id) {
  await api.delete(`/alugueis/${id}`);
}

// CONVERSAO
// - Removidos: type-only import, anotacoes de tipo e generics de axios.
// - Adaptacao especial: JSDoc simples nos metodos publicos.
// - Risco de runtime: formato de payload/resposta nao e mais verificado em compilacao.