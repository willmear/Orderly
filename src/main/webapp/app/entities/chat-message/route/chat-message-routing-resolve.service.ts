import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IChatMessage } from '../chat-message.model';
import { ChatMessageService } from '../service/chat-message.service';

@Injectable({ providedIn: 'root' })
export class ChatMessageRoutingResolveService implements Resolve<IChatMessage | null> {
  constructor(protected service: ChatMessageService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IChatMessage | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((chatMessage: HttpResponse<IChatMessage>) => {
          if (chatMessage.body) {
            return of(chatMessage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
