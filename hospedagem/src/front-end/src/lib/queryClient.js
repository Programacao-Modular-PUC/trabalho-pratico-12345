import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

// CONVERSAO
// - Apenas renomeado de .ts para .js, sem alteracoes de logica.
// - Sem adaptacoes especiais.
// - Risco de runtime: nenhum novo risco identificado.