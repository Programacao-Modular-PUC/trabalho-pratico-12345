import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;

    if (status === 401) {
      window.location.assign('/login');
    }

    if (status >= 500) {
      window.dispatchEvent(
        new CustomEvent('app:toast', {
          detail: {
            type: 'error',
            message: 'Erro interno no servidor.',
          },
        }),
      );
    }

    return Promise.reject(error);
  },
);

// CORS deve ser configurado no backend Java com @CrossOrigin ou CorsFilter.

// CONVERSAO
// - Apenas renomeado de .ts para .js, sem alteracoes de logica.
// - Sem adaptacoes especiais.
// - Risco de runtime: nenhum novo risco identificado.