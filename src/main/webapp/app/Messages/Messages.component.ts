import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-Messages',
  templateUrl: './Messages.component.html',
  styleUrls: ['./Messages.component.css'],
})
export class MessagesComponent implements OnInit {
  message: string;

  constructor() {
    this.message = '';
  }

  ngOnInit(): void {
    this.message = 'MessagesComponent message';
  }
}
