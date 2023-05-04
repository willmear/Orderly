import { IUser } from "../user/user.model";

export interface ITarget {
  id: number;
  text?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTarget = Omit<ITarget, 'id'> & { id: null };
