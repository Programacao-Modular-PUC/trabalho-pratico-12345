import type { Residencia } from './residencia.types';

export type TipoQuarto = 'INDIVIDUAL' | 'DUPLO' | 'FAMILIA';
export type TipoCama = 'CASAL' | 'QUEEN' | 'KING';
export type TipoCamaFamilia = 'SOLTEIRO' | 'CASAL' | 'QUEEN' | 'KING';

interface QuartoBase {
  id: number;
  tipo: TipoQuarto;
  valorBase: number;
  possuiAR: boolean;
  possuiHidro: boolean;
  residencia?: Residencia | null;
}

export interface QuartoIndividual extends QuartoBase {
  tipo: 'INDIVIDUAL';
  numeroDeCamas: number;
  adicionalPorCama: number;
}

export interface QuartoDuplo extends QuartoBase {
  tipo: 'DUPLO';
  tipoCama: TipoCama;
  solicitouBerco: boolean;
}

export interface QuartoFamilia extends QuartoBase {
  tipo: 'FAMILIA';
  listaDeCamas: TipoCamaFamilia[];
  quantidadeDeAmbientes: number;
  capacidadeMaxima: number;
}

export type Quarto = QuartoIndividual | QuartoDuplo | QuartoFamilia;

export interface QuartoPayload {
  tipo: TipoQuarto;
  valorBase: number;
  possuiAR: boolean;
  possuiHidro: boolean;
  residenciaId: number;
  numeroDeCamas?: number;
  adicionalPorCama?: number;
  tipoCama?: TipoCama;
  solicitouBerco?: boolean;
  listaDeCamas?: TipoCamaFamilia[];
  quantidadeDeAmbientes?: number;
}
