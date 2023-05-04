
import { IUser } from 'app/entities/user/user.model';

export interface IInventory {
  id: number;
  name?: string | null;
  quantity?: number | null;
  status?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewInventory = Omit<IInventory, 'id'> & { id: null };
