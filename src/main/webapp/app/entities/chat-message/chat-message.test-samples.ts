import { IChatMessage, NewChatMessage } from './chat-message.model';

export const sampleWithRequiredData: IChatMessage = {
  id: 16788,
};

export const sampleWithPartialData: IChatMessage = {
  id: 25880,
};

export const sampleWithFullData: IChatMessage = {
  id: 80495,
};

export const sampleWithNewData: NewChatMessage = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
