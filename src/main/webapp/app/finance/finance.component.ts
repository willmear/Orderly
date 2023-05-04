import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ITarget } from 'app/entities/target/target.model';
import { ChartData, ChartType } from 'chart.js';
import { data } from 'cypress/types/jquery';
import { TargetService } from 'app/entities/target/service/target.service';
import { IChart } from './chart.model';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TargetEditComponent } from './dialogs/target-edit/target-edit.component';
import { IUser } from 'app/admin/user-management/user-management.model';
import { ITEM_DELETED_EVENT, ITEM_SAVED_EVENT } from 'app/config/navigation.constants';
import { UserOrdersService } from 'app/entities/user-orders/user-orders/service/user-orders.service';

@Component({
  selector: 'jhi-finance',
  templateUrl: './finance.component.html',
  styleUrls: ['./finance.component.css'],
})
export class FinanceComponent implements OnInit {
  business: string;
  pieChartData!: ChartData<ChartType, number[], string>;
  pieChartLabels: string[] = [];
  pieChartDataTwo!: ChartData<ChartType, number[], string>;
  pieChartLabelsTwo: string[] = [];
  barChartData!: ChartData<ChartType, number[], string>;
  barChartLabels: string[] = [];
  barChartDataTwo!: ChartData<ChartType, number[], string>;
  barChartLabelsTwo: string[] = [];
  targets: ITarget[];
  target: any;
  chartOneTimescale: string = 'Timescale';
  chartTwoTimescale: string = 'Timescale';
  timeScales: string[] = ['One Month', 'Six Months', 'One Year'];

  constructor(
    private http: HttpClient,
    protected targetService: TargetService,
    protected modalService: NgbModal,
    protected userOrdersService: UserOrdersService
  ) {
    this.business = '';
    this.targets = [];
  }

  ngOnInit(): void {
    this.business = 'Business Name';

    // Load all targets
    this.targetService.query().subscribe((res: HttpResponse<ITarget[]>) => this.onTargetSuccess(res.body));

    // Load chart that corresponds to the selected dropdown option
    switch (this.chartOneTimescale) {
      case 'One Month':
        this.userOrdersService.chart1OneMonth().subscribe((res: HttpResponse<Map<String, number>>) => this.onChartOneSuccess(res.body));
        break;
      case 'Six Months':
        this.userOrdersService.chart1SixMonths().subscribe((res: HttpResponse<Map<String, number>>) => this.onChartOneSuccess(res.body));
        break;
      case 'One Year':
        this.userOrdersService.chart1OneYear().subscribe((res: HttpResponse<Map<String, number>>) => this.onChartOneSuccess(res.body));
        break;
      default:
        this.userOrdersService.chart1().subscribe((res: HttpResponse<Map<String, number>>) => this.onChartOneSuccess(res.body));
        break;
    }

    // Load chart that corresponds to the selected dropdown option
    switch (this.chartTwoTimescale) {
      case 'One Month':
        this.userOrdersService.chart2OneMonth().subscribe((res: HttpResponse<Map<String, number>>) => this.onChartTwoSuccess(res.body));
        break;
      case 'Six Months':
        this.userOrdersService.chart2SixMonths().subscribe((res: HttpResponse<Map<String, number>>) => this.onChartTwoSuccess(res.body));
        break;
      case 'One Year':
        this.userOrdersService.chart2OneYear().subscribe((res: HttpResponse<Map<String, number>>) => this.onChartTwoSuccess(res.body));
        break;
      default:
        this.userOrdersService.chart2().subscribe((res: HttpResponse<Map<String, number>>) => this.onChartTwoSuccess(res.body));
        break;
    }

    // Load charts
    this.userOrdersService.chart3().subscribe((res: HttpResponse<Map<String, number>>) => this.onChartThreeSuccess(res.body));
    this.userOrdersService.chart4().subscribe((res: HttpResponse<Map<String, Array<number>>>) => this.onChartFourSuccess(res.body));
  }

  // Change dropdown display for chart 1
  onChangeTimescaleOne(newTimescale: string): void {
    this.chartOneTimescale = newTimescale;
    this.ngOnInit();
  }
  // Change dropdown display for chart 2
  onChangeTimescaleTwo(newTimescale: string): void {
    this.chartTwoTimescale = newTimescale;
    this.ngOnInit();
  }

  // Create Sales chart
  onChartOneSuccess(data: Map<String, number> | null): void {
    if (data == null) {
      console.log(null);
    } else {
      this.pieChartLabels = Object.keys(data);
      this.pieChartData = {
        labels: this.pieChartLabels,
        datasets: [
          {
            data: Object.values(data),
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', 'grey', 'green'],
            hoverBackgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', 'grey', 'green'],
          },
        ],
      };
    }
  }

  // Create Revenue by Product chart
  onChartTwoSuccess(data: Map<String, number> | null): void {
    if (data == null) {
      console.log(null);
    } else {
      this.pieChartLabelsTwo = Object.keys(data);
      this.pieChartDataTwo = {
        labels: this.pieChartLabelsTwo,
        datasets: [
          {
            data: Object.values(data),
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', 'grey', 'green'],
            hoverBackgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', 'grey', 'green'],
          },
        ],
      };
    }
  }

  // Create Total Revenue chart
  onChartThreeSuccess(data: Map<String, number> | null): void {
    if (data == null) {
      console.log(null);
    } else {
      this.barChartLabels = Object.keys(data);
      this.barChartData = {
        labels: this.barChartLabels,
        datasets: [
          {
            label: 'Revenue by Month',
            data: Object.values(data),
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', 'grey', 'green'],
            hoverBackgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', 'grey', 'green'],
          },
        ],
      };
    }
  }

  // Create Revenue and Loss chart
  onChartFourSuccess(data: Map<String, Array<number>> | null): void {
    if (data == null) {
      console.log(null);
    } else {
      let vals = Object.values(data);
      vals = vals.join(', ').split(', ');
      let valsString: String = vals.join();
      let first = valsString.split(',');
      let revList = first.filter((item, index) => index % 2 === 0).map(str => parseInt(str));
      let lossList = first.filter((item, index) => index % 2 != 0).map(str => parseInt(str));

      this.barChartLabelsTwo = Object.keys(data);
      this.barChartDataTwo = {
        labels: this.barChartLabelsTwo,
        datasets: [
          {
            label: 'Revenue',
            data: revList,
            backgroundColor: ['#FF6384'],
            hoverBackgroundColor: ['#FF6384'],
          },
          {
            label: 'Loss',
            data: lossList,
            backgroundColor: ['red'],
            hoverBackgroundColor: ['#36A2EB'],
          },
        ],
      };
    }
  }

  // Opens edit target modal after clicking 'Edit' on individual target
  // Takes the id for possible calls to either delete or edit the target
  onEditTarget(id: number, user: IUser | null | undefined): void {
    const modalRef = this.modalService.open(TargetEditComponent, { size: 'lg', backdrop: 'static', centered: true, keyboard: true });
    modalRef.componentInstance.id = id;
    modalRef.componentInstance.currUser = user;
    modalRef.result.then(response => {
      if (response === ITEM_SAVED_EVENT || response === ITEM_DELETED_EVENT) {
        this.ngOnInit();
      }
    });
  }

  // Create a new target. text sent from html, id set to null.
  onTargetCreate(targets: { id: null; text: string }) {
    this.targetService.create(targets).subscribe(res => {
      console.log(targets);
      this.ngOnInit();
    });
  }

  // Adds each target to targets list
  addTarget(target: ITarget): void {
    this.target.addTarget({
      id: target.id,
      text: target.text,
    });
  }

  // Called after http request to get all targets is successful
  onTargetSuccess(targets: ITarget[] | null): void {
    this.targets = targets || [];
    this.targets.forEach(e => this.addTarget(e));
  }

  // Delete target given target id
  onTargetDelete(id: number) {
    this.targetService.delete(id).subscribe(res => {
      console.log(id);
      this.ngOnInit();
    });
  }
}
