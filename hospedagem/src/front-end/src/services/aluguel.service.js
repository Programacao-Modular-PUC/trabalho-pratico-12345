import { api } from '../lib/api';

/** @returns {Promise<Object[]>} */
export async function listAlugueis() {
    const response = await api.get('/alugueis');
    return response.data;
}

/** @param {number} clienteId @returns {Promise<Object[]>} */
export async function listAlugueisByCliente(clienteId) {
    const response = await api.get(`/alugueis/cliente/${clienteId}`);
    return response.data;
}

/** @param {number} residenciaId @returns {Promise<Object[]>} */
export async function listAlugueisByResidencia(residenciaId) {
    const response = await api.get(`/alugueis/residencia/${residenciaId}`);
    return response.data;
}

/** @param {number} id @returns {Promise<Object>} */
export async function getReciboAluguel(id) {
    const response = await api.get(`/alugueis/${id}/recibo`);
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

/** @param {number} id @returns {Promise<Object>} */
export async function cancelAluguel(id) {
    const response = await api.patch(`/alugueis/${id}/cancelar`);
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
