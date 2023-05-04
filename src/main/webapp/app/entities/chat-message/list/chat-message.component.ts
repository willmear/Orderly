import { Component, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { combineLatest, Observable, switchMap, tap } from 'rxjs';
// import { Router } from '@angular/router';
import dayjs from 'dayjs/esm';

import { IChatMessage } from '../chat-message.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ASC, DESC, SORT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { EntityArrayResponseType, ChatMessageService } from '../service/chat-message.service';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { IUser } from 'app/entities/user/user.model';
import { HttpResponse } from '@angular/common/http';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-chat-message',
  templateUrl: './chat-message.component.html',
  styleUrls: ['./chat-message.component.css'],
})
export class ChatMessageComponent implements OnInit {
  users: IUser[];
  receiverUsers: IUser[];
  currentUser?: IUser;
  receiverUser?: IUser;
  chatMessages?: IChatMessage[];
  isLoading = false;
  receiverIdSorted?: IUser[];
  predicate = 'id';
  ascending = true;
  filteredMessages: IChatMessage[] = [];

  itemsPerPage = ITEMS_PER_PAGE;
  links: { [key: string]: number } = {
    last: 0,
  };
  page = 1;
  inputValue: string = '';

  constructor(
    protected chatMessageService: ChatMessageService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected parseLinks: ParseLinks
  ) {
    combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending))
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
          this.getCurrentUser();
        },
      });

    this.userService.query().subscribe((res: HttpResponse<IUser[]>) => this.onUserSuccess(res.body));

    this.users = [];
    this.receiverUsers = [];
    this.currentUser = undefined;
    this.receiverUser = undefined;
  }

  reset(): void {
    this.page = 1;
    this.chatMessages = [];
    this.load();
  }

  loadPage(page: number): void {
    this.page = page;
    this.load();
  }

  //   trackId = (_index: number, item: IChatMessage): number => this.chatMessageService.getChatMessageIdentifier(item);

  ngOnInit(): void {
    this.load();
    this.getCurrentUser();

    this.userService.query().subscribe((res: HttpResponse<IUser[]>) => this.onUserSuccess(res.body));
  }

  onUserSuccess(users: IUser[] | null): void {
    this.users = users || [];
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.page, this.predicate, this.ascending);
  }

  navigateToPage(page = this.page): void {
    this.handleNavigation(page, this.predicate, this.ascending);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending))
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.chatMessages = dataFromBody;
    this.getReceiverNames();
    console.log(this.receiverUsers);
  }

  protected fillComponentAttributesFromResponseBody(data: IChatMessage[] | null): IChatMessage[] {
    const chatMessagesNew = this.chatMessages ?? [];
    if (data) {
      for (const d of data) {
        if (chatMessagesNew.map(op => op.id).indexOf(d.id) === -1) {
          chatMessagesNew.push(d);
        }
      }
    }
    return chatMessagesNew;
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links = this.parseLinks.parse(linkHeader);
    } else {
      this.links = {
        last: 0,
      };
    }
  }

  protected queryBackend(page?: number, predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = page ?? 1;
    const queryObject = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.chatMessageService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected getCurrentUser(): void {
    this.chatMessageService.getCurrentUser().subscribe(
      (res: HttpResponse<IUser>) => this.onCurrentUserSuccess(res.body),
      (err: any) => console.error(err)
    );
  }

  onCurrentUserSuccess(user: IUser | null): void {
    if (user !== null) {
      this.currentUser = user;
      console.log('currentUser');
      console.log(this.currentUser);
    }
  }

  // protected getReceiverNames() : void {
  //    this.receiverUsers = this.sortMessages(this.chatMessages!!)
  // }

  protected getReceiverNames(): void {
    const currentUserMessages = this.chatMessages!!.filter(
      message => message.senderId === this.currentUser?.id || message.receiverId === this.currentUser?.id
    );
    this.receiverUsers = this.sortMessages(currentUserMessages);
  }

  private sortMessages(messages: IChatMessage[]): IUser[] {
    // Step 1: Create a map to store the latest message for each receiverId
    if (messages.length === 0) {
      return [];
    }
    const latestMessages: { [ReceiverId: number]: IChatMessage } = {};
    messages.forEach(message => {
      const ReceiverId = message.receiverId ? message.receiverId : null;
      if (ReceiverId && (!latestMessages[ReceiverId] || message.timestamp! >= latestMessages[ReceiverId].timestamp!)) {
        latestMessages[ReceiverId] = message;
      }
    });

    // Step 2: Sort the latest messages by timestamp, and then by receiverId
    const sortedMessages = Object.entries(latestMessages).sort(([ReceiverId1, message1], [ReceiverId2, message2]) => {
      if (!message2.timestamp) {
        return -1;
      } else if (!message1.timestamp) {
        return 1;
      } else {
        const timestampComparison = dayjs(message2.timestamp).diff(dayjs(message1.timestamp));
        if (timestampComparison !== 0) {
          return timestampComparison;
        } else {
          return +ReceiverId1 - +ReceiverId2;
        }
      }
    });

    //   Step 3: Return the receiverIds of the sorted messages
    var receiverIds = sortedMessages.map(([ReceiverId, _]) => +ReceiverId);
    console.log(receiverIds);
    return this.users
      .filter(user => receiverIds.includes(user.id))
      .filter(user => user.id != this.currentUser?.id)
      .sort((user1, user2) => {
        const index1 = receiverIds.indexOf(user1.id);
        const index2 = receiverIds.indexOf(user2.id);
        return index1 - index2;
      });
  }

  filterMessages(receiverId: number): void {
    console.log('currentUser:', this.currentUser);
    console.log('receiverId:', receiverId);
    console.log('chatMessages:', this.chatMessages);

    const filteredMessages = this.chatMessages
      ? this.chatMessages.filter(
          message =>
            (message.senderId === this.currentUser?.id && message.receiverId === receiverId) ||
            (message.senderId === receiverId && message.receiverId === this.currentUser?.id)
        )
      : [];
    console.log('filteredMessages:', filteredMessages);

    this.filteredMessages = filteredMessages;
  }

  refreshPage() {
    location.reload();
  }

  storeInput() {
    console.log(this.inputValue);
    const newChatMessage: IChatMessage = {
      id: null,
      senderId: this.currentUser?.id,
      receiverId: this.receiverUser?.id,
      message: this.inputValue,
      timestamp: dayjs(),
    };
    console.log(newChatMessage);
    var test = this.chatMessageService.save(newChatMessage).subscribe();
    console.log(test);
  }

  protected setReceiverId(userId: number) {
    this.receiverUser = this.users.filter(user => user.id === userId)[0];
    console.log(this.receiverUser);
    this.filterMessages(this.receiverUser.id);
  }

  protected handleNavigation(page = this.page, predicate?: string, ascending?: boolean): void {
    const queryParamsObj = {
      page,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
