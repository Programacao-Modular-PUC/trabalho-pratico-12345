export interface Cliente {
  id: number;
  nome: string;
  cpf: string;
  email?: string | null;
  telefone?: string | null;
}

export interface ClientePayload {
  nome: string;
  cpf: string;
  email?: string;
  telefone?: string;
}
