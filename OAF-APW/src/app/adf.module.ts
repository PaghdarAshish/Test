/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

import { NgModule } from '@angular/core';

import { ProcessModule } from '@alfresco/adf-process-services';
import { ContentModule } from '@alfresco/adf-content-services';
import { CoreModule } from '@alfresco/adf-core';
import { InsightsModule } from '@alfresco/adf-insights';

export function modules() {
    return [
        CoreModule,
        ProcessModule,
        ContentModule,
        InsightsModule
    ];
}

@NgModule({
    imports: modules(),
    exports: modules()
})
export class AdfModule {
}
