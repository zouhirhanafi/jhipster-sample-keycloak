import { ICategorie } from 'app/shared/model/categorie.model';

export interface IArticle {
  id?: number;
  designation?: string | null;
  pu?: number | null;
  categorie?: ICategorie | null;
}

export const defaultValue: Readonly<IArticle> = {};
