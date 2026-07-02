import { useMutation, useQueryClient } from '@tanstack/react-query';
import { aluguelKeys } from './useAluguel';
import { confirmPagamento } from '../services/pagamento.service';

export function useConfirmPagamento() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: confirmPagamento,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: aluguelKeys.all }),
  });
}
