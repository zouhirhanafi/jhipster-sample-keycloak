import { IArticle } from 'app/shared/model/article.model';

export interface ICategorie {
  id?: number;
  name?: string | null;
  articles?: IArticle[] | null;
}

export const defaultValue: Readonly<ICategorie> = {};
