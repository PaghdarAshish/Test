/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {TaskFilterService, FilterRepresentationModel } from '@alfresco/adf-process-services';
import { AppDefinitionRepresentationModel } from '@alfresco/adf-process-services';
import { Subscription } from 'rxjs';


@Component({
    selector: 'apw-apps',
    templateUrl: 'apps.component.html',
    styleUrls: ['./apps.component.scss'],
    providers: []
})

export class AppsComponent implements OnInit {

    landingRoutePage: string;
    sub: Subscription;
    constructor( public router: Router, private taskFilterService: TaskFilterService) {
    }

    ngOnInit() {
        //this.landingRoutePage = this.appConfig.get('landing-page', 'dashboard/default');
    }

    onAppSelection(app: AppDefinitionRepresentationModel) {
        const appId = app.id ? app.id : 0;                  
        this.taskFilterService.getTaskFilterByName('My Tasks',appId).subscribe(
            (res: FilterRepresentationModel) => {                
                if(res !== undefined){
                    this.landingRoutePage = 'tasks/'+res.id.toString(); 
                    this.router.navigate([`apps/${appId}/${this.landingRoutePage}`]);                               
                }
                else{
                    this.taskFilterService.createDefaultFilters(appId).subscribe((res2: FilterRepresentationModel[]) => {
                        this.taskFilterService.getTaskFilterByName('My Tasks',appId).subscribe(
                            (res3: FilterRepresentationModel) => {
                                this.landingRoutePage = 'tasks/'+res3.id.toString();                
                                this.router.navigate([`apps/${appId}/${this.landingRoutePage}`]);                
                            });
                    });                    
                }                                
            });
        
    }

}
