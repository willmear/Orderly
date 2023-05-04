import { IBusinesses, NewBusinesses } from './businesses.model';

export const sampleWithRequiredData: IBusinesses = {
  id: 2923,
  name: 'redefine Way Electronics',
  summary: '../fake-data/blob/hipster.txt',
  type: 'Intelligent Handmade',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithPartialData: IBusinesses = {
  id: 10467,
  name: 'Fantastic Baht',
  summary: '../fake-data/blob/hipster.txt',
  type: 'Quality Synergized',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithFullData: IBusinesses = {
  id: 53869,
  name: 'payment',
  summary: '../fake-data/blob/hipster.txt',
  rating: 16799,
  location: 'parse generate Gorgeous',
  type: 'Director Account',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  rateCount: 0,
};

export const sampleWithNewData: NewBusinesses = {
  name: 'distributed Salad Operations',
  summary: '../fake-data/blob/hipster.txt',
  type: 'Senior system Investment',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
