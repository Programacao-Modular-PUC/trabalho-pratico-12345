import { api } from '../lib/api';

/** @returns {Promise<Object[]>} */
export async function listClientes() {
  const response = await api.get('/clientes');
  return response.data;
}

/** @param {number} id @returns {Promise<Object>} */
export async function getCliente(id) {
  const response = await api.get(`/clientes/${id}`);
  return response.data;
}

/** @param {Object} payload @returns {Promise<Object>} */
export async function createCliente(payload) {
  const response = await api.post('/clientes', payload);
  return response.data;
}

/** @param {number} id @param {Object} payload @returns {Promise<Object>} */
export async function updateCliente(id, payload) {
  const response = await api.put(`/clientes/${id}`, payload);
  return response.data;
}

/** @param {number} id @returns {Promise<void>} */
export async function deleteCliente(id) {
  await api.delete(`/clientes/${id}`);
}

// CONVERSAO
// - Removidos: type-only import, anotacoes de tipo e generics de axios.
// - Adaptacao especial: JSDoc simples nos metodos publicos.
// - Risco de runtime: formato de payload/resposta nao e mais verificado em compilacao.