import { z } from 'zod';

export const aluguelSchema = z
  .object({
    clienteId: z.coerce.number().min(1, 'Selecione um cliente.'),
    quartoId: z.coerce.number().min(1, 'Selecione um quarto.'),
    dataEntrada: z.string().min(1, 'Informe a data de entrada.'),
    dataSaida: z.string().min(1, 'Informe a data de saída.'),
    numeroDeHospedes: z.coerce.number().min(1, 'Informe pelo menos 1 hóspede.'),
    solicitouBerco: z.boolean(),
  })
  .refine((values) => values.dataSaida > values.dataEntrada, {
    path: ['dataSaida'],
    message: 'A saída deve ser posterior à entrada.',
  });
