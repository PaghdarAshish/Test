import { OnDestroy } from '@angular/core';
import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { WidgetComponent } from '@alfresco/adf-core';
import { OfaApwConstantsService } from '../../services/oaf-apw-constants.service';

@Component({
  selector: 'apa-input-sales-comments',
  templateUrl: './input-sales-comments.component.html',
  styleUrls: ['./input-sales-comments.component.scss']
})
export class InputSalesCommentsComponent extends WidgetComponent implements OnInit, OnDestroy {

  newSalesComments = '';
  isAddSalesComment: any = false;
  currentUserName: String = '';  
  isDisableAllFields  : Boolean = false;

  constructor(private ofaApwConstantsService :OfaApwConstantsService) {
      super();
  }

  ngOnInit(): void {
      this.field.value = this.field.value ? String(this.field.value).replace(/\n/g, '<br>') : '';
      this.field.value = this.field.value ? String(this.field.value).replace(/ /g, '&nbsp;') : ''; 
      if(this.ofaApwConstantsService.isCompletetask)
      {
          this.newSalesComments="";
          this.isAddSalesComment=false;
          this.isDisableAllFields=true;
          this.isAddSalesComment = true;
          let comment = this.field.value ? this.field.value.replace(/&nbsp;/g," ") : '';
          comment = comment ? comment.replace(/<br>/g,"\r\n") : '';
          this.newSalesComments=comment;
      }
      else
      {
          let comment = this.field.value ? this.field.value.replace(/&nbsp;/g," ") : '';
          comment = comment ? comment.replace(/<br>/g,"\r\n") : '';
          this.newSalesComments=comment;
          this.isAddSalesComment=true;
          this.isDisableAllFields=false;
      }
      
  }

  ngOnDestroy(): void {

  }

  addCustomSalesComment(): void {
      this.isAddSalesComment = true;
  }

  cancelCustomSalesComment(): void {
      this.isAddSalesComment = false;
      this.newSalesComments = '';
  }

  onSearchChange(searchValue: string) {
      this.ofaApwConstantsService.addGeneralSalesComments = searchValue;
      searchValue = searchValue ? String(searchValue).replace(/ /g, '&nbsp;') : '';
      searchValue = searchValue ? String(searchValue).replace(/\n/g, '<br>') : '';
      this.field.value =searchValue;
  }

}
