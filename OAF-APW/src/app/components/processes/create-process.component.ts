/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

import { Component, Input, OnInit, OnDestroy, ViewEncapsulation ,AfterViewChecked ,ElementRef ,ViewChild } from '@angular/core';
import { ProcessFilterService, FilterProcessRepresentationModel } from '@alfresco/adf-process-services';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AppConfigService, FormService, FormFieldEvent, FormRenderingService } from '@alfresco/adf-core';
import { InputCommentsComponent } from '../input-comments/input-comments.component';
import { ViewCommentsComponent } from '../view-comments/view-comments.component';
import { InputSalesCommentsComponent } from '../input-sales-comments/input-sales-comments.component';
// import { InputCommentsComponent } from '../input-comments/input-comments.component';

@Component({
    selector: 'apw-create-process',
    templateUrl: './create-process.component.html',
    styleUrls: ['./create-process.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class CreateProcessComponent implements OnInit, OnDestroy , AfterViewChecked {

    @Input()
    appId: string = null;

    sub: Subscription;
    defaultFilterId = '';
    counterForDom : number;

    defaultProcessDefinitionName: string;
    defaultProcessName: string;
    constructor(private route: ActivatedRoute,
        private router: Router,
        private appConfig: AppConfigService,
        formService: FormService,
        private processFilterService: ProcessFilterService,
        private element : ElementRef,
        private formRenderingService: FormRenderingService) {
            this.counterForDom = 0;
         
            formService.formFieldValueChanged.subscribe(
                (e: FormFieldEvent) => {
                  console.log(`Field value changed. Form: ${e.form.id}, Field: ${e.field.id}, Value: ${e.field.value}`);
                  console.log(e.field.value);
                  /*if (`${e.field.id}` === 'tc1form') {
                  }*/
                  });
          

        // custom_button
         this.formRenderingService.setComponentTypeResolver('oaf_add_comments', () => InputCommentsComponent, true);
         this.formRenderingService.setComponentTypeResolver('oaf_add_sales_comments', () => InputSalesCommentsComponent, true);
         this.formRenderingService.setComponentTypeResolver('oaf_view_comments', () => ViewCommentsComponent, true);
        // dynamic_article_details_form
    

    }

    @ViewChild('mat-card-actions') d1 :ElementRef;

    ngOnInit() {

        this.defaultProcessName = this.appConfig.get<string>('adf-start-process.name');
        this.defaultProcessDefinitionName = this.appConfig.get<string>('adf-start-process.processDefinitionName');

        this.sub = this.route.parent.params.subscribe(params => {
            this.appId = params['appId'];
            this.getDefaultProcessFilter(this.getAppId());
        });
    }

    ngOnDestroy() {
    }

     ngAfterViewChecked() 
     {
        if(this.counterForDom == 0) {
            if(document.readyState == "complete") {
                debugger;
                let  matCardActions =  this.element.nativeElement.querySelector('.adf-start-form-actions');
                if(matCardActions) {
                    debugger;
                    this.counterForDom  = 1;
                    matCardActions.insertAdjacentHTML('beforeend', '<button id="btn" (click)="save()" class="test">Save Process</button>');
                }
            }

            
        }
    }
    
    backFromProcessCreation(): void {
        this.router.navigateByUrl('apps/' + this.appId + '/processes/' + this.defaultFilterId);
    }

    getDefaultProcessFilter(appId: string): void {
        this.processFilterService.getProcessFilterByName('Running', +appId).subscribe(
            (res: FilterProcessRepresentationModel) => {
                this.defaultFilterId = res.id.toString();
            }
        );
    }

    getAppId(): string {
        return +this.appId === 0 ? null : this.appId;
    }
}
