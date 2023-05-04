import {Component, OnInit} from '@angular/core';
import {Calendar} from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import listPlugin from '@fullcalendar/list';
import interactionPlugin from '@fullcalendar/interaction';

import {CalendarEventService} from 'app/entities/calendar-event/service/calendar-event.service';
import {ICalendarEvent} from "../entities/calendar-event/calendar-event.model";
import {HttpResponse} from "@angular/common/http";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {CalendarEventViewDialogComponent} from "./dialogs/calendar-event-view-dialog.component";
import {CONFIRMED_EVENT, ITEM_DELETED_EVENT, ITEM_SAVED_EVENT} from "../config/navigation.constants";
import {GoogleAuthService} from "./google-calendar/google-auth.service";
import {GoogleAuthSignoutDialogComponent} from "./google-calendar/google-auth-signout-dialog.component";
import {GoogleCalendarService} from "./google-calendar/google-calendar.service";
import {GoogleEventViewDialogComponent} from "./dialogs/google-event-view-dialog.component";
import {GoogleAuthUnauthorisedDialogComponent} from "./google-calendar/google-auth-unauthorised-dialog.component";
import {GoogleAuthSigninDialogComponent} from "./google-calendar/google-auth-signin-dialog.component";
import dayjs from "dayjs";
import {OrderViewDialogComponent} from "./dialogs/order-view-dialog.component";
import {DateNavDialogComponent} from "./dialogs/date-nav-dialog.component";
import {CalendarEventCreateDialogComponent} from "./dialogs/calendar-event-create-dialog.component";


@Component({
  selector: 'jhi-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css'],
})
export class CalendarComponent implements OnInit {
  events: ICalendarEvent[];

  orders : any[];

  googleEvents: any[];

  //Boolean flag to determine if the calendar is loading
  isLoading: boolean;

  //The actual calendar object
  calendar: any;

  //Variable to keep track of number of custom events currently displayed
  numCustom: number;

  //Variable to keep track of number of orders currently displayed
  numOrders: number;

  //Variable to keep track of number of Google Calendar events currently displayed
  numGoogle: number;

  //View descriptor - ensures accurate description of event summary (i.e. events this "month", or events this "week")
  view: string | undefined;

  //Boolean to determine whether custom events should be shown or not
  showCustom: boolean;

  //Boolean to determine whether orders should be shown or not
  showOrders: boolean;

  //Boolean to determine whether Google Calendar events should be shown or not
  showGoogle: boolean;

  //Boolean to determine whether Google Calendar events have loaded or not
  gcalLoaded: boolean;

  //Boolean to symbolise whether the user is currently signed into their Google account
  //and we have a valid access token we can use to access their calendar
  gauthed: boolean;

  //Store email address of signed in account to display to user
  googleAccount: any;


  // Initialise all global variables
  constructor(
    protected calendarEventService: CalendarEventService,
    protected modalService: NgbModal,
    protected googleAuthService: GoogleAuthService,
    protected googleCalendarService: GoogleCalendarService
  ) {
    this.events = [];
    this.orders = [];
    this.googleEvents = [];
    this.numCustom = 0;
    this.numOrders = 0;
    this.numGoogle = 0;
    this.view = "this month";
    this.isLoading = false;
    this.showCustom = true;
    this.showOrders = true;
    this.showGoogle = true;
    this.gcalLoaded = false;
    this.gauthed = false;
  }

  // Called on initialisation of the page
  ngOnInit() {
    let calendarEl: HTMLElement = document.getElementById('orderlyCalendarElement')!;

    this.calendar = new Calendar(calendarEl, {
      plugins: [dayGridPlugin, listPlugin, interactionPlugin],
      initialView: 'dayGridMonth',
      eventDisplay: 'block',
      buttonText: {
        today: 'Today'
      },
      views: {
        dayGridMonth: {buttonText: 'Month', dayMaxEventRows: 4},
        dayGridWeek: {buttonText: 'Week', dayMaxEventRows: 23},
        dayGridDay: {buttonText: 'Day', dayMaxEventRows: 23},
        listMonth: {buttonText: 'Agenda', dayMaxEventRows: false}
      },
      firstDay: 1,    //sets first day of the week to Monday
      eventInteractive: true,   //sets all events to be 'tab-able' for accessibility
      customButtons: {
        navToDate: {
          text: 'Go to',
          click: this.navigateToDate
        }
      },
      headerToolbar: {
        left: 'title',
        center: '',
        right: 'navToDate today prev,next dayGridMonth dayGridWeek dayGridDay listMonth'
      },
      datesSet: this.updateCounts.bind(this), //called when the view dates of the calendar are changed
      showNonCurrentDates: false,  //disables previous/following weeks being shown on month calendar view - helps get accurate event counts
      fixedWeekCount: false,   //number of weeks (rows) displayed varies on month, not a fixed number (default 6)
      eventClick: this.viewEvent,
      dateClick: this.handleDateClick
    });
    //gets calendar events from CalendarEventRepository, and runs them into the onCalendarSuccess function
    this.load();
    //finally renders the calendar
    this.calendar.render();
  }

  // Function called on each CalendarEvent to add it to the calendar
  public addEvent(ev: ICalendarEvent): void {
    this.calendar.addEvent({
      id: ("C" + ev.id),         //map FullCalendar 'id' property to 'id' of CalendarEvent. evIdPrefix is either "C" or "O", representing "Custom events" and "Orders" respectively
      title: ev.name,                   //map FullCalendar 'title' property to 'name' of CalendarEvent
      start: ev.start?.toISOString(),   //map FullCalendar 'start' property to 'start' of CalendarEvent
      end: ev.end?.toISOString(),       //map FullCalendar 'end' property to 'end' of CalendarEvent
      allDay: this.isAllDay(ev.start?.toISOString(), ev.end?.toISOString()),                    //determines whether the event is 00:00 -> 00:00
      color: '#A64C4C',
    })
  }

  public addGoogleEvent(ev: any) {
    //differentiate between all day events and timed events
    let start;
    let end;
    let allDay;
    if(ev.start.dateTime !== undefined) {
      //timed event (not all day)
      start = new Date(ev.start.dateTime.value).toISOString();
      end = new Date(ev.end.dateTime.value).toISOString();
      allDay = false;
    } else {
      start = new Date(ev.start.date.value).toISOString();
      end = new Date(ev.end.date.value).toISOString();
      allDay = true;
    }

    this.calendar.addEvent({
      id: ("G" + ev.id),
      title: ev.summary,
      description: ev.description,
      start: start,
      end: end,
      location: ev.location,
      allDay: allDay,
      color: '#628D57'
    });

  }

  public addOrder(order : any) {
    this.calendar.addEvent({
      id: ("O" + order.id),
      title: ("Order #" + order.orderNum),
      start: dayjs(order.dueDate).toISOString(),
      end: dayjs(order.dueDate).toISOString(),
      allDay: true,
      color: '#FFD580',
      textColor: '#000000'
    })
  }

  // Called on a successful HTTP request, and passes each calendarEvent returned to the addEvent
  // method, and updates the counts of the events
  public onCalendarSuccessCustom(event: ICalendarEvent[] | null): void {
    this.events = event || [];
    if (this.showCustom) {
      this.events.forEach(e => this.addEvent(e));
    }
    this.updateCounts();
  }

  public onCalendarSuccessOrders(orders : any) {
    this.orders = orders;
    if(this.showOrders) {
      this.orders.forEach(e => this.addOrder(e));
    }
    this.updateCounts();
  }


  //gets calendar events from CalendarEventRepository, and runs them into the onCalendarSuccess function
  async load() {
    this.isLoading = true;
    //check if the user is signed into their Google account, and we have a valid access token we can use
    await this.googleAuthCheck();

    this.removeEvents(this.showCustom, this.showOrders, this.showGoogle);

    const queryObject = {
      allEvents: false,   //all users, admin or not, should only view their own events on their calendar
    }
    this.calendarEventService.query(queryObject).subscribe((res: HttpResponse<ICalendarEvent[]>) => this.onCalendarSuccessCustom(res.body))
    this.calendarEventService.getOrders().subscribe((res) => this.onCalendarSuccessOrders(res.body));

    if (this.gauthed) {
      await this.getGoogleEvents();
    } else {
      if(this.gcalLoaded) {
        this.handleExpiredGoogleAccount();
      }
    }
    this.isLoading = false;
  }

  // Function called when the date range of the calendar is changed
  public updateCounts() {
    let dates = this.getVisibleRange();
    this.view = this.getReadableSummaryTitle();
    this.countCustom(dates);
    this.countOrders(dates);
    this.countGoogle(dates);
  }

  // Returns, with respect to the visible events on the calendar, the start date and end date
  private getVisibleRange() {
    let view = this.calendar.view;
    let start = view.activeStart.toISOString();
    let end = view.activeEnd.toISOString();
    return {start: start, end: end};
  }

  // Counts the number of custom events in the visible view, and update numCustom variable accordingly
  private countCustom(dates: any) {
    //parse start and end dates from the visible range
    let rangeStart = dates.start;
    let rangeEnd = dates.end;

    //new variable to count number of custom events and to override this.numCustom
    let newCustom = 0;

    //iterate over events in this.events array (populated by onCalendarSuccess()),
    // determine whether the event falls in the visible range, and if so, increment the
    // numCustom variable
    for (let i = 0; i < this.events.length; i++) {
      if (!((this.events[i].start?.toISOString() ?? -1) >= rangeEnd) && !((this.events[i].end?.toISOString() ?? -1) <= rangeStart)) {
        newCustom++;
      }
    }

    //finally, replace numCustom with newCustom
    //Note: angular will automatically get this new variable and replace it in HTML
    // via interpolation
    this.numCustom = newCustom;
  }

  private countOrders(dates: any) {
    let rangeStart = dates.start;
    let rangeEnd = dates.end;

    //new variable to count number of orders and to override this.numOrders
    let newOrders = 0;

    //iterate over orders in this.orders array (populated by onCalendarSuccessOrders()),
    // determine whether the event falls in the visible range, and if so, increment the
    // numOrders variable
    for (let i = 0; i < this.orders.length; i++) {
      if (!((dayjs(this.orders[i].dueDate).toISOString() ?? -1) >= rangeEnd) && !((dayjs(this.orders[i].dueDate).toISOString()) <= rangeStart)) {
        newOrders++;
      }
    }

    //finally, replace numCustom with newOrders
    //Note: angular will automatically get this new variable and replace it in HTML
    // via interpolation
    this.numOrders = newOrders;
  }

  private countGoogle(dates: any) {
    //parse start and end dates from the visible range
    let rangeStart = dates.start;
    let rangeEnd = dates.end;

    //new variable to count number of custom events and to override this.numGoogle
    let newGoogle = 0;

    //iterate over events in this.events array,
    // determine whether the event falls in the visible range, and if so, increment the
    // numGoogle variable
    //differentiate between all day and non-all day events
    for (let i = 0; i < this.googleEvents.length; i++) {
      if(this.googleEvents[i].start.dateTime !== undefined) {
        if (!((new Date(this.googleEvents[i].start.dateTime.value).toISOString().substring(0, 16) ?? -1) >= rangeEnd) && !(((new Date(this.googleEvents[i].end.dateTime.value).toISOString().substring(0, 16) ?? -1) <= rangeStart))) {
          newGoogle++;
        }
      } else {
        if (!((new Date(this.googleEvents[i].start.date.value).toISOString().substring(0, 16) ?? -1) >= rangeEnd) && !(((new Date(this.googleEvents[i].end.date.value).toISOString().substring(0, 16) ?? -1) <= rangeStart))) {
          newGoogle++;
        }
      }
    }

    //finally, replace numGoogle with newGoogle
    //Note: angular will automatically get this new variable and replace it in HTML
    // via interpolation
    this.numGoogle = newGoogle;
  }

  // Returns a string that describes the current view for the summary title
  private getReadableSummaryTitle(): string | undefined {
    let viewType = this.calendar.view.type;
    //get today's date as ISO string, set to midnight
    let today = new Date(new Date().setHours(0, 0, 0, 0)).toISOString();
    //get start and end dates in visible calendar
    let activeStart = this.calendar.view.activeStart.toISOString();
    let activeEnd = this.calendar.view.activeEnd.toISOString();

    if (viewType === "dayGridDay") {
      //if looking at day view
      if (activeStart === today) {
        //if looking at today
        return "today";
      } else {
        return "on " + this.calendar.view.title;
      }
    } else {
      if (viewType === "dayGridWeek") {
        //if looking at week view
        if (activeStart <= today && today < activeEnd) {
          //if looking at this current week
          return "this week";
        } else {
          return this.calendar.view.title;
        }
      } else {
        if (viewType === "dayGridMonth" || viewType === "listMonth") {
          //if looking at month view (grid or list)
          if (activeStart <= today && today < activeEnd) {
            //if current month
            return "this month";
          } else {
            return "in " + this.calendar.view.title;
          }
        } else {
          //error catching - if above checks fail for some reason, return such that it reads "Events summary"
          return "summary";
        }
      }

    }
  }

  // Determines whether an event is an "all-day" event
  // That is, that it spans from 00:00 on one day
  // to 00:00 on another day
  private isAllDay(start: any, end: any): boolean {
    //get date objects for passed in ISO strings
    let startDate = new Date(start);
    let endDate = new Date(end);

    //explicitly get the hours & minutes of these dates, and encapsulate into an object
    let startTime = {
      hours: startDate.getHours(),
      mins: startDate.getMinutes()
    }
    let endTime = {
      hours: endDate.getHours(),
      mins: endDate.getMinutes()
    }

    //determine whether all are set to 0
    return (startTime.hours === 0 && startTime.mins === 0 && endTime.hours === 0 && endTime.mins === 0);
  }

  // Function called to remove sets of events based on arguments
  private removeEvents(custom: boolean, orders: boolean, google: boolean) {
    if (custom) {
      this.events.forEach(e => this.removeEvent(e, "C"));
    }
    if (orders) {
      this.orders.forEach(e => this.removeEvent(e, "O"));
    }
    if (google) {
      this.googleEvents.forEach(e => this.removeEvent(e, "G"));
    }
  }

  // Function called to add sets of events based on arguments
  private addEvents(custom: boolean, orders: boolean, google: boolean) {
    if (custom) {
      this.events.forEach(e => this.addEvent(e));
    }
    if (orders) {
      this.orders.forEach(e => this.addOrder(e));
    }
    if (google) {
      this.googleEvents.forEach((e) => this.addGoogleEvent(e));
    }
  }

  //Function called on each event object in an array to remove it from the calendar
  private removeEvent(ev: any, evIdPrefix: string) {
    let event = this.calendar.getEventById(evIdPrefix + ev.id);
    if(event !== null) {
      event.remove();
    }
  }

  // Function called when the custom event toggle is clicked
  // Either displays custom events, or hides custom events
  public toggleCustom() {
    if (!this.showCustom) {
      //hide custom events
      this.removeEvents(true, false, false);
    } else {
      //show custom events
      this.addEvents(true, false, false);
    }
  }

  // Function called when the order toggle is clicked
  // Either displays orders, or hides orders
  public toggleOrders() {
    if (!this.showOrders) {
      //hide orders
      this.removeEvents(false, true, false);
    } else {
      //show orders
      this.addEvents(false, true, false);
    }
  }

  public toggleGoogle() {
    if (!this.showGoogle) {
      //hide google events
      this.removeEvents(false, false, true);
    } else {
      //show Google events
      this.addEvents(false, false, true);
    }
  }

  viewEvent = (info: any) => {
    let prefix = info.event.id.substring(0, 1);
    switch (prefix) {
      case 'G':
        this.viewGoogleEvent(info);
        break;
      case 'C':
        this.viewCustomEvent(info);
        break;
      case 'O':
        this.viewOrder(info);
        break;
      default:
        console.error("Unable to view event: unknown event type");
    }
  }

  // Function called when a custom event is clicked on
  viewCustomEvent(info: any) {
    //open the modal popup
    const modalRef = this.modalService.open(CalendarEventViewDialogComponent, {size: 'lg', backdrop: 'static', keyboard: true});

    //find the calendar event object in the array of calendar events from the last HTTP request
    let calendarEvent = this.searchLoadedCustomEvents(this.convertLongIdToShortId(info.event.id));

    //if found, pass that calendar event that has been clicked on to the modal being opened
    if (calendarEvent !== null) {
      modalRef.componentInstance.calendarEvent = calendarEvent;
    } else {
      //event not found, so error and close modal
      console.error("Error opening modal: event not found");
      modalRef.close();
      return;
    }
    //wait for the response from the modal on close
    modalRef.result.then((response) => {
      if (response === ITEM_DELETED_EVENT || response === ITEM_SAVED_EVENT) {
        //success, so ensure calendar is reloaded to get changes
        this.load();
      }
    }).catch(e => {
      console.error(e);
    })

  }

  viewOrder(info: any) {
    const modalRef = this.modalService.open(OrderViewDialogComponent, {size: 'lg', backdrop: 'static', keyboard: true});
    let order = this.searchLoadedOrders(info.event.id.substring(1));

    //if found, pass that order object that has been clicked on to the modal being opened
    if (order !== null) {
      modalRef.componentInstance.order = order;
    } else {
      //event not found, so error and close modal
      console.error("Error opening modal: order not found");
      modalRef.close();
      return;
    }
  }

  viewGoogleEvent(info: any) {
    const modalRef = this.modalService.open(GoogleEventViewDialogComponent, {size: 'lg', backdrop: 'static', keyboard: true});
    let googleEvent = this.searchLoadedGoogleEvents(info.event.id.substring(1));

    if (googleEvent !== null) {
      modalRef.componentInstance.googleEvent = googleEvent;
      modalRef.componentInstance.googleAccount = this.googleAccount;
      modalRef.componentInstance.allDay = info.event.allDay;
    } else {
      //event not found, so close modal and error
      console.error("Error opening modal: event not found");
      modalRef.close();
    }
  }

  // Function to search through the list of custom events based on an id parameter
  searchLoadedCustomEvents(id: number): ICalendarEvent | null {
    for (let i = 0; i < this.events.length; i++) {
      if (this.events[i].id === id) {
        return this.events[i];
      }
    }
    return null;
  }

  // Function to search through the list of orders based on an id parameter
  searchLoadedOrders(id: number): any {
    for (let i = 0; i < this.orders.length; i++) {
      if (this.orders[i].id == id) {
        return this.orders[i];
      } else {
      }
    }
    return null;
  }


  searchLoadedGoogleEvents(id: string): any {
    for (let i = 0; i < this.googleEvents.length; i++) {
      if (this.googleEvents[i].id === id) {
        return this.googleEvents[i];
      }
    }
    return null;
  }

  // Converts a 'Long ID' to a 'Short ID', i.e.:
  // LongID of a calendar event would be 'C1234', so it's conversion would be from 'C1234' -> '1234'
  // LongIDs are only used when stored in the calendar and Google Calendar of user (if linked)
  // in order to differentiate between custom events and orders as ID numbers may clash
  convertLongIdToShortId(id: string): number {
    return parseInt(id.substring(1), 10);
  }

  gauth(ask : boolean) {
    if(ask) {
      const modalRef = this.modalService.open(GoogleAuthSigninDialogComponent, {size: 'lg', backdrop: 'static', keyboard: true});
      //wait for response from modal
      //if user confirmed, trigger sign out request
      modalRef.result.then((response) => {
        if (response === CONFIRMED_EVENT) {
          this.handleGauth();
        }
      });
    } else {
      this.handleGauth();
    }
  }

  handleGauth() {
    this.googleAuthService.authenticate().subscribe({
      next: (res: HttpResponse<any>) => {
        window.location.href = res.body.url;
      },
      error: e => console.error("Error in authenticate method in calendar component: ", e)
    });
  }

  async googleAuthCheck() {
    const isAuthenticated = new Promise<boolean>((resolve, reject) => {
      this.googleAuthService.isAuthenticated().subscribe({
        next: (res: HttpResponse<any>) => {
          this.gauthed = res.body.authStatus;
          if (res.body.authStatus === true) {
            this.googleAccount = res.body.email;
          }
          resolve(true);
        },
        error: e => {
          console.error(e);
          reject(e);
        }
      });
    });
    //wait for the authentication check to complete before returning
    await isAuthenticated;
  }

  gsignout() {
    //open the modal to confirm whether the user wants to sign out
    const modalRef = this.modalService.open(GoogleAuthSignoutDialogComponent, {size: 'lg', backdrop: 'static', keyboard: true});

    //pass the user's email address to modal to confirm which account is being signed out of
    modalRef.componentInstance.googleAccount = this.googleAccount;

    //wait for response from modal
    //if user confirmed, trigger sign out request
    modalRef.result.then((response) => {
      if (response === CONFIRMED_EVENT) {

        this.googleAuthService.signOut().subscribe({
          next: (res: HttpResponse<any>) => {
            if (res.status === 200) {
              this.clearGoogle();
            }
          },
          error: e => console.error(e)
        });
      }
    });
  }

  async getGoogleEvents() {
    if (this.gauthed) {
      let getEventsComplete = new Promise<boolean>((resolve, reject) => {
        this.googleCalendarService.getEvents().subscribe({
          next: (res: HttpResponse<any>) => {
            if(res.status === 200) {
              this.googleEvents = res.body;
              this.addEvents(false, false, true);
              this.updateCounts();
              this.gcalLoaded = true;
              resolve(true);
            } else {
              if(res.status === 401) {
                //unauthorised, require re-authentication
                this.handleExpiredGoogleAccount();
              } else {
                reject();
              }
            }
          },
          error: e => {
            console.error(e)
            reject(e);
          }
        });
      });
      await getEventsComplete;
    }
  }

  handleExpiredGoogleAccount() {

    //open the modal to confirm whether the user wants to reauthenticate
    const modalRef = this.modalService.open(GoogleAuthUnauthorisedDialogComponent, {size: 'lg', backdrop: 'static', keyboard: true});

    //pass the user's email address to modal to confirm which account has expired
    modalRef.componentInstance.googleAccount = this.googleAccount;

    //wait for response from modal
    //if user confirmed, trigger sign in request
    modalRef.result.then((response) => {
      this.clearGoogle();
      if (response === CONFIRMED_EVENT) {
        this.gauth(false);
      }
    });
  }

  clearGoogle() {
    //set gauthed status to false to return button to sign in
    this.gauthed = false;
    //set gcalLoaded status to false to hide Google event toggle
    this.gcalLoaded = false;
    //remove stored email address
    this.googleAccount = undefined;
    //remove the Google events from the calendar
    this.removeEvents(false, false, true);
    //clear memory of Google events
    this.googleEvents = [];
  }

  handleDateClick = (info : any) => {
      this.calendar.changeView('dayGridDay', info.dateStr.substring(0, 10));
  }

  // Asks the user for a date to navigate directly to
  navigateToDate = () => {
    const modalRef = this.modalService.open(DateNavDialogComponent, {size: 'sm', backdrop: true, keyboard: true});

    modalRef.result.then((response: {status : string, date : string, switchView : boolean}) => {
      if(response) {
        if(response.status == CONFIRMED_EVENT) {
          //the switchView parameter changes the view to only the specified date
          if(response.switchView) {
            this.calendar.changeView('dayGridDay', response.date);
          } else {
            //if the user doesn't want to switch view, they will be navigated to the date, but remain in their current view
            this.calendar.gotoDate(response.date);
          }
        }
      }


    });
  }

  createNewEvent() {
    const modalRef = this.modalService.open(CalendarEventCreateDialogComponent, {size: 'lg', backdrop: 'static', keyboard: true});

    modalRef.result.then((response) => {
      if(response === ITEM_SAVED_EVENT) {
        //if the event has been created, reload the page
        this.load();
      }
      //else, do nothing to improve performance
    });
  }

//TODO LIST
//TODO: Visual enhancements of Calendar page
//TODO: Accessibility of Calendar page

}
