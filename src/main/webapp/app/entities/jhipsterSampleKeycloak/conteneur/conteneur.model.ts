import dayjs from 'dayjs/esm';

export interface IConteneur {
  id: number;
  statut?: number | null;
  dateEntree?: dayjs.Dayjs | null;
  dateSortie?: dayjs.Dayjs | null;
  zone?: number | null;
  ligne?: number | null;
  colonne?: number | null;
  commentaire?: string | null;
}

export type NewConteneur = Omit<IConteneur, 'id'> & { id: null };
