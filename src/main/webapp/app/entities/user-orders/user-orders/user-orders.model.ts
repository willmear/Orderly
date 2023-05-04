import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IUserOrders {
  id: number;
  orderNum?: number | null;
  orderDescription?: string | null;
  deliveryAddress?: string | null;
  dateOrdered?: dayjs.Dayjs | null;
  dueDate?: dayjs.Dayjs | null;
  customerID?: number | null;
  productionTime?: number | null;
  productionCost?: number | null;
  price?: number | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewUserOrders = Omit<IUserOrders, 'id'> & { id: null };
