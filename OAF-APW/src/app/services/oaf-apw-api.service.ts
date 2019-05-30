import { Injectable } from "@angular/core";
import { Http , Headers } from "@angular/http";
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';
import { DomainService } from './domain.service';
import { AuthenticationService} from '@alfresco/adf-core';
import { CustomCoreService } from "./custom-core.service";
 
@Injectable()
export class OfaApwApiService {
  userId : string; 
  private headers = new Headers();
  constructor(private http: Http,private domain : DomainService,
  private auth : AuthenticationService, private coreService: CustomCoreService) {
 
  }
  
  public approveRejectDetail(taskDetail,taskId){
    let url = this.domain.apsDomain + "/activiti-app/api/enterprise/task-forms/"+taskId;
    this.headers = new Headers();
    this.headers.append('Content-Type','application/json;charset=UTF-8');
    this.headers.append('Authorization', this.auth.getTicketBpm());
    return this.http.post(url, taskDetail, { headers: this.headers }).toPromise().then(res => res);
  }

  public saveDetail(taskDetail,taskId){
    let url = this.domain.apsDomain + "/activiti-app/api/enterprise/task-forms/"+taskId+"/save-form";
    this.headers = new Headers();
    this.headers.append('Content-Type','application/json;charset=UTF-8');
    this.headers.append('Authorization', this.auth.getTicketBpm());
    return this.http.post(url, taskDetail, { headers: this.headers }).toPromise().then(res => res);  
  }

  // For Email Approval Services start
  public taskStatus(taskId){
    const url = this.domain.apsDomain +`/activiti-app/api/enterprise/tasks/${taskId}`;
    this.coreService.userId = this.userId;
    return this.coreService.httpGet(url, this.coreService.privateHeaders(),false);
  }

  
  public getTaskForm(taskId) {    
    const url = this.domain.apsDomain + `/activiti-app/api/enterprise/task-forms/${taskId}`;
    this.coreService.userId = this.userId;
    return this.coreService.httpAMGet(url, this.coreService.privateHeaders(), false);
  } 

  public addTaskComment( taskId, processInstanceId, message: string ){
    const url = this.domain.apsDomain + `/activiti-app/api/enterprise/tasks/${taskId}/comments`;
    this.coreService.userId = this.userId; 
    return this.coreService.httpAMPost(url, { message: message}, this.coreService.privateHeaders(), false);
  }
  
  public addProcessVariable(processInstanceId, valueJson ){    
    const url = this.domain.apsDomain + `/activiti-app/api/enterprise/process-instances/${processInstanceId}/variables`;
    this.coreService.userId = this.userId;
    return this.coreService.httpAMPut(url, valueJson, this.coreService.privateHeaders(), false);

  }

  public  downloadDocument(nodeRef,documentName) : any{
    const url=this.domain.apsDomain+`/activiti-app/api/enterprise/oaf-download-document?nodeRef=${nodeRef}&document=${documentName}`;
    return this.coreService.imageGet(url,documentName);
  } 

  public completeTaskForm(taskId, data, outCome) {
    const formRequest = { 'outcome': outCome, 'values': data };
    const url = this.domain.apsDomain +`/activiti-app/api/enterprise/task-forms/${taskId}`;
    this.coreService.userId = this.userId;    
    return this.coreService.httpAMPost(url,formRequest,this.coreService.privateHeaders(),false);    
  }

  public completeSAPTaskForm(taskId) {
    const url = this.domain.apsDomain +`/activiti-app/api/enterprise/tasks/${taskId}/action/complete`;
    return this.coreService.httpAMPut(url,undefined,this.coreService.privateHeaders(), false);    
  }

  public getOAFReportDetail(reportDetail : any) {
    //const url = this.domain.apsDomain +"/oaf-adapter/getOAFReportDetail";
    const url = this.domain.apsDomain + "/oaf-adapter/getOAFReportDetail";
    this.headers.append('Content-Type','application/json;charset=UTF-8');
    this.headers.append('Authorization', this.auth.getTicketBpm());
    return this.http.post(url, reportDetail, { headers: this.headers }).toPromise().then(res => res.json());
  }
  // For Email Approval Services End

  public getProcessInstanceVariables(processInstanceId :any) {
    const url = this.domain.apsDomain + "/activiti-app/api/enterprise/process-instances/"+processInstanceId+"/historic-variables";
    this.headers.append('Content-Type','application/json;charset=UTF-8');
    this.headers.append('Authorization', this.auth.getTicketBpm());
    return this.http.get(url,{ headers: this.headers }).toPromise().then(res => res.json());
  }

}