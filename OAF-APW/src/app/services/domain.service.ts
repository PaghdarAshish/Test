/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
import { Injectable } from '@angular/core';
import { AppConfigService } from '@alfresco/adf-core';

@Injectable()
export class DomainService {
    constructor(private appConfig: AppConfigService) {
    }

    retailHFDomain: String = this.appConfig.get('bpmHost');
    apsDomain: String = this.appConfig.get('bpmHost');
}
