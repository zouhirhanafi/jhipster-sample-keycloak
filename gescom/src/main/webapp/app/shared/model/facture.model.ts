import dayjs from 'dayjs';

export interface IFacture {
  id?: number;
  client?: number | null;
  date?: string | null;
}

export const defaultValue: Readonly<IFacture> = {};
