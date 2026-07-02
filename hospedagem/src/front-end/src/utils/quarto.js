export function countCama(quarto, tipo) {
  return quarto?.listaDeCamas?.filter((cama) => cama === tipo).length || 0;
}

export function buildListaDeCamas(values) {
  return [
    ...Array(Number(values.camasSolteiro || 0)).fill('SOLTEIRO'),
    ...Array(Number(values.camasCasal || 0)).fill('CASAL'),
    ...Array(Number(values.camasQueen || 0)).fill('QUEEN'),
    ...Array(Number(values.camasKing || 0)).fill('KING'),
  ];
}

export function getQuartoResumo(quarto) {
  if (quarto.tipo === 'INDIVIDUAL') {
    return `${quarto.numeroDeCamas} cama(s)`;
  }
  if (quarto.tipo === 'DUPLO') {
    return `${quarto.tipoCama}${quarto.possuiBerco ? ' + berço disponível' : ''}`;
  }
  return `${quarto.capacidadeMaxima ?? 0} hóspedes, ${quarto.quantidadeDeAmbientes} ambiente(s)`;
}
