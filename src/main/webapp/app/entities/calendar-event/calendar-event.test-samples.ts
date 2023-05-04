import dayjs from 'dayjs/esm';

import { ICalendarEvent, NewCalendarEvent } from './calendar-event.model';

export const sampleWithRequiredData: ICalendarEvent = {
  id: 8653,
  name: 'port',
  start: dayjs('2023-03-10T21:17'),
  end: dayjs('2023-03-11T01:53'),
};

export const sampleWithPartialData: ICalendarEvent = {
  id: 63288,
  name: 'redundant',
  start: dayjs('2023-03-11T01:00'),
  end: dayjs('2023-03-11T12:02'),
  location: 'up Beauty',
};

export const sampleWithFullData: ICalendarEvent = {
  id: 28457,
  name: 'Stravenue',
  description: 'Belarussian',
  start: dayjs('2023-03-11T13:30'),
  end: dayjs('2023-03-11T14:07'),
  location: 'Lebanon',
};

export const sampleWithNewData: NewCalendarEvent = {
  name: 'Synergized',
  start: dayjs('2023-03-11T06:45'),
  end: dayjs('2023-03-11T19:20'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
