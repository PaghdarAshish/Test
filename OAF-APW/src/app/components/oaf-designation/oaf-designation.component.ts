/*
* Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
*
* License rights for this program may be obtained from Alfresco Software, Ltd.
* pursuant to a written agreement and any use of this program without such an
* agreement is prohibited.
*/
import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { WidgetComponent } from '@alfresco/adf-core';
import { OfaApwConstantsService } from '../../services/oaf-apw-constants.service';

@Component({
    selector: 'apw-oaf-designation',
    templateUrl: './oaf-designation.component.html',
    styleUrls: ['./oaf-designation.component.scss']
})
export class OAfDesignationComponent extends WidgetComponent implements OnInit {


    constructor(private ofaApwConstantsService : OfaApwConstantsService) {
        super();
    }

    ngOnInit(): void {
		this.ofaApwConstantsService.sendemailnotificationfor = this.field.value;
    }

}
