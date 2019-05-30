/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
import { Component, OnInit } from '@angular/core';
import { WidgetComponent,  } from '@alfresco/adf-core';
import { OfaApwConstantsService } from '../../services/oaf-apw-constants.service';

export interface OAFComments{
  designation:string;
  name:string;
  date:string;
  comments:string;
  status:string;
}

@Component({
  selector: 'apw-view-comments',
  templateUrl: './view-comments.component.html',
  styleUrls: ['./view-comments.component.scss']
})
export class ViewCommentsComponent extends WidgetComponent implements OnInit {
  data: any;
  commentDataSource: OAFComments[] = [];
  //previewList:any=[];
  constructor(public ofaApwConstantsService :OfaApwConstantsService) {
    super();
  }

  ngOnInit(): void {
   
    
    if(this.field.value) {
      let listOfData = JSON.parse(this.field.value);
      for(let i=listOfData.length; i > 0 ; i--) {
        listOfData[i-1].comments  = listOfData[i-1].comments  ? String(listOfData[i-1].comments ).replace(/ /g, '&nbsp;') : '';
        listOfData[i-1].comments  = listOfData[i-1].comments  ? String(listOfData[i-1].comments ).replace(/\n/g, '<br>') : '';
        listOfData[i-1].comments = listOfData[i-1].comments ? String(listOfData[i-1].comments).replace(/%5%/g, '\\') : '';
        listOfData[i-1].comments = listOfData[i-1].comments ? String(listOfData[i-1].comments).replace(/%2%/g, '\"') : '';
        this.commentDataSource.push(listOfData[i-1]);
      }      
    }
  }
}
  
