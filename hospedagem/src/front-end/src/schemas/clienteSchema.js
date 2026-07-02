import { z } from 'zod';

export const clienteSchema = z.object({
  nome: z.string().min(1, 'Informe o nome.'),
  cpf: z.string().min(1, 'Informe o CPF.'),
  email: z.string().email('E-mail inválido.').or(z.literal('')).optional(),
  telefone: z.string().optional(),
});
