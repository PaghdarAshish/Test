/*
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

// import { ViewCommentsComponent } from './../view-comments/view-comments.component';
import {
    Component,
    Input,
    OnInit,
    OnDestroy,
    AfterViewChecked,
    Output,
    EventEmitter,
    TemplateRef,
    ViewEncapsulation,
    ElementRef
} from '@angular/core';
import {
    FormOutcomeEvent,
    FormModel,
    BpmUserService,
    ContentLinkModel,
    FormRenderingService,
    NotificationService,
    TranslationService,
    LogService
} from '@alfresco/adf-core';
import {
    TaskDetailsModel,
    AttachFileWidgetComponent,
    AttachFolderWidgetComponent,
    TaskListService
} from '@alfresco/adf-process-services';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { FormService, FormFieldEvent } from '@alfresco/adf-core';
import { MatDialog } from '@angular/material';
import { TaskFormDialogComponent } from './task-form-dialog/task-form-dialog.component';
import { InputCommentsComponent } from './../input-comments/input-comments.component';
import { InputSalesCommentsComponent } from './../input-sales-comments/input-sales-comments.component';
import { ViewCommentsComponent } from '../view-comments/view-comments.component';
import { OAFFormComponent } from '../oaf-form/oaf-form.component';
import { OfaApwApiService } from '../../services/oaf-apw-api.service';
import { OfaApwConstantsService  } from '../../services/oaf-apw-constants.service';
import { LoaderService } from '../../services/loader.service';
import { OAfDesignationComponent } from '../oaf-designation/oaf-designation.component';
import { FilterRepresentationModel,TaskFilterService } from '@alfresco/adf-process-services';
import { ToastrManager } from 'ng6-toastr-notifications';
import { ViewPendingApprovalComponent } from '../view-pending-approval/view-pending-approval.component';

// import { DynamicArticleViewComponent } from '../dynamic-article-view/dynamic-article-view.component';

@Component({
    selector: 'apw-task-form',
    templateUrl: './task-form.component.html',
    styleUrls: ['./task-form.component.scss'],
    encapsulation: ViewEncapsulation.None
})

export class TaskFromComponent implements OnInit, OnDestroy,AfterViewChecked {

    @Input()
    taskDetails: TaskDetailsModel;

    @Input()
    readOnlyForm = false;

    @Output()
    navigate: EventEmitter<any> = new EventEmitter<any>();


    @Output()
    cancel: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    complete: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    executeNoFormOutcome: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    taskFormName: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    contentClicked: EventEmitter<ContentLinkModel> = new EventEmitter<ContentLinkModel>();

    @Output()
    formOutcomeExecute: EventEmitter<FormOutcomeEvent> = new EventEmitter<FormOutcomeEvent>();

    @Output()
    formChange: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    formAttached: EventEmitter<any> = new EventEmitter<any>();

    appId;
    taskId: string;

    showInfoDrawer: boolean;
    attachmentDetails: any = {};

    taskFormValues: string;

    noTaskDetailsTemplateComponent: TemplateRef<any>;

    sub: Subscription;
    private currentUserId: number;
    counterForApprovalbtn:number=0;
    taskFormObj:any = {};
    customTaskFilter:any="";
    constructor(
        private taskListService: TaskListService,
        private logService: LogService,
        private route: ActivatedRoute,
        private bpmUserService: BpmUserService,
        private formRenderingService: FormRenderingService,
        private formService: FormService,
        public dialog: MatDialog,
        private notificationService: NotificationService,
        private translateService: TranslationService,
        private element:ElementRef,
        private ofaApwApiService :OfaApwApiService,
        private ofaApwConstantsService:OfaApwConstantsService,
        public loader:LoaderService,
        private router : Router,
        private taskFilterService : TaskFilterService,
        private toaster : ToastrManager) {
            formService.formFieldValueChanged.subscribe(
                (e: FormFieldEvent) => {
                    if (e.field.id == "viewComments")
                    {
                       this.taskFormObj.viewComments = e.field.value;
                    }
            });

        // console.error('Im in task form constructor');
        this.formRenderingService.setComponentTypeResolver('sendemailnotificationfor', () => OAfDesignationComponent, true);
        this.formRenderingService.setComponentTypeResolver('oaf_add_comments', () => InputCommentsComponent, true);
        this.formRenderingService.setComponentTypeResolver('oaf_add_sales_comments', () => InputSalesCommentsComponent, true);
         this.formRenderingService.setComponentTypeResolver('oaf_view_comments', () => ViewCommentsComponent, true);
         this.formRenderingService.setComponentTypeResolver('oaf_view_pending_approvals', () => ViewPendingApprovalComponent, true);
         this.formRenderingService.setComponentTypeResolver('oaf_form_stencils', () => OAFFormComponent, true);
        this.formRenderingService.setComponentTypeResolver('custom_button', () => InputCommentsComponent, true);
        // this.formRenderingService.setComponentTypeResolver('readonly-text', () => ViewCommentsComponent, true);
        this.formRenderingService.setComponentTypeResolver('upload', () => AttachFileWidgetComponent, true);
        this.formRenderingService.setComponentTypeResolver('select-folder', () => AttachFolderWidgetComponent, true);
        // this.formRenderingService.setComponentTypeResolver('view_dynamic_article_form', () => DynamicArticleViewComponent, true);
        // this.formRenderingService.setComponentTypeResolver('readonly-text', () => DynamicArticleViewComponent, true);
    }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.appId = params['appId'];
            this.taskId = params['taskId'];
        });

        this.taskFilterService.getTaskFilterByName('My Tasks',+this.appId).subscribe(
            (res: FilterRepresentationModel) => {   
                this.customTaskFilter = res.id.toString();
        });

      this.loadCurrentUser();
    }

    ngAfterViewChecked() {
        this.ofaApwConstantsService.isCompletetask = this.hasCompleteButton();
        if(this.taskDetails) {
            //if(this.taskDetails.name.includes("Senior GM") || this.taskDetails.name.includes("General SM") ||
               //this.taskDetails.name.includes("CreditM") || this.taskDetails.name.includes("SeniorFM") || this.taskDetails.name.includes("FinanceD") ||
               //this.taskDetails.name.includes("ViceP") || this.taskDetails.name.includes("SeniorFA"))  {
            if(this.counterForApprovalbtn === 0 && !this.ofaApwConstantsService.isCompletetask) {
                    let  matCardActions =  this.element.nativeElement.querySelector('.adf-form-mat-card-actions');
                    if(matCardActions) {
                        this.counterForApprovalbtn = 1;
                        //matCardActions.insertAdjacentHTML('beforeend', '<button id="btnSave">SAVE</button>');
                        matCardActions.insertAdjacentHTML('beforeend', '<button id="btnApprove">APPROVE</button>');
                        matCardActions.insertAdjacentHTML('beforeend', '<button id="btnReject">REJECT</button>');
                      
                        if(document.getElementById("btnApprove") && document.getElementById("btnReject")) {
                               //document.getElementById("btnSave").addEventListener("click",(evt) => this.saveForm(this));
                            if(this.isAssignedToCurrentUser()) {
                                document.getElementById("btnApprove").addEventListener("click",(evt) => this.approveForm(this));
                                document.getElementById("btnReject").addEventListener("click",(evt) => this.rejectForm(this));
                            }
                            else
                            {
                                document.getElementById("btnApprove").style.display = "none";
                                document.getElementById("btnReject").style.display = "none";
                            }
                        }
                     }  
            }
            else
            {
                if(document.getElementById("adf-form-approve") && document.getElementById("adf-form-save") && document.getElementById("adf-form-reject")) {
                    this.counterForApprovalbtn == 1;
                    document.getElementById("adf-form-approve").style.display = "none";
                    document.getElementById("adf-form-save").style.display = "none";
                    document.getElementById("adf-form-reject").style.display = "none";
                }
            }
        }
    }



    saveForm(form:any) {
        this.loader.loader = true;
        let oafForm = null;
        if(this.ofaApwConstantsService.oafForm) {
            oafForm = JSON.stringify(this.ofaApwConstantsService.oafForm);
        }
        let saveDetailObject = {
            values :{
                addComments: form.ofaApwConstantsService.addGeneralComments,
                addSalesComments: form.ofaApwConstantsService.addGeneralSalesComments,
                oafForm: oafForm,
                viewComments:form.taskFormObj.viewComments
            }
        }
        this.ofaApwApiService.saveDetail(saveDetailObject,this.taskId).then((result)=> {
            if(result) 
            {
                this.loader.loader = false;
                this.clearOfaConstantService();
                this.router.navigateByUrl('apps/' + this.appId + '/tasks/'+this.customTaskFilter);
            } 
            }).catch((error)=> {
                console.log("Error is coming when calling alfresco api: "+ error);
                this.loader.loader = false;
        })
    }


    /*This function call when click on approve button *
     * 
     * @param form 
     */
    approveForm(form:any){
        this.loader.loader = true;
        let oafForm = null;
        if(this.ofaApwConstantsService.oafForm){
            oafForm = JSON.stringify(this.ofaApwConstantsService.oafForm);
        }
        console.log("hello aksjda "+form.ofaApwConstantsService.addGeneralSalesComments); 
        let approveObject = {
            outcome: "Approve",
            values :{
                addComments : form.ofaApwConstantsService.addGeneralComments,
                addSalesComments: form.ofaApwConstantsService.addGeneralSalesComments,
                oafForm: oafForm,
                viewComments:form.taskFormObj.viewComments
            }
        }  
        form.apiSubmit(approveObject);
    }


    /*This function call when click on reject button *
     * 
     * @param form 
     */
    rejectForm(form:any){
        this.loader.loader = true;
        if(form.ofaApwConstantsService.addGeneralComments) {
            let oafForm = null;
            if(this.ofaApwConstantsService.oafForm){
                oafForm = JSON.stringify(this.ofaApwConstantsService.oafForm);
            } 
            let rejectObject = {
                outcome: "Reject",
                values :{
                    addComments : form.ofaApwConstantsService.addGeneralComments,
                    addSalesComments: form.ofaApwConstantsService.addGeneralSalesComments,
                    oafForm: oafForm,
                    viewComments:form.taskFormObj.viewComments
                }
            }  
            form.apiSubmit(rejectObject);
        }
        else
        {
            this.toaster.errorToastr("* Please fill required details (comment)", 'Error', {animate: "slideFromTop" ,showCloseButton: true });
            document.getElementById("addComments").focus();
            this.loader.loader = false; 
        }
    }


     /*This function used for call api*
     * 
     * @param requestObject 
     */
    apiSubmit(requestObject) {
        this.ofaApwApiService.approveRejectDetail(requestObject,this.taskId).then((result)=> {
                    if(result) 
                    {
                        this.toaster.successToastr("Task completed successfully", 'Success ', {animate: "slideFromTop" , showCloseButton: true});
                        this.clearOfaConstantService();
                        this.loader.loader = false;
                        this.router.navigateByUrl('apps/' + this.appId + '/tasks/'+this.customTaskFilter);
                    } 
         }).catch((error)=> {
               console.log("Error is coming when calling alfresco api: "+ error);
               this.loader.loader = false;
         })
    }


    /*This function used for clear ofa constans services*
     * 
     */
    clearOfaConstantService(){
        this.ofaApwConstantsService.addGeneralComments = "";
        this.ofaApwConstantsService.addGeneralSalesComments = "";
        this.ofaApwConstantsService.oafForm = {};
    }
    

    hasFormKey() {
        return !!this.taskDetails.formKey;
    }

    isTaskLoaded(): boolean {
        return !!this.taskDetails;
    }

    onFormCompleted(form: FormModel) {
        const processInfo = {
            processInstanceId: this.taskDetails.processInstanceId,
            processDefinitionId: this.taskDetails.processDefinitionId
        };
        this.complete.emit(processInfo);
    }

    onFormLoaded(form: FormModel): void {
        this.taskFormValues = JSON.stringify(form.values);
        const formName = (form && form.name ? form.name : null);
        this.taskFormName.emit(formName);
        this.onFormChange();
    }

    onFormContentClick(content: ContentLinkModel): void {
        this.contentClicked.emit(content);
    }

    isCompletedTask(): boolean {
      
        if(this.taskDetails){
            return this.taskDetails && this.taskDetails.endDate !== undefined && this.taskDetails.endDate !== null;
        }
            
    }

    hasCompleteButton(): boolean {
        return this.isCompletedTask() && this.isAssignedToCurrentUser();
    }

    onCompleteTask(): void {
        this.taskListService.completeTask(this.taskDetails.id)
            .subscribe(
                (res) => {
                    this.navigate.emit();
                    this.executeNoFormOutcome.emit();
                },
                error => {
                    this.logService.error('Task form' + error);
                }
        );
    }

    private loadCurrentUser(): void {
        this.bpmUserService.getCurrentUserInfo().subscribe((res) => {
            this.currentUserId = res && res.id;
        });
    }

    isAssignedToCurrentUser(): boolean {
        if(this.taskDetails)
            return +this.currentUserId === (this.taskDetails.assignee && this.taskDetails.assignee.id);
    }

    canInitiatorComplete(): boolean {
        return this.taskDetails.initiatorCanCompleteTask;
    }

    isReadOnlyForm(): boolean {
        return this.readOnlyForm || !(this.isAssignedToCurrentUser() || this.canInitiatorComplete());
    }

    isProcessInitiator(): boolean {
        return this.currentUserId === +this.taskDetails.processInstanceStartUserId;
    }

    isSaveButtonVisible(): boolean {
        return this.isAssignedToCurrentUser() || (!this.canInitiatorComplete() && !this.isProcessInitiator());
    }

    getTaskName(): any {
        return { taskName: this.taskDetails.name };
    }

    onCancelButtonClick() {
        this.cancel.emit();
    }

    onFormOutcomeExecute(formOutcomeEvent: FormOutcomeEvent) {
        this.formOutcomeExecute.emit(formOutcomeEvent);
    }

    ngOnDestroy() {
        if (this.sub) {
            this.sub.unsubscribe();
        }
    }

    onFormChange() {
        this.formService.formFieldValueChanged.subscribe(
            (e: FormFieldEvent) => {
                const eventChanges = JSON.stringify(e.form.values);
                if (eventChanges !== this.taskFormValues) {
                    this.formChange.emit(e.form);
                }
            }
        );
    }

    onShowAttachForm() {
        const dialogRef = this.dialog.open(TaskFormDialogComponent, {
            data: {
                taskId: this.taskDetails.id,
                formKey: this.taskDetails.formKey
            },
            width: '80%',
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result && result.isYes()) {
                this.formAttached.emit();
                this.notify('DW-TASK-FORM.OPERATIONS.FORM-ATTACH');
            } else if (result && result.isNo()) {
                this.dialog.closeAll();
            } else if (result && result.isError()) {
                this.notify('DW-TASK-FORM.OPERATIONS.FORM-ERROR');
            }
        });
    }

    private notify(message: string): void {
        const translatedMessage = this.translateService.instant(message);
        this.notificationService.openSnackMessage(translatedMessage, 4000);
    }

    hasProcessDefinitionId() {
        return !!this.taskDetails.processDefinitionId;
    }
}
