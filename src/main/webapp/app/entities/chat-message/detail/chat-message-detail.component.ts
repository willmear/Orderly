import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IChatMessage } from '../chat-message.model';

@Component({
  selector: 'jhi-chat-message-detail',
  templateUrl: './chat-message-detail.component.html',
})
export class ChatMessageDetailComponent implements OnInit {
  chatMessage: IChatMessage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ chatMessage }) => {
      this.chatMessage = chatMessage;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
