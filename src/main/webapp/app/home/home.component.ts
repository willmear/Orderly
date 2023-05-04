import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { faBuilding } from '@fortawesome/free-solid-svg-icons';
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import { faDollar } from '@fortawesome/free-solid-svg-icons';
import { faBook } from '@fortawesome/free-solid-svg-icons';
import { faPaintbrush } from '@fortawesome/free-solid-svg-icons';
import { faCoffee } from '@fortawesome/free-solid-svg-icons';
import { faHome } from '@fortawesome/free-solid-svg-icons';
import { faComputer } from '@fortawesome/free-solid-svg-icons';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { DataUtils } from '../core/util/data-util.service';

@Component({
  selector: 'customer-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  faSearch = faSearch;
  faBuilding = faBuilding;
  faDollar = faDollar;
  faBook = faBook;
  faPaintbrush = faPaintbrush;
  faCoffee = faCoffee;
  faHome = faHome;
  faComputer = faComputer;

  private readonly destroy$ = new Subject<void>();
  // slides: any[] = new Array(3).fill({id: -1, src: '', title: '', subtitle: ''});

  constructor(private accountService: AccountService, private router: Router, protected dataUtils: DataUtils) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
    // this.slides[0] = {
    //   src: './content/images/orderlylogo.png',
    // };
    // this.slides[1] = {
    //   src: './content/images/businessimage2.jpg',
    // }
    // this.slides[2] = {
    //   src: './content/images/businessimage1.jpg',
    // }
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  businessSearch(): void {
    let search = document.getElementById('businessSearch') as HTMLInputElement;
    this.dataUtils.setSearch(search.value);
    this.router.navigate(['/businesses']);
  }

  businessFilter(input: string): void {
    this.dataUtils.setFilter(input);
    this.router.navigate(['/businesses']);
  }
}
