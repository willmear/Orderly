import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IChatMessage } from '../chat-message.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../chat-message.test-samples';

import { ChatMessageService } from './chat-message.service';

const requireRestSample: IChatMessage = {
  ...sampleWithRequiredData,
};

describe('ChatMessage Service', () => {
  let service: ChatMessageService;
  let httpMock: HttpTestingController;
  let expectedResult: IChatMessage | IChatMessage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ChatMessageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ChatMessage', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addChatMessageToCollectionIfMissing', () => {
      it('should add a ChatMessage to an empty array', () => {
        const chatMessage: IChatMessage = sampleWithRequiredData;
        expectedResult = service.addChatMessageToCollectionIfMissing([], chatMessage);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(chatMessage);
      });

      it('should not add a ChatMessage to an array that contains it', () => {
        const chatMessage: IChatMessage = sampleWithRequiredData;
        const chatMessageCollection: IChatMessage[] = [
          {
            ...chatMessage,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addChatMessageToCollectionIfMissing(chatMessageCollection, chatMessage);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ChatMessage to an array that doesn't contain it", () => {
        const chatMessage: IChatMessage = sampleWithRequiredData;
        const chatMessageCollection: IChatMessage[] = [sampleWithPartialData];
        expectedResult = service.addChatMessageToCollectionIfMissing(chatMessageCollection, chatMessage);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(chatMessage);
      });

      it('should add only unique ChatMessage to an array', () => {
        const chatMessageArray: IChatMessage[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const chatMessageCollection: IChatMessage[] = [sampleWithRequiredData];
        expectedResult = service.addChatMessageToCollectionIfMissing(chatMessageCollection, ...chatMessageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const chatMessage: IChatMessage = sampleWithRequiredData;
        const chatMessage2: IChatMessage = sampleWithPartialData;
        expectedResult = service.addChatMessageToCollectionIfMissing([], chatMessage, chatMessage2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(chatMessage);
        expect(expectedResult).toContain(chatMessage2);
      });

      it('should accept null and undefined values', () => {
        const chatMessage: IChatMessage = sampleWithRequiredData;
        expectedResult = service.addChatMessageToCollectionIfMissing([], null, chatMessage, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(chatMessage);
      });

      it('should return initial array if no ChatMessage is added', () => {
        const chatMessageCollection: IChatMessage[] = [sampleWithRequiredData];
        expectedResult = service.addChatMessageToCollectionIfMissing(chatMessageCollection, undefined, null);
        expect(expectedResult).toEqual(chatMessageCollection);
      });
    });

    describe('compareChatMessage', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareChatMessage(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareChatMessage(entity1, entity2);
        const compareResult2 = service.compareChatMessage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareChatMessage(entity1, entity2);
        const compareResult2 = service.compareChatMessage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareChatMessage(entity1, entity2);
        const compareResult2 = service.compareChatMessage(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
