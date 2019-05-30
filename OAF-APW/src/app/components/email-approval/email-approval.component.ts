import { Component, OnInit, ViewEncapsulation, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { TaskDetailsModel } from '@alfresco/adf-process-services';
import { FormModel, FormOutcomeModel } from '@alfresco/adf-core';
import { Router, ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import * as Rx from "rxjs";
import { filter } from 'rxjs/operators';
import { ToastrManager } from 'ng6-toastr-notifications';
//import moment from 'moment-es6';
import { OfaApwApiService } from '../../services/oaf-apw-api.service';

@Component({
  selector: 'apa-email-approval',
  templateUrl: './email-approval.component.html',
  styleUrls: ['./email-approval.component.scss'],
  providers: [OfaApwApiService],
  encapsulation: ViewEncapsulation.None
})
export class EmailApprovalComponent implements OnInit, OnDestroy {

  taskId = new Rx.BehaviorSubject<string>('');
  action: string;
  comment: string = '';
  //userRole: string;
  loading = true;
  taskDetails: TaskDetailsModel = new TaskDetailsModel();
  sub: Subscription;
  routerSub: Subscription;
  message: string = '';
  messageSales: string = '';
  formattedMessage: string = '';
  formattedSalesMessage: string = '';
  approvalMessage: string = 'This task has been completed';
  form: FormModel;
  currentUserId: number;
  test: any;
  butCondition: string;

  constructor(private route: ActivatedRoute,
    private oafApwApiService: OfaApwApiService,
    private router: Router, private toaster: ToastrManager) { }

  ngOnInit() {
    this.loading = false;
    //this.getTaskId();
    //this.getTaskDetails();
    this.routerSub = this.route.params.subscribe((param) => {
      this.butCondition = param['action']
      this.action = this.butCondition;
      // this.userRole=param['userRole'];   
      if (this.action !== 'updatesap') {
        this.currentUserId = parseInt(param['userId'], 10);
        this.oafApwApiService.userId = this.currentUserId.toString();
      }
      this.taskId.next(param['taskId']);
    });

    if (this.action !== 'updatesap') {
      this.taskId.pipe(
        filter(id => !!id),
        switchMap((taskId) => this.oafApwApiService.taskStatus(taskId)))
        .subscribe((taskDetail: TaskDetailsModel) => {
          this.taskDetails = taskDetail;
          if (this.taskDetails.endDate) {
            this.approvalMessage = 'Sorry, task has already been completed';
            this.loading = false;
          }
        }, () => {
          this.loading = false;
          this.approvalMessage = 'Sorry, something went wrong';
          this.taskDetails.endDate = new Date();
        });
    } else if (this.action === 'updatesap') {
      this.loading = true;
      this.taskId.pipe(
        filter(id => !!id),
        switchMap((taskId) => this.oafApwApiService.taskStatus(taskId)))
        .subscribe((taskDetail: TaskDetailsModel) => {
          this.taskDetails = taskDetail;
          if (this.taskDetails.endDate) {
            this.approvalMessage = 'Sorry, task has already been completed';
            this.loading = false;
          } else {
            this.loading = true;
            this.oafApwApiService.completeSAPTaskForm(this.taskDetails.id)
              .subscribe(() => {
                this.loading = false;
                this.taskDetails.endDate = new Date();
                this.toaster.successToastr(`Request has been successfully sent.`, 'Success',
                  { animate: "slideFromTop", showCloseButton: true });
                this.approvalMessage = (`Request has been successfully sent.`);
              }, () => {
                this.loading = false;
                this.approvalMessage = 'Sorry, something went wrong';
                this.taskDetails.endDate = new Date();
                this.toaster.errorToastr('Something went wrong', 'Error', { animate: "slideFromTop", showCloseButton: true });
              });
          }
        });
    }
  }


  private getTaskDetails() {
    this.taskId.pipe(
      filter(id => !!id),
      switchMap((taskId) => this.oafApwApiService.taskStatus(taskId)))
      .subscribe((taskDetail: TaskDetailsModel) => {
        this.taskDetails = taskDetail;
        if (!this.taskDetails.endDate) {
          if (this.action.toLowerCase() === 'approve') {
            //  TOdo: complete form with approve outcome
            this.completeTask('Approve')
          } else if (this.action.toLowerCase() === 'reject') {
            //  TOdo: complete form with reject outcome
            this.completeTask('Reject');
          } else if (this.action === 'view') {
            this.loading = false;
            this.router.navigateByUrl(`apps/0/tasks/${taskDetail.endDate ? 'completed-task' : 'pending-task'}/${taskDetail.processInstanceId}/${taskDetail.id}`);
          } else {
            this.loading = false;
          }
        } else {
          this.approvalMessage = 'Sorry, task has already been completed';
          this.loading = false;
        }
      }, () => {
        this.loading = false;
        this.approvalMessage = 'Sorry, something went wrong';
        this.taskDetails.endDate = new Date();
      });
  }

  completeTask(outCome: string) {
    this.loading = true;
    this.oafApwApiService.getTaskForm(this.taskDetails.id).then(taskform => {
      this.form = this.parseForm(taskform);

      if (this.userAllowedOrNot()) {
        this.addComment(outCome);
      } else {
        this.loading = false;
        this.taskDetails.endDate = new Date();
        this.toaster.errorToastr('This user is not having sufficient permission to complete the task', 'Error', { animate: "slideFromTop", showCloseButton: true });
        this.approvalMessage = 'This user is not having sufficient permission to complete the task';
      }
    }, () => {
      this.loading = false;
      this.approvalMessage = 'Sorry, something went wrong';
      this.taskDetails.endDate = new Date();
      this.toaster.errorToastr('Something went wrong', 'Error', { animate: "slideFromTop", showCloseButton: true });
    });
  }

  private userAllowedOrNot(): boolean {
    const approverid = this.taskDetails.assignee.id;

    if (this.currentUserId === +approverid) {
      return true;
    }
    else {
      return false;
    }

  }

  redirectToHome() {
    this.router.navigateByUrl('');
    //location.href="../oaf-workflows/#/apps";
  }
  addComment(outCome): void {
    let commentExVarName = null;
    let commentSalesVarName = null;
    commentExVarName = "addComments";
    commentSalesVarName = "addSalesComments";

    if (commentExVarName !== null) {
      let commentExJson = `[{"name":"${commentExVarName}","scope":"global","type":"string","value":"${this.formattedMessage}"},{"name":"${commentSalesVarName}","scope":"global","type":"string","value":"${this.formattedSalesMessage}"}]`;
      this.oafApwApiService.addProcessVariable(this.taskDetails.processInstanceId, JSON.parse(commentExJson)).subscribe(
        () => {
          if (this.message.trim() !== "") {
            //this.toaster.successToastr(`Comment successfully added`,'Success', {animate: "slideFromTop" ,showCloseButton: true });
          }
          if (!this.taskDetails.endDate) {
            this.oafApwApiService.completeTaskForm(this.taskDetails.id, this.form.values, outCome)
              .subscribe(() => {
                this.loading = false;
                this.taskDetails.endDate = new Date();
                this.toaster.successToastr(`Task has been ${outCome.toLowerCase() === 'approve' ? 'approved' : 'rejected'}`, 'Success', { animate: "slideFromTop", showCloseButton: true });
                this.approvalMessage = (`Task has been ${outCome.toLowerCase() === 'approve' ? 'approved' : 'rejected'}`);
              }, () => {
                this.loading = false;
                this.approvalMessage = 'Sorry, something went wrong';
                this.taskDetails.endDate = new Date();
                this.toaster.errorToastr('Something went wrong', 'Error', { animate: "slideFromTop", showCloseButton: true });
              });
          }
          else {
            this.loading = false;
            this.approvalMessage = 'Sorry, something went wrong';
            this.taskDetails.endDate = new Date();
            this.toaster.errorToastr('Something went wrong', 'Error', { animate: "slideFromTop", showCloseButton: true });
          }

        },
        () => {
          this.loading = false;
          this.approvalMessage = 'Sorry, something went wrong';
          this.taskDetails.endDate = new Date();
        }
      );


    }
    else {
      this.loading = false;
      this.toaster.errorToastr("No, user role found", 'Error', { animate: "slideFromTop", showCloseButton: true });
      this.approvalMessage = 'Sorry, no user role found';
      this.taskDetails.endDate = new Date();

    }



  }

  submitClicked() {
    if (this.action === 'reject' && this.message.trim() == "") {
      this.toaster.errorToastr("Comment is required in the case of rejection", 'Error', { animate: "slideFromTop", showCloseButton: true });
      (document.getElementById("message").focus());
    }
    else {
      this.getTaskDetails();
    }

  }

  parseForm(json: any): FormModel {
    if (json) {
      const form = new FormModel(json, [], true);
      if (!json.fields) {
        form.outcomes = this.getFormDefinitionOutcomes(form);
      }
      return form;
    }
    return null;
  }

  getFormDefinitionOutcomes(form: FormModel): FormOutcomeModel[] {
    return [
      new FormOutcomeModel(form, { id: '$custom', name: FormOutcomeModel.SAVE_ACTION, isSystem: true })
    ];
  }

  onSearchChange(searchValue: string) {
    searchValue = searchValue ? String(searchValue).replace(/ /g, '&nbsp;') : '';
    searchValue = searchValue ? String(searchValue).replace(/\n/g, '<br>') : '';
    searchValue = searchValue ? String(searchValue).replace(/\\/g, '%5%') : '';
    searchValue = searchValue ? String(searchValue).replace(/"/g, '%2%') : '';
    this.formattedMessage = searchValue;
  }
  onSearchChange2(searchValue: string) {
    searchValue = searchValue ? String(searchValue).replace(/ /g, '&nbsp;') : '';
    searchValue = searchValue ? String(searchValue).replace(/\n/g, '<br>') : '';
    searchValue = searchValue ? String(searchValue).replace(/\\/g, '%5%') : '';
    searchValue = searchValue ? String(searchValue).replace(/"/g, '%2%') : '';
    this.formattedSalesMessage = searchValue;
  }
  ngOnDestroy(): void {
    if (this.sub) {
      this.sub.unsubscribe();
    }
    if (this.routerSub) {
      this.routerSub.unsubscribe();
    }
  }

}
