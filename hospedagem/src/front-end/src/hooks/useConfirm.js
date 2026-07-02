import { useCallback } from 'react';

export function useConfirm() {
  return useCallback((mensagem) => window.confirm(mensagem), []);
}
