import { z } from 'zod';

export const residenciaSchema = z.object({
  nome: z.string().min(1, 'Informe o nome.'),
  endereco: z.string().min(1, 'Informe o endereço.'),
  descricao: z.string().optional(),
});
