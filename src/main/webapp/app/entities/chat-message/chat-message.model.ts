import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IChatMessageForeignKey {
  senderId?: number | null;
  receiverId?: number | null;
}

export interface IChatMessage extends IChatMessageForeignKey {
  id?: number | null;
  message?: string;
  timestamp?: dayjs.Dayjs;
}

export class ChatMessage implements IChatMessage {
  constructor(
    public id?: number | null,
    public message?: string,
    public timestamp?: dayjs.Dayjs,
    public senderId?: number,
    public receiverId?: number
  ) {}
}

export type NewChatMessage = Omit<IChatMessage, 'id'> & { id: null };
