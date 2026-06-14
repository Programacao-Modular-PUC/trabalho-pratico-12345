import { api } from '../lib/api';

/** @param {string=} tipo @returns {Promise<Object[]>} */
export async function listQuartos(tipo) {
  const response = await api.get('/quartos', {
    params: tipo ? { tipo } : {},
  });
  return response.data;
}

/** @param {number} id @returns {Promise<Object>} */
export async function getQuarto(id) {
  const response = await api.get(`/quartos/${id}`);
  return response.data;
}

/** @param {Object} payload @returns {Promise<Object>} */
export async function createQuarto(payload) {
  const response = await api.post('/quartos', payload);
  return response.data;
}

/** @param {number} id @param {Object} payload @returns {Promise<Object>} */
export async function updateQuarto(id, payload) {
  const response = await api.put(`/quartos/${id}`, payload);
  return response.data;
}

/** @param {number} id @returns {Promise<void>} */
export async function deleteQuarto(id) {
  await api.delete(`/quartos/${id}`);
}

// CONVERSAO
// - Removidos: type-only import, anotacoes de tipo e generics de axios.
// - Adaptacao especial: JSDoc simples nos metodos publicos.
// - Risco de runtime: formato de payload/resposta nao e mais verificado em compilacao.
