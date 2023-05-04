import { ITarget, NewTarget } from './target.model';

export const sampleWithRequiredData: ITarget = {
  id: 74001,
};

export const sampleWithPartialData: ITarget = {
  id: 5270,
};

export const sampleWithFullData: ITarget = {
  id: 43449,
  text: 'Engineer salmon Soap',
};

export const sampleWithNewData: NewTarget = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
