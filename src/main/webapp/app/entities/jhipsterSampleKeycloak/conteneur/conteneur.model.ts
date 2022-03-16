import dayjs from 'dayjs/esm';

export interface IConteneur {
  id?: number;
  statut?: number | null;
  dateEntree?: dayjs.Dayjs | null;
  dateSortie?: dayjs.Dayjs | null;
  zone?: number | null;
  ligne?: number | null;
  colonne?: number | null;
  commentaire?: string | null;
}

export class Conteneur implements IConteneur {
  constructor(
    public id?: number,
    public statut?: number | null,
    public dateEntree?: dayjs.Dayjs | null,
    public dateSortie?: dayjs.Dayjs | null,
    public zone?: number | null,
    public ligne?: number | null,
    public colonne?: number | null,
    public commentaire?: string | null
  ) {}
}

export function getConteneurIdentifier(conteneur: IConteneur): number | undefined {
  return conteneur.id;
}
