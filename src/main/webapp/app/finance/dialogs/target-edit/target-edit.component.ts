import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { IUser } from 'app/admin/user-management/user-management.model';
import { ITEM_DELETED_EVENT, ITEM_SAVED_EVENT } from 'app/config/navigation.constants';
import { TargetService } from 'app/entities/target/service/target.service';
import { ITarget } from 'app/entities/target/target.model';
import { FinanceComponent } from 'app/finance/finance.component';

@Component({
  selector: 'jhi-target-edit',
  templateUrl: './target-edit.component.html',
  styleUrls: ['./target-edit.component.scss'],
})
export class TargetEditComponent implements OnInit {
  id: number;
  currUser: any;
  constructor(protected activeModal: NgbActiveModal, protected targetService: TargetService) {
    this.id = -1;
  }

  ngOnInit(): void {}

  // Close the modal
  close(): void {
    this.activeModal.close();
  }

  // Save an edited target
  onTargetSave(targets: { id: number; newText: string; user: null }): void {
    targets.id = this.id;
    targets.user = this.currUser;
    this.targetService.update(targets).subscribe(res => {
      this.activeModal.close(ITEM_SAVED_EVENT);
    });
  }

  // Delete a target
  onDelete(): void {
    this.targetService.delete(this.id).subscribe(res => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
