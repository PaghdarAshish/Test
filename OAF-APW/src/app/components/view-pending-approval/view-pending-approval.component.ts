import { Component, OnInit } from '@angular/core';
import { WidgetComponent } from '@alfresco/adf-core';



@Component({
  selector: 'apw-view-pending-approval',
  templateUrl: './view-pending-approval.component.html',
  styleUrls: ['./view-pending-approval.component.scss']
})
export class ViewPendingApprovalComponent extends WidgetComponent implements OnInit {

  data: any;
  pendingApprovalsDataSource: any= [];
  //previewList:any=[];
  constructor() {
    super();
  }

  ngOnInit(): void {
    if(this.field.value) {      
      let listOfData = JSON.parse(this.field.value);
      for(let i = 0; i < listOfData.length; i ++) {
        this.pendingApprovalsDataSource.push(listOfData[i]);
      }
    }
  }
}
