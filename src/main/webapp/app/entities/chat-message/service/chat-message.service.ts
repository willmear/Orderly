import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IChatMessage, NewChatMessage } from '../chat-message.model';

export type PartialUpdateChatMessage = Partial<IChatMessage> & Pick<IChatMessage, 'id'>;

export type EntityResponseType = HttpResponse<IChatMessage>;
export type EntityArrayResponseType = HttpResponse<IChatMessage[]>;
import { IUser } from 'app/entities/user/user.model';

@Injectable({ providedIn: 'root' })
export class ChatMessageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/chat-messages');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IChatMessage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IChatMessage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getByReceiverIdAndSenderId(senderId: number, receiverId: number): Observable<HttpResponse<IChatMessage[]>> {
    return this.http.get<IChatMessage[]>(`${this.resourceUrl}-by-ids?senderId=${senderId}&receiverId=${receiverId}`, {
      observe: 'response',
    });
  }

  //   getReceiverIdByTimestamp():Observable<HttpResponse<number[]>> {
  //     return this.http.get<number[]>(`${this.resourceUrl}/receivers`, { observe: 'response' });
  //   }

  getCurrentUser(): Observable<HttpResponse<IUser>> {
    return this.http.get<IUser>(`${this.resourceUrl}/current-user`, { observe: 'response' });
  }

  getChatMessageIdentifier(chatMessage: Pick<IChatMessage, 'id'>) {
    return chatMessage.id;
  }

  save(chatMessage: IChatMessage): Observable<EntityResponseType> {
    return this.http.post<IChatMessage>(this.resourceUrl, chatMessage, { observe: 'response' });
  }

  compareChatMessage(o1: Pick<IChatMessage, 'id'> | null, o2: Pick<IChatMessage, 'id'> | null): boolean {
    return o1 && o2 ? this.getChatMessageIdentifier(o1) === this.getChatMessageIdentifier(o2) : o1 === o2;
  }

  //   addChatMessageToCollectionIfMissing<Type extends Pick<IChatMessage, 'id'>>(
  //     chatMessageCollection: Type[],
  //     ...chatMessagesToCheck: (Type | null | undefined)[]
  //   ): Type[] {
  //     const chatMessages: Type[] = chatMessagesToCheck.filter(isPresent);
  //     if (chatMessages.length > 0) {
  //       const chatMessageCollectionIdentifiers = chatMessageCollection.map(
  //         chatMessageItem => this.getChatMessageIdentifier(chatMessageItem)!
  //       );
  //       const chatMessagesToAdd = chatMessages.filter(chatMessageItem => {
  //         const chatMessageIdentifier = this.getChatMessageIdentifier(chatMessageItem);
  //         if (chatMessageCollectionIdentifiers.includes(chatMessageIdentifier)) {
  //           return false;
  //         }
  //         chatMessageCollectionIdentifiers.push(chatMessageIdentifier);
  //         return true;
  //       });
  //       return [...chatMessagesToAdd, ...chatMessageCollection];
  //     }
  //     return chatMessageCollection;
  //   }
}
