import type { Quarto } from './quarto.types';

export interface Residencia {
  id: number;
  nome: string;
  endereco: string;
  descricao?: string | null;
  quartos?: Quarto[];
}

export interface ResidenciaPayload {
  nome: string;
  endereco: string;
  descricao?: string;
}
