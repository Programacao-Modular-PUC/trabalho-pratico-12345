import type { Cliente } from './cliente.types';
import type { Quarto } from './quarto.types';

export interface Aluguel {
  id: number;
  cliente: Cliente;
  quarto: Quarto;
  dataEntrada: string;
  dataSaida: string;
  numeroDeHospedes: number;
  solicitouBerco: boolean;
  valorTotal: number;
}

export interface AluguelPayload {
  clienteId: number;
  quartoId: number;
  dataEntrada: string;
  dataSaida: string;
  numeroDeHospedes: number;
  solicitouBerco: boolean;
}
