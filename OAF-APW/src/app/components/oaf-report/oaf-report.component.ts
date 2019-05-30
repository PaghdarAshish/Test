import { OnInit ,Component ,ViewChild} from '@angular/core';
import { OfaApwApiService } from '../../services/oaf-apw-api.service';
import {FormControl,FormGroup } from '@angular/forms';
import { MatPaginator, MatTableDataSource} from '@angular/material';
import { LoaderService } from '../../services/loader.service';
import { ToastrManager } from 'ng6-toastr-notifications';


export interface OAFReportModel {
  company_code: string,
  inq :string,	 
  quote: string,
  creation_date:string,
  sold_to_party: string,
  tonnage: string,
  elapsed_day:string,
  sales_office:string,
  sales_engineer: string,
  dqnumber:string,
  approved_date:string,
  pending_with:string,
  last_approval_date:string,
  last_approval_user:string,
  rejection_date:string,
  rejection_reason:string,
  rejection_user:string,
} 

@Component({
  selector: 'apa-oaf-report',
  templateUrl: './oaf-report.component.html',
  styleUrls: ['./oaf-report.component.scss']
})

export class OAFReportComponent implements OnInit {
  approvalReportForm: FormGroup;
  isApprovalShowGrid:boolean = false;
  isApprovalShowGridMessage:string = "";
  finalObject:any = {};

  rejectionReportForm: FormGroup;
  isRejectionShowGrid:boolean = false;
  isRejectionShowGridMessage:string = "";


  pendingReportForm: FormGroup;
  isPendingShowGrid:boolean = false;
  isPendingGridMessage:string = "";
  

  displayedColumnsForApproval: string[] =  ['company_code','inq','quote','creation_date','sold_to_party', 'tonnage', 'elapsed_day','sales_office','sales_engineer','dqnumber','approved_date'];
  displayedColumnsForRejection: string[] = ['company_code','inq','quote','creation_date','sold_to_party', 'tonnage', 'elapsed_day','sales_office','sales_engineer','dqnumber','rejection_user','rejection_reason','rejection_date'];
  displayedColumnsForPending: string[] =  ['company_code','inq','quote','creation_date','sold_to_party', 'tonnage', 'elapsed_day','sales_office','sales_engineer','dqnumber' ,'pending_with','last_approval_date','last_approval_user'];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  approvalList: OAFReportModel[] = [];
  rejectionList: OAFReportModel[] = [];
  pendingList: OAFReportModel[] = [];

  dataSourceApproval = new MatTableDataSource<OAFReportModel>(this.approvalList);
  dataSourceRejection = new MatTableDataSource<OAFReportModel>(this.rejectionList);
  dataSourcePending = new MatTableDataSource<OAFReportModel>(this.pendingList);
  currentDate : Date;
  constructor(public loader: LoaderService,public ofaApwApiService: OfaApwApiService, private toaster: ToastrManager) {

  }

  ngOnInit() {
    this.approvalReportForm = new FormGroup({
      company_code: new FormControl(),
      quote_type: new FormControl(),
      fromDate: new FormControl(),
      toDate: new FormControl()
    });
 
    this.rejectionReportForm = new FormGroup({
    company_code: new FormControl(),
    quote_type: new FormControl(),
    fromDate: new FormControl(),
    toDate: new FormControl()
    });
 
    this.pendingReportForm = new FormGroup({
    company_code: new FormControl(),
    quote_type: new FormControl(),
    fromDate: new FormControl(),
    toDate: new FormControl()
    });

    this.currentDate = new Date();
    this.approvalReportForm.get('toDate').setValue(this.currentDate);
    this.rejectionReportForm.get('toDate').setValue(this.currentDate);
    this.pendingReportForm.get('toDate').setValue(this.currentDate);    
  }

  /*This function used for search Approval data*
   * 
   */
  searchApproval() {
    this.loader.loader = true;
    this.approvalList = [];
    this.dataSourceApproval =  new MatTableDataSource<OAFReportModel>(this.approvalList);
    this.isApprovalShowGrid = false;
    this.isApprovalShowGridMessage = "";
    let isValidRecord = true;

    let fromDate = null;
    if(this.approvalReportForm.value.fromDate){
      fromDate =  this.approvalReportForm.value.fromDate._d === undefined ?   this.approvalReportForm.value.fromDate.toISOString() :  this.approvalReportForm.value.fromDate._d.toISOString();
      if(fromDate !== null)
      fromDate = new Date(fromDate).getTime();
    }
    
    let toDate = null;
    if(this.approvalReportForm.value.toDate){
      toDate =  this.approvalReportForm.value.toDate._d === undefined ?  this.approvalReportForm.value.toDate.toISOString() :  this.approvalReportForm.value.toDate._d.toISOString();
      if(toDate !== null)
      toDate = new Date(toDate).getTime();
    }

    this.finalObject = {
      company_code:this.approvalReportForm.value.company_code,
      quote_type:"Approval",
      fromDate:fromDate,
      toDate:toDate
    };

    if(isValidRecord) {
      this.isApprovalShowGridMessage = "";
      this.bindListData( this.finalObject.quote_type);
    }
  }

  /*This funciton used for clear approval section*
   * 
   */
  resetFormApproval(){
    this.finalObject= {};
    this.approvalReportForm.reset(); 
    this.approvalReportForm.get('toDate').setValue(this.currentDate);
    this.isApprovalShowGrid = false;
    this.isApprovalShowGridMessage = "";
  }


  /*This function used for search Rejection data*
   * 
   */
  searchRejection(){
    this.loader.loader = true;
    this.rejectionList = [];
    this.dataSourceRejection =  new MatTableDataSource<OAFReportModel>(this.rejectionList);
    this.isRejectionShowGrid = false;
    this.isRejectionShowGridMessage = "";

    let fromDate = null;
    if(this.rejectionReportForm.value.fromDate){
      fromDate =  this.rejectionReportForm.value.fromDate._d === undefined ?  this.rejectionReportForm.value.fromDate.toISOString() :  this.rejectionReportForm.value.fromDate._d.toISOString();
      if(fromDate !== null)
      fromDate = new Date(fromDate).getTime();
    }

    let toDate = null;
    if(this.rejectionReportForm.value.toDate){
      toDate =  this.rejectionReportForm.value.toDate._d === undefined ?  this.rejectionReportForm.value.toDate.toISOString() :  this.rejectionReportForm.value.toDate._d.toISOString();
      if(toDate !== null)
      toDate = new Date(toDate).getTime();
    }
    
    this.finalObject = {
      company_code:this.rejectionReportForm.value.company_code,
      quote_type:"Rejection",
      fromDate:fromDate,
      toDate:toDate
    };

    this.isRejectionShowGridMessage = "";
    this.bindListData( this.finalObject.quote_type);
  }

  /*This funciton used for clear rejection section*
   * 
   */
  resetFormRejection(){
    this.finalObject= {};
    this.rejectionReportForm.reset(); 
    this.rejectionReportForm.get('toDate').setValue(this.currentDate);
    this.isRejectionShowGrid = false;
    this.isRejectionShowGridMessage = "";
  }

  /*This function used for search Pending data*
   * 
   */
  searchPending(){
    this.loader.loader = true;
    this.pendingList = [];
    this.dataSourcePending =  new MatTableDataSource<OAFReportModel>(this.pendingList);
    this.isPendingShowGrid = false;
    this.isPendingGridMessage = "";

    let fromDate = null;
    if(this.pendingReportForm.value.fromDate){
      fromDate =  this.pendingReportForm.value.fromDate._d === undefined ?  this.pendingReportForm.value.fromDate.toISOString() :  this.pendingReportForm.value.fromDate._d.toISOString();
      if(fromDate !== null)
      fromDate = new Date(fromDate).getTime();
    }
    else
    {
        this.toaster
    }

    let toDate = null;
    if(this.pendingReportForm.value.toDate){
      toDate =  this.pendingReportForm.value.toDate._d === undefined ?  this.pendingReportForm.value.toDate.toISOString() :  this.pendingReportForm.value.toDate._d.toISOString();
      if(toDate !== null)
      toDate = new Date(toDate).getTime();
    }
    
    this.finalObject = {
      company_code:this.pendingReportForm.value.company_code,
      quote_type:"Pending",
      fromDate:fromDate,
      toDate:toDate
    };

    this.isPendingGridMessage = "";
    this.bindListData( this.finalObject.quote_type);
  }


  /*This funciton used for clear pending section*
   * 
   */
  resetFormPending() {
    this.finalObject= {};
    this.pendingReportForm.reset(); 
    this.pendingReportForm.get('toDate').setValue(this.currentDate);
    this.isPendingShowGrid = false;
    this.isApprovalShowGridMessage = "";
  }

  bindListData(searchSection :string) {
    this.ofaApwApiService.getOAFReportDetail(this.finalObject).then((response)=>{
      if(response) {
        if(response.error){
          this.loader.loader = false;
          this.toaster.errorToastr(response.error,"Error", {animate: "slideFromTop" ,showCloseButton: true });      
        }
        else{
          let listOfRecords = [];
      
          if(searchSection === "Approval")
            listOfRecords = response.approved;
          else if(searchSection === "Rejection")
            listOfRecords = response.rejected;
          else if(searchSection === "Pending")
            listOfRecords = response.pending;
          
          for(let i=0;i< listOfRecords.length;i++) {
            if(searchSection === "Approval") 
            {
              this.approvalList.push({company_code:(i+1).toString() ,inq:listOfRecords[i].CommonElements.inq, quote:listOfRecords[i].CommonElements.quote, creation_date:listOfRecords[i].CommonElements.creation_date ,sold_to_party: listOfRecords[i].CommonElements.sold_to_party, tonnage:listOfRecords[i].CommonElements.tonnage, elapsed_day:listOfRecords[i].CommonElements.elapsed_day, sales_office:listOfRecords[i].CommonElements.sales_office, sales_engineer:listOfRecords[i].CommonElements.sales_engineer, approved_date:listOfRecords[i].approved_date, pending_with:"",last_approval_date:"",last_approval_user:"",rejection_date:"",rejection_reason:"",rejection_user:"",dqnumber:listOfRecords[i].CommonElements.dq});
            }
            else if(searchSection === "Rejection"){
              this.rejectionList.push({company_code:(i+1).toString(),inq:listOfRecords[i].CommonElements.inq ,quote:listOfRecords[i].CommonElements.quote ,creation_date:listOfRecords[i].CommonElements.creation_date ,sold_to_party: listOfRecords[i].CommonElements.sold_to_party, tonnage:listOfRecords[i].CommonElements.tonnage, elapsed_day:listOfRecords[i].CommonElements.elapsed_day, sales_office:listOfRecords[i].CommonElements.sales_office, sales_engineer:listOfRecords[i].CommonElements.sales_engineer, approved_date:"",pending_with:"",last_approval_date:"",last_approval_user:"",rejection_date:listOfRecords[i].rejection_date, rejection_reason:listOfRecords[i].rejection_reason, rejection_user:listOfRecords[i].rejection_user, dqnumber:listOfRecords[i].CommonElements.dq});
            }
            else if(searchSection === "Pending")
            {
              this.pendingList.push({company_code:(i+1).toString(),inq:listOfRecords[i].CommonElements.inq, quote:listOfRecords[i].CommonElements.quote ,creation_date:listOfRecords[i].CommonElements.creation_date ,sold_to_party: listOfRecords[i].CommonElements.sold_to_party, tonnage:listOfRecords[i].CommonElements.tonnage, elapsed_day:listOfRecords[i].CommonElements.elapsed_day, sales_office:listOfRecords[i].CommonElements.sales_office, sales_engineer:listOfRecords[i].CommonElements.sales_engineer, approved_date:"",pending_with:listOfRecords[i].pending_with ,last_approval_date:listOfRecords[i].last_approval_date, last_approval_user:listOfRecords[i].last_approval_user, rejection_date:"", rejection_reason:"", rejection_user:"", dqnumber:listOfRecords[i].CommonElements.dq});  
            }
          }

          if(searchSection === "Approval") {
            this.isApprovalShowGrid = true;
            this.dataSourceApproval = new MatTableDataSource<OAFReportModel>(this.approvalList);
            setTimeout(() => this.dataSourceApproval.paginator = this.paginator,10);
            if(this.approvalList.length > 0) 
              this.isApprovalShowGridMessage = "";
            else
              this.isApprovalShowGridMessage = "No Record found";
          }

          else if(searchSection === "Rejection") {
            this.isRejectionShowGrid = true;
            this.dataSourceRejection = new MatTableDataSource<OAFReportModel>(this.rejectionList);
            setTimeout(() => this.dataSourceRejection.paginator = this.paginator,10);
            if(this.rejectionList.length > 0) 
              this.isRejectionShowGridMessage = "";
            else
              this.isRejectionShowGridMessage = "No Record found";
          }

          else if(searchSection === "Pending") {
            this.isPendingShowGrid = true;
            this.dataSourcePending = new MatTableDataSource<OAFReportModel>(this.pendingList);
            setTimeout(() => this.dataSourcePending.paginator = this.paginator,10);
            if(this.pendingList.length > 0) 
              this.isPendingGridMessage = "";
            else
              this.isPendingGridMessage = "No Record found";
          }
          this.finalObject= {};
          this.loader.loader = false;
          }        
      }
    }).catch((error)=> {

      this.loader.loader = false;
      this.toaster.errorToastr("Something went wrong","Error", {animate: "slideFromTop" ,showCloseButton: true });
      console.log("Error is comming while call the Oaf-report detail api"+error);
    })
  }

}
