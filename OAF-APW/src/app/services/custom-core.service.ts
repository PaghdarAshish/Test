import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions, ResponseContentType, Response } from '@angular/http';
import { AuthenticationService, AppConfigService } from '@alfresco/adf-core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { LoaderService } from '../services/loader.service';

@Injectable({
  providedIn: 'root'
})
export class CustomCoreService {
  private headers = new Headers();  
  userId: string;
  constructor(public loader: LoaderService,private http: Http,
    private auth: AuthenticationService, private appConfig: AppConfigService) { } 

  
  private authToken(){
    const admin_credential = this.appConfig.get('adminUsername')+":"+this.appConfig.get('adminPassword');
    return `Basic ${btoa(admin_credential)}`;

  }
  public httpGet(url, header?, auth?) {
    header = header ? header : { 'Content-Type': 'application/json' };
    this.headers = new Headers(header);
    if(auth){
      this.headers.append('Authorization', this.auth.getTicketBpm());   
    }        
    return this.http.get(url, { headers: this.headers}).toPromise().then(res => res.json());
  } 

  // Approval Matrix Requests 
  public httpAMPost(url, data, header?, auth?) {
    header = header ? header : { 'Content-Type': 'application/json' };
    this.headers = new Headers(header);
    if(auth){
      this.headers.append('Authorization', this.auth.getTicketBpm());   
    }    
    return this.http.post(url, data, { headers: this.headers });
  }

  public httpAMPut(url, data, header?, auth?) {
    header = header ? header : { 'Content-Type': 'application/json' };
    this.headers = new Headers(header);
    if (auth) {
        this.headers.append('Authorization', this.auth.getTicketBpm());   
   
    }    
    return this.http.put(url, data, { headers: this.headers });
  }
  public httpAMGet(url, header?, auth?) {
    header = header ? header : { 'Content-Type': 'application/json' };
    this.headers = new Headers(header);
    if(auth){
      this.headers.append('Authorization', this.auth.getTicketBpm());   
    }    
    return this.http.get(url, { headers: this.headers}).toPromise();
  }

  // Document Download Start
  public imageGet(url,documentName): any{ 
    this.getFile(url).subscribe(fileData => {
      let b: any= new Blob([fileData],{type:"application/octet-stream"});
      var url = window.URL.createObjectURL(b);
      const a: HTMLAnchorElement = document.createElement('a') as HTMLAnchorElement;
      a.href = url;
      a.download = documentName;
      document.body.appendChild(a);
      a.click();        
      document.body.removeChild(a);
      URL.revokeObjectURL(url);   
      this.loader.loader = false;      
      return true;
    },(error) => {      
      this.loader.loader =false;
      return false;
    });
   
  }

  public getFile(path: string):Observable<any>{
    this.headers = new Headers();    
    this.headers.append('Authorization', this.authToken()); 
    let options = new RequestOptions({responseType: ResponseContentType.Blob, headers: this.headers});
    return this.http.get(path,options).pipe(map((response: Response)=> <Blob>response.blob()));
  }

  // Document Download End
   
  public privateHeaders(){
    return { 'Authorization': this.authToken(), 'Content-Type': 'application/json', 'activiti-user': this.userId, 'activiti-user-value-type': 'userIdType' };
  }
}
