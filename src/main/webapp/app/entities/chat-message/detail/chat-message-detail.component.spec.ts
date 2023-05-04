import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ChatMessageDetailComponent } from './chat-message-detail.component';

describe('ChatMessage Management Detail Component', () => {
  let comp: ChatMessageDetailComponent;
  let fixture: ComponentFixture<ChatMessageDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChatMessageDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ chatMessage: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ChatMessageDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ChatMessageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load chatMessage on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.chatMessage).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
