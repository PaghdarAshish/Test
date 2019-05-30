import { Component, OnInit } from '@angular/core';
import { OfaApwApiService } from '../../services/oaf-apw-api.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { LoaderService } from '../../services/loader.service';
import { ToastrManager } from 'ng6-toastr-notifications';
import { DomainService } from '../../services/domain.service';
import { CustomCoreService } from "../../services/custom-core.service";

@Component({
  selector: 'apa-oaf-document-downloader',
  templateUrl: './oaf-document-downloader.component.html',
  providers: [OfaApwApiService],
  styleUrls: ['./oaf-document-downloader.component.scss']
})
export class OafDocumentDownloaderComponent implements OnInit {

  routerSub: Subscription;  
  documentName:string;
  nodeRef:string;
  docDownloadMsg: string = "";
  constructor(private domain : DomainService, private coreService: CustomCoreService, private route: ActivatedRoute,public loader : LoaderService, public toaster :ToastrManager) { }

  ngOnInit() { 
    this.setVariables(); 
    this.downloadDocument();
  }

  public setVariables(){    
    this.routerSub = this.route.params.subscribe(( param ) => {
      this.nodeRef = param[ 'nodeRef' ];      
      this.documentName = param[ 'documentName' ]; 
    });
  }

  
  public downloadDocument(){
    this.loader.loader = true;
    const url=this.domain.apsDomain+`/activiti-app/api/enterprise/oaf-download-document?nodeRef=${this.nodeRef}&document=${this.documentName}`;
    this.coreService.getFile(url).subscribe(fileData => {      
      let b: any= new Blob([fileData],{type:"application/octet-stream"});
      var url = window.URL.createObjectURL(b);
      const a: HTMLAnchorElement = document.createElement('a') as HTMLAnchorElement;
      a.href = url;
      a.download = this.documentName;
      document.body.appendChild(a);
      a.click();        
      document.body.removeChild(a);
      URL.revokeObjectURL(url);   
      this.loader.loader = false;
      this.toaster.successToastr("Document downloaded successfully", 'Success ', {animate: "slideFromTop" , showCloseButton: true});
      this.docDownloadMsg = "Document downloaded. Thank you";      
    },(error) => {
      this.loader.loader=false;
      this.toaster.errorToastr("Document failed to download", 'Error ', {animate: "slideFromTop" , showCloseButton: true});
      this.docDownloadMsg = "Document failed to download, Please try again.";
    });       
  }
 
}
