import dayjs from 'dayjs/esm';

import { IConteneur, NewConteneur } from './conteneur.model';

export const sampleWithRequiredData: IConteneur = {
  id: 19713,
};

export const sampleWithPartialData: IConteneur = {
  id: 17790,
  statut: 14025,
  dateSortie: dayjs('2024-09-28T19:04'),
};

export const sampleWithFullData: IConteneur = {
  id: 18821,
  statut: 19999,
  dateEntree: dayjs('2024-09-28T09:54'),
  dateSortie: dayjs('2024-09-28T22:35'),
  zone: 4906,
  ligne: 16488,
  colonne: 29574,
  commentaire: 'dedans calme',
};

export const sampleWithNewData: NewConteneur = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
