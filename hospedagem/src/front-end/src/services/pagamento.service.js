import { api } from '../lib/api';

/** @param {number} id @returns {Promise<Object>} */
export async function confirmPagamento(id) {
  const response = await api.patch(`/pagamentos/${id}/confirmar`);
  return response.data;
}
