export function getApiErrorMessage(error) {
  return (
    error?.response?.data?.erro ||
    error?.response?.data?.message ||
    error?.message ||
    'Nao foi possivel concluir a operacao.'
  );
}

export function formatCurrency(value) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(Number(value || 0));
}
