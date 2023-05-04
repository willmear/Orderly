import dayjs from 'dayjs/esm';

import { IUserOrders, NewUserOrders } from './user-orders.model';

export const sampleWithRequiredData: IUserOrders = {
  id: 27855,
  orderNum: 2543,
  orderDescription: 'navigating Field',
  dueDate: dayjs('2023-04-25'),
  customerID: 51469,
  price: 94242,
};

export const sampleWithPartialData: IUserOrders = {
  id: 34362,
  orderNum: 59825,
  orderDescription: 'Accounts context-sensitive array',
  dateOrdered: dayjs('2023-04-25'),
  dueDate: dayjs('2023-04-25'),
  customerID: 86339,
  price: 17272,
};

export const sampleWithFullData: IUserOrders = {
  id: 63472,
  orderNum: 90296,
  orderDescription: 'Money Decentralized',
  deliveryAddress: 'system Clothing',
  dateOrdered: dayjs('2023-04-25'),
  dueDate: dayjs('2023-04-24'),
  customerID: 73792,
  productionTime: 5505,
  productionCost: 22215,
  price: 8878,
};

export const sampleWithNewData: NewUserOrders = {
  orderNum: 59203,
  orderDescription: 'Intelligent Leone withdrawal',
  dueDate: dayjs('2023-04-25'),
  customerID: 94133,
  price: 35973,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
