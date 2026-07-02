import { z } from 'zod';

export const quartoSchema = z
  .object({
    tipo: z.enum(['INDIVIDUAL', 'DUPLO', 'FAMILIA']),
    valorBase: z.coerce.number().min(0, 'Informe um valor base válido.'),
    possuiAR: z.boolean(),
    possuiHidro: z.boolean(),
    residenciaId: z.coerce.number().min(1, 'Selecione uma residência.'),
    numeroDeCamas: z.coerce.number().optional(),
    adicionalPorCama: z.coerce.number().optional(),
    tipoCama: z.enum(['CASAL', 'QUEEN', 'KING']).optional(),
    possuiBerco: z.boolean(),
    quantidadeDeAmbientes: z.coerce.number().optional(),
    camasSolteiro: z.coerce.number().min(0).optional(),
    camasCasal: z.coerce.number().min(0).optional(),
    camasQueen: z.coerce.number().min(0).optional(),
    camasKing: z.coerce.number().min(0).optional(),
  })
  .superRefine((values, context) => {
    if (values.tipo === 'INDIVIDUAL' && (!values.numeroDeCamas || values.numeroDeCamas < 1)) {
      context.addIssue({ code: 'custom', path: ['numeroDeCamas'], message: 'Informe pelo menos 1 cama.' });
    }
    if (values.tipo === 'DUPLO' && !values.tipoCama) {
      context.addIssue({ code: 'custom', path: ['tipoCama'], message: 'Informe o tipo de cama.' });
    }
    if (values.tipo === 'FAMILIA') {
      const totalCamas = ['camasSolteiro', 'camasCasal', 'camasQueen', 'camasKing']
        .reduce((total, campo) => total + Number(values[campo] || 0), 0);
      if (!values.quantidadeDeAmbientes || values.quantidadeDeAmbientes < 1) {
        context.addIssue({ code: 'custom', path: ['quantidadeDeAmbientes'], message: 'Informe pelo menos 1 ambiente.' });
      }
      if (totalCamas < 1) {
        context.addIssue({ code: 'custom', path: ['camasSolteiro'], message: 'Informe pelo menos 1 cama.' });
      }
    }
  });
