import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface ICalendarEvent {
  id: number;
  name?: string | null;
  description?: string | null;
  start?: dayjs.Dayjs | null;
  end?: dayjs.Dayjs | null;
  location?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCalendarEvent = Omit<ICalendarEvent, 'id'> & { id: null };
