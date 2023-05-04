

import { IInventory, NewInventory } from './inventory.model';

export const sampleWithRequiredData: IInventory = {
  id: 70625,
  name: 'Island',
  quantity: 84577,
  status: 'International Sleek Italy'
};

export const sampleWithPartialData: IInventory = {
  id: 48509,
  name: 'Pizza Table reinvent',
  quantity: 54448,
  status: 'Florida'
};

export const sampleWithFullData: IInventory = {
  id: 31025,
  name: 'connecting',
  quantity: 94382,
  status: 'connecting'
};

export const sampleWithNewData: NewInventory = {
  name: 'Yuan',
  quantity: 14261,
  status: 'Missouri invoice',
  id: null
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
