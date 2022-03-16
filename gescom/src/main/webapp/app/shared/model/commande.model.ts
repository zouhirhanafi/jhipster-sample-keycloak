import dayjs from 'dayjs';

export interface ICommande {
  id?: number;
  date?: string | null;
  client?: number | null;
}

export const defaultValue: Readonly<ICommande> = {};
