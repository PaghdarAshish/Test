import { Injectable } from '@angular/core';

@Injectable()
export class OfaApwConstantsService {
    addGeneralComments : string = "";
    addGeneralSalesComments : string = "";
    newComment : string = "";
    newSalesComments:string = "";
    isCompletetask: boolean = false;
    oafForm:any={};
    sendemailnotificationfor:string ="";
}