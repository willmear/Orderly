import { Component, OnInit } from '@angular/core';
import { faBriefcase } from '@fortawesome/free-solid-svg-icons';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
})
export class SideNavComponent implements OnInit {
  faBriefcase = faBriefcase;
  account: Account | null = null;

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
  }
}
