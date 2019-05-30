import { OnDestroy } from '@angular/core';
import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { WidgetComponent } from '@alfresco/adf-core';
import { OfaApwConstantsService } from '../../services/oaf-apw-constants.service';
 
@Component({
    selector: 'apw-input-comments',
    templateUrl: './input-comments.component.html',
    styleUrls: ['./input-comments.component.scss']
})
export class InputCommentsComponent extends WidgetComponent implements OnInit, OnDestroy {
 
    newComment = '';
    isAddComment: any = false;
    currentUserName: String = '';
    isAddCommentsButtonDisable: Boolean = false;
    isDisableAllFields  : Boolean = false;

    constructor(private ofaApwConstantsService :OfaApwConstantsService) {
        super();
    }

    ngOnInit(): void {
        this.field.value = this.field.value ? String(this.field.value).replace(/\n/g, '<br>') : '';
        this.field.value = this.field.value ? String(this.field.value).replace(/ /g, '&nbsp;') : ''; 
        if(this.ofaApwConstantsService.isCompletetask)
        {
            this.newComment="";
            this.isAddComment=false;
            this.isDisableAllFields=true;
            this.isAddComment = true;
            let comment = this.field.value ? this.field.value.replace(/&nbsp;/g," ") : '';
            comment = comment ? comment.replace(/<br>/g,"\r\n") : '';
            this.newComment=comment;
        }
        else
        {
            let comment = this.field.value ? this.field.value.replace(/&nbsp;/g," ") : '';
            comment = comment ? comment.replace(/<br>/g,"\r\n") : '';
            this.newComment=comment;
            this.isAddComment=true;
            this.isDisableAllFields=false;
        }
        
    }

    ngOnDestroy(): void {

    }

    addCustomComment(): void {
        this.isAddComment = true;
    }

    cancelCustomComment(): void {
        this.isAddComment = false;
        this.newComment = '';
    }

    onSearchChange(searchValue: string) {
        this.ofaApwConstantsService.addGeneralComments = searchValue;
        searchValue = searchValue ? String(searchValue).replace(/ /g, '&nbsp;') : '';
        searchValue = searchValue ? String(searchValue).replace(/\n/g, '<br>') : '';
        this.field.value =searchValue;
    }
}
