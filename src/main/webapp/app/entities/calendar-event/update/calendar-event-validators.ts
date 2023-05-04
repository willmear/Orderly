import {AbstractControl} from "@angular/forms";

export function calendarDateValidator(control: AbstractControl) : {[key: string]: any} | null {
  //get the values of the start and end fields
  let start = control.root.get('start')?.value;
  let end = control.root.get('end')?.value;

  if(start && end) {
    //only check if both values are present
    let startDateTime = new Date(start);
    let endDateTime = new Date(end);


    if(startDateTime.getTime() >= endDateTime.getTime()) {
      //start date is after end date, so mark as invalid
      return {'dateInvalid': true};
    }
  }
  //else, validator success
  return null;
}
