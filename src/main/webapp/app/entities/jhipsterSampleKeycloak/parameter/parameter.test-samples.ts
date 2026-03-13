import { IParameter, NewParameter } from './parameter.model';

export const sampleWithRequiredData: IParameter = {
  id: 30483,
};

export const sampleWithPartialData: IParameter = {
  id: 24686,
  activated: false,
  lib3: 'énumérer du moment que faciliter',
  refExterne: 'dynamique',
  val1: "adversaire à l'exception de",
  val2: 'tellement',
  val3: 'soit',
};

export const sampleWithFullData: IParameter = {
  id: 16347,
  label: 'moderne alors que sauvage',
  activated: true,
  lib2: 'sale étant donné que triathlète',
  lib3: 'souple dépasser épargner',
  refExterne: 'aussitôt que ferme',
  val1: 'calme',
  val2: 'cot cot blablabla',
  val3: 'dès que',
  ordre: 28804,
};

export const sampleWithNewData: NewParameter = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
