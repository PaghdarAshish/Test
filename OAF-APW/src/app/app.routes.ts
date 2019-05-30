/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuardBpm } from '@alfresco/adf-core';

import {
    AppsContainerComponent,
    AppContainerComponent,
    AppsComponent,
    CreateProcessComponent,
    CreateTaskComponent,
    //DashboardComponent,
    LoginComponent,
    ProcessDetailsContainerComponent,
    ProcessListContainerComponent,
    TaskDetailsContainerComponent,
    TaskListContainerComponent,
    SettingComponent,
    ProvidersComponent,
    EmailApprovalComponent,
    OafDocumentDownloaderComponent,
    OAFReportComponent
} from './components';
import { BlobPreviewComponent } from './components/blob-preview/blob-preview.component';

export const appRoutes: Routes = [
    { path: '',   redirectTo: '/apps', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'settings', component: SettingComponent },
    { path: 'providers', component: ProvidersComponent },
    {
        path: 'apps',
        component: AppsContainerComponent,
        canActivate: [ AuthGuardBpm ],
        children: [ { path: '', component: AppsComponent } ]
    },
    {
        path: 'email-approval/:userId/:taskId/:action',
        component: EmailApprovalComponent,
    },
    {
        path: 'email-approval/:taskId/:action',
        component: EmailApprovalComponent
    },
    {
        path: 'document-downloader/:nodeRef/:documentName',
        component: OafDocumentDownloaderComponent
    },
    {
        path: 'apps/:appId',
        component: AppContainerComponent,
        canActivate: [ AuthGuardBpm ],
        children: [
            { path: 'tasks', component: TaskListContainerComponent },
            { path: 'tasks/new', component: CreateTaskComponent },
            { path: 'tasks/:taskFilterId', component: TaskListContainerComponent },
            { path: 'processes', component: ProcessListContainerComponent },
            { path: 'processes/new', component: CreateProcessComponent },
            { path: 'processes/:processFilterId', component: ProcessListContainerComponent },
            { path: 'report/default', component: OAFReportComponent }
            // { path: 'dashboard/default', component: DashboardComponent }
        ]
    },
    { path: 'processdetails/:appId/:processInstanceId', component: ProcessDetailsContainerComponent, canActivate: [AuthGuardBpm] },
    {
        path: 'taskdetails/:appId/:taskId',
        component: TaskDetailsContainerComponent,
        canActivate: [AuthGuardBpm],
    },
    {
        path: 'preview/blob',
        component: BlobPreviewComponent,
        outlet: 'overlay',
    }
];

export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes, { useHash: true });
