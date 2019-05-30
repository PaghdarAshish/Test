import { OnInit ,Component,AfterViewChecked} from '@angular/core';
import { WidgetComponent } from '@alfresco/adf-core';
import { OfaApwConstantsService } from '../../services/oaf-apw-constants.service';
import { OfaApwApiService } from '../../services/oaf-apw-api.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { TaskDetailsModel, TaskListService , ProcessService} from '@alfresco/adf-process-services';
import { LoaderService } from '../../services/loader.service';
import { ToastrManager } from 'ng6-toastr-notifications';
import { DomainService } from '../../services/domain.service';
import { CustomCoreService } from "../../services/custom-core.service";

@Component({
  selector: 'apa-oaf-form',
  templateUrl: './oaf-form.component.html',
  styleUrls: ['./oaf-form.component.scss']
})
export class OAFFormComponent extends WidgetComponent implements OnInit ,AfterViewChecked {

  quotationDetail : any = {};
  counterForDesignation :number =0;
  attachmentList :any = [];
  taskId: string;
  sub: Subscription;
  taskDetails : any;
  valueForCategory: any;
  constructor(private domain : DomainService, private coreService: CustomCoreService, public loader: LoaderService,private processService: ProcessService, private taskListService: TaskListService,private route : ActivatedRoute, private ofaApwConstantsService : OfaApwConstantsService,private oafApwApiService: OfaApwApiService, public toaster :ToastrManager) {
    super();
  }

  ngOnInit() {    
    if(this.field.value) {
      this.sub = this.route.params.subscribe(params => {
        this.taskId = params['taskId'];
    });


      this.attachmentList = [];
      let feildValue = JSON.parse(this.field.value);
      this.ofaApwConstantsService.oafForm = feildValue;
      let headerSegmentObj = feildValue.headerSegment
      let otherChargeList = [];
      let priceAndWeightBreakupList = [];
      if(feildValue.otherCharges.length > 0) {
          feildValue.otherCharges.forEach(element => {
            otherChargeList.push({
              kschl:element.kschl,
              item : element.item,
              percentage : element.percentage,
              amount : element.amount,
              totalAmount : element.totalAmount,
              updated : element.updated.toString().trim() !== "" ?  (element.updated.toString().toLowerCase().trim() == "true" ? true : false) : false
            })
          });
      }
      if(feildValue.priceAndWeightBreakups.length > 0) {
        feildValue.priceAndWeightBreakups.forEach(element => {
          priceAndWeightBreakupList.push({
            item : element.item,
            areaSqMt : element.areaSqMt,
            weightMt : element.weightMt,
            bookPrice : element.bookPrice,
            discount : element.discount,
            discountPrice : element.discountPrice,
            updated : element.updated.toString().trim() !== "" ?  (element.updated.toString().toLowerCase().trim() == "true" ? true : false) : false
          })
        });
      }
      this.quotationDetail = {
      headerSegment : {
        vbeln : {
          value : headerSegmentObj.vbeln === null ? "-":  headerSegmentObj.vbeln.value.trim() === "" ? "-":headerSegmentObj.vbeln.value,
          update: headerSegmentObj.vbeln === null ? false: headerSegmentObj.vbeln.update === null ? false : headerSegmentObj.vbeln.update
        },
        waers : {
          value : headerSegmentObj.waers === null ? "-": headerSegmentObj.waers.value.trim() === "" ? "-":headerSegmentObj.waers.value,
          update: headerSegmentObj.waers === null ? false:headerSegmentObj.waers.update === null ? false : headerSegmentObj.waers.update
        },
        nature : {
          value : headerSegmentObj.nature === null ? "-": headerSegmentObj.nature.value.trim() === "" ? "-":headerSegmentObj.nature.value,
          update: headerSegmentObj.nature === null ? false:headerSegmentObj.nature.update === null ? false : headerSegmentObj.nature.update
        },
        vtext : {
          value : headerSegmentObj.vtext === null ? "-":headerSegmentObj.vtext.value.trim() === "" ? "-": headerSegmentObj.vtext.value,
          update: headerSegmentObj.vtext === null ? false:headerSegmentObj.vtext.update === null ? false : headerSegmentObj.vtext.update
        },
        vkbur : {
          value : headerSegmentObj.vkbur === null ? "-":headerSegmentObj.vkbur.value.trim() === "" ? "-": headerSegmentObj.vkbur.value,
          update: headerSegmentObj.vkbur === null ? false:headerSegmentObj.vkbur.update === null ? false : headerSegmentObj.vkbur.update
        },
        district : {
          value : headerSegmentObj.district === null ? "-": headerSegmentObj.district.value.trim() === "" ? "-":headerSegmentObj.district.value,
          update: headerSegmentObj.district === null ? false: headerSegmentObj.district.update === null ? false : headerSegmentObj.district.update
        },
        sname : {
          value : headerSegmentObj.sname === null ? "-":headerSegmentObj.sname.value.trim() === "" ? "-":headerSegmentObj.sname.value,
          update: headerSegmentObj.sname === null ? false:headerSegmentObj.sname.update === null ? false : headerSegmentObj.sname.update
        },
        waerk : {
          value : headerSegmentObj.waerk === null ? "-":headerSegmentObj.waerk.value.trim() === "" ? "-":headerSegmentObj.waerk.value,
          update: headerSegmentObj.waerk === null ? false:headerSegmentObj.waerk.update === null ? false : headerSegmentObj.waerk.update
        },
        txt04 : {
          value : headerSegmentObj.txt04 === null ? "-":headerSegmentObj.txt04.value.trim() === "" ? "-":headerSegmentObj.txt04.value,
          update: headerSegmentObj.txt04 === null ? false:headerSegmentObj.txt04.update === null ? false : headerSegmentObj.txt04.update
        },
        txt30 : {
          value : headerSegmentObj.txt30 === null ? "-":headerSegmentObj.txt30.value.trim() === "" ? "-":headerSegmentObj.txt30.value,
          update: headerSegmentObj.txt30 === null ? false:headerSegmentObj.txt30.update === null ? false : headerSegmentObj.txt30.update
        },
        auart : {
          value : headerSegmentObj.auart === null ? "-":headerSegmentObj.auart.value.trim() === "" ? "-":headerSegmentObj.auart.value,
          update : headerSegmentObj.auart === null ? false:headerSegmentObj.auart.update === null ? false : headerSegmentObj.auart.update
        },
        bezei : {
          value : headerSegmentObj.bezei === null ? "-":headerSegmentObj.bezei.value.trim() === "" ? "-":headerSegmentObj.bezei.value,
          update: headerSegmentObj.bezei === null ? false:headerSegmentObj.bezei.update === null ? false : headerSegmentObj.bezei.update
        },
        erdat : {          
          value : headerSegmentObj.erdat === null ? "-": headerSegmentObj.erdat.value.trim() === "" ? "-":headerSegmentObj.erdat.value,
          update: headerSegmentObj.erdat === null ? false:headerSegmentObj.erdat.update === null ? false : headerSegmentObj.erdat.update
        },
        inq : {
          value : headerSegmentObj.inq === null ? "-": headerSegmentObj.inq.value.trim() === "" ? "-":headerSegmentObj.inq.value,
          update: headerSegmentObj.inq === null ? false:headerSegmentObj.inq.update === null ? false : headerSegmentObj.inq.update
        },
        revno : {
          value : headerSegmentObj.revno === null ? "-": headerSegmentObj.revno.value.trim() === "" ? "-":headerSegmentObj.revno.value ,
          update: headerSegmentObj.revno === null ? false: headerSegmentObj.revno.update === null ? false : headerSegmentObj.revno.update
        },
        quo : {
          value : headerSegmentObj.quo === null ? "-": headerSegmentObj.quo.value.trim() === "" ? "-":headerSegmentObj.quo.value,
          update: headerSegmentObj.quo === null ? false:headerSegmentObj.quo.update === null ? false : headerSegmentObj.quo.update
        },
        bstnk : {
          value : headerSegmentObj.bstnk === null ? "-": headerSegmentObj.bstnk.value.trim() === "" ? "-":headerSegmentObj.bstnk.value,
          update: headerSegmentObj.bstnk === null ? false: headerSegmentObj.bstnk.update === null ? false : headerSegmentObj.bstnk.update
        },
        custname : {
          value : headerSegmentObj.custname  === null ? "-": headerSegmentObj.custname.value.trim() === "" ? "-":headerSegmentObj.custname.value,
          update: headerSegmentObj.custname  === null ? false: headerSegmentObj.custname.update === null ? false : headerSegmentObj.custname.update
        },
        kurrf : {
          value : headerSegmentObj.kurrf  === null ? "-": headerSegmentObj.kurrf.value.trim() === "" ? "-":headerSegmentObj.kurrf.value,
          update: headerSegmentObj.kurrf  === null ? false: headerSegmentObj.kurrf.update === null ? false : headerSegmentObj.kurrf.update
        },
        cltname : {
          value : headerSegmentObj.cltname === null ?  "-": headerSegmentObj.cltname.value.trim() === "" ? "-":headerSegmentObj.cltname.value,
          update: headerSegmentObj.cltname === null ? false:headerSegmentObj.cltname.update === null ? false : headerSegmentObj.cltname.update
        },
        cltloc : {
          value : headerSegmentObj.cltloc === null ? "-": headerSegmentObj.cltloc.value.trim() === "" ? "-":headerSegmentObj.cltloc.value ,
          update: headerSegmentObj.cltloc === null ? false:headerSegmentObj.cltloc.update === null ? false : headerSegmentObj.cltloc.update
        },
        steeldisc : {
          value : headerSegmentObj.steeldisc === null ? "-": headerSegmentObj.steeldisc.value.trim() === "" ? "-":headerSegmentObj.steeldisc.value,
          update: headerSegmentObj.steeldisc === null ? false: headerSegmentObj.steeldisc.update === null ? false : headerSegmentObj.steeldisc.update
        },
        steeldp : {
          value : headerSegmentObj.steeldp === null ? "-": headerSegmentObj.steeldp.value.trim() === "" ? "-":headerSegmentObj.steeldp.value,
          update: headerSegmentObj.steeldp === null ? false:headerSegmentObj.steeldp.update === null ? false : headerSegmentObj.steeldp.update
        },
        steeldpr : {
          value : headerSegmentObj.steeldpr === null ? "-":headerSegmentObj.steeldpr.value.trim() === "" ? "-": headerSegmentObj.steeldpr.value,
          update: headerSegmentObj.steeldpr === null ? false: headerSegmentObj.steeldpr.update === null ? false : headerSegmentObj.steeldpr.update
        },
        steeldprmt : {
          value : headerSegmentObj.steeldprmt === null ? "-":headerSegmentObj.steeldprmt.value.trim() === "" ? "-": headerSegmentObj.steeldprmt.value,
          update: headerSegmentObj.steeldprmt === null ? false:headerSegmentObj.steeldprmt.update === null ? false : headerSegmentObj.steeldprmt.update
        },
        swpdisc : {
          value : headerSegmentObj.swpdisc === null ? "-":headerSegmentObj.swpdisc.value.trim() === "" ? "-": headerSegmentObj.swpdisc.value,
          update: headerSegmentObj.swpdisc === null ? false:headerSegmentObj.swpdisc.update === null ? false : headerSegmentObj.swpdisc.update
        },
        swpdidp : {
          value : headerSegmentObj.swpdidp === null ? "-":headerSegmentObj.swpdidp.value.trim() === "" ? "-": headerSegmentObj.swpdidp.value,
          update: headerSegmentObj.swpdidp === null ? false:headerSegmentObj.swpdidp.update === null ? false : headerSegmentObj.swpdidp.update
        },
        swpdbp : {
          value : headerSegmentObj.swpdbp === null ? "-": headerSegmentObj.swpdbp.value.trim() === "" ? "-":headerSegmentObj.swpdbp.value,
          update: headerSegmentObj.swpdbp === null ? false:headerSegmentObj.swpdbp.update === null ? false : headerSegmentObj.swpdbp.update
        },
        swpdmt : {
          value : headerSegmentObj.swpdmt === null ? "-": headerSegmentObj.swpdmt.value.trim() === "" ? "-":headerSegmentObj.swpdmt.value,
          update: headerSegmentObj.swpdmt === null ? false: headerSegmentObj.swpdmt.update === null ? false : headerSegmentObj.swpdmt.update
        },
        roofarea : {
          value : headerSegmentObj.roofarea === null ? "-": headerSegmentObj.roofarea.value.trim() === "" ? "-":headerSegmentObj.roofarea.value,
          update: headerSegmentObj.roofarea === null ? false: headerSegmentObj.roofarea.update === null ? false : headerSegmentObj.roofarea.update
        },
        wallarea : {
          value : headerSegmentObj.wallarea === null ? "-": headerSegmentObj.wallarea.value.trim() === "" ? "-":headerSegmentObj.wallarea.value,
          update: headerSegmentObj.wallarea === null ? false :headerSegmentObj.wallarea.update === null ? false : headerSegmentObj.wallarea.update
        },
        accdp : {
          value : headerSegmentObj.accdp === null ? "-":headerSegmentObj.accdp.value.trim() === "" ? "-": headerSegmentObj.accdp.value ,
          update: headerSegmentObj.accdp === null ? false: headerSegmentObj.accdp.update === null ? false : headerSegmentObj.accdp.update
        },
        accmt : {
          value : headerSegmentObj.accmt === null ? "-":headerSegmentObj.accmt.value.trim() === "" ? "-": headerSegmentObj.accmt.value,
          update: headerSegmentObj.accmt === null ? false: headerSegmentObj.accmt.update === null ? false : headerSegmentObj.accmt.update
        },
        insdp : {
          value : headerSegmentObj.insdp === null ? "-": headerSegmentObj.insdp.value.trim() === "" ? "-":headerSegmentObj.insdp.value,
          update: headerSegmentObj.insdp === null ? false: headerSegmentObj.insdp.update === null ? false : headerSegmentObj.insdp.update
        },
        insmt : {
          value : headerSegmentObj.insmt=== null ? "-":headerSegmentObj.insmt.value.trim() === "" ? "-": headerSegmentObj.insmt.value ,
          update: headerSegmentObj.insmt=== null ? false: headerSegmentObj.insmt.update === null ? false : headerSegmentObj.insmt.update
        },
        buydp : {
          value : headerSegmentObj.buydp=== null ? "-":headerSegmentObj.buydp.value.trim() === "" ? "-": headerSegmentObj.buydp.value,
          update: headerSegmentObj.buydp=== null ? false: headerSegmentObj.buydp.update === null ? false : headerSegmentObj.buydp.update
        },
        buymt : {
          value : headerSegmentObj.buymt === null ? "-":headerSegmentObj.buymt.value.trim() === "" ? "-": headerSegmentObj.buymt.value,
          update: headerSegmentObj.buymt === null ? false: headerSegmentObj.buymt.update === null ? false : headerSegmentObj.buymt.update
        },
        spaintbp : {
          value : headerSegmentObj.spaintbp  === null ? "-":headerSegmentObj.spaintbp.value.trim() === "" ? "-": headerSegmentObj.spaintbp.value ,
          update: headerSegmentObj.spaintbp  === null ? false: headerSegmentObj.spaintbp.update === null ? false : headerSegmentObj.spaintbp.update
        },
        kst : {
          value : headerSegmentObj.kst === null ? "-":headerSegmentObj.kst.value.trim() === "" ? "-":this.getValue(headerSegmentObj.kst.value),
          update: headerSegmentObj.kst === null ? false:headerSegmentObj.kst.update === null ? false : headerSegmentObj.kst.update
        },
        ksttext : {
          value : headerSegmentObj.ksttext === null ? "-":headerSegmentObj.ksttext.value.trim() === "" ? "-":headerSegmentObj.ksttext.value,
          update :headerSegmentObj.ksttext === null ? false: headerSegmentObj.ksttext.update === null ? false : headerSegmentObj.ksttext.update
        },
        downpay : {
          value : headerSegmentObj.downpay  === null ? "-":headerSegmentObj.downpay.value.trim() === "" ? "-":headerSegmentObj.downpay.value,
          update: headerSegmentObj.downpay  === null ? false:headerSegmentObj.downpay.update === null ? false : headerSegmentObj.downpay.update
        },
        downpaych : {
          value : headerSegmentObj.downpaych === null ? "-":headerSegmentObj.downpaych.value.trim() === "" ? "-": this.getValue(headerSegmentObj.downpaych.value),
          update: headerSegmentObj.downpaych === null ? false:headerSegmentObj.downpaych.update === null ? false : headerSegmentObj.downpaych.update
        },
        downpaytt : {
          value : headerSegmentObj.downpaytt === null ? "-": headerSegmentObj.downpaytt.value.trim() === "" ? "-":this.getValue(headerSegmentObj.downpaytt.value),
          update: headerSegmentObj.downpaytt === null ? false:headerSegmentObj.downpaytt.update === null ? false : headerSegmentObj.downpaytt.update
        },
        downpaylc : {
          value : headerSegmentObj.downpaylc === null ? "-":headerSegmentObj.downpaylc.value.trim() === "" ? "-": this.getValue(headerSegmentObj.downpaylc.value),
          update: headerSegmentObj.downpaylc === null ? false:headerSegmentObj.downpaylc.update === null ? false : headerSegmentObj.downpaylc.update
        },
        downpayot : {
          value : headerSegmentObj.downpayot === null ? "-": headerSegmentObj.downpayot.value.trim() === "" ? "-":this.getValue(headerSegmentObj.downpayot.value),
          update: headerSegmentObj.downpayot === null ? false:headerSegmentObj.downpayot.update === null ? false : headerSegmentObj.downpayot.update
        },
        billpay : {
          value : headerSegmentObj.billpay === null ? "-": headerSegmentObj.billpay.value.trim() === "" ? "-":headerSegmentObj.billpay.value ,
          update: headerSegmentObj.billpay === null ? false: headerSegmentObj.billpay.update === null ? false : headerSegmentObj.billpay.update
        },
        billpaych : {
          value : headerSegmentObj.billpaych === null ? "-": headerSegmentObj.billpaych.value.trim() === "" ? "-":this.getValue(headerSegmentObj.billpaych.value),
          update: headerSegmentObj.billpaych === null ? false:headerSegmentObj.billpaych.update === null ? false : headerSegmentObj.billpaych.update
        },
        billpaytt : {
          value : headerSegmentObj.billpaytt === null ? "-":headerSegmentObj.billpaytt.value.trim() === "" ? "-": this.getValue(headerSegmentObj.billpaytt.value),
          update: headerSegmentObj.billpaytt === null ? false:headerSegmentObj.billpaytt.update === null ? false : headerSegmentObj.billpaytt.update
        },
        billpaylc : {
          value : headerSegmentObj.billpaylc === null ? "-":headerSegmentObj.billpaylc.value.trim() === "" ? "-": this.getValue(headerSegmentObj.billpaylc.value),
          update: headerSegmentObj.billpaylc === null ? false: headerSegmentObj.billpaylc.update === null ? false : headerSegmentObj.billpaylc.update
        },
        billpayot : {
          value : headerSegmentObj.billpayot === null ? "-": headerSegmentObj.billpayot.value.trim() === "" ? "-":this.getValue(headerSegmentObj.billpayot.value),
          update: headerSegmentObj.billpayot === null ? false:headerSegmentObj.billpayot.update === null ? false : headerSegmentObj.billpayot.update
        },
        penalty : {
          value : headerSegmentObj.penalty === null ? "-": headerSegmentObj.penalty.value.trim() === "" ? "-": this.getValue(headerSegmentObj.penalty.value),
          update: headerSegmentObj.penalty === null ? false: headerSegmentObj.penalty.update === null ? false : headerSegmentObj.penalty.update
        },
        penaltytxt : {
          value : headerSegmentObj.penaltytxt  === null ? "-": headerSegmentObj.penaltytxt.value.trim() === "" ? "-": headerSegmentObj.penaltytxt.value  ,
          update: headerSegmentObj.penaltytxt  === null ? false: headerSegmentObj.penaltytxt.update === null ? false : headerSegmentObj.penaltytxt.update
        },
        perf : {
          value : headerSegmentObj.perf === null ? "-":headerSegmentObj.perf.value.trim() === "" ? "-": this.getValue(headerSegmentObj.perf.value),
          update: headerSegmentObj.perf === null ? false:headerSegmentObj.perf.update === null ? false : headerSegmentObj.perf.update
        },
        perfbond : {
          value :  headerSegmentObj.perfbond === null ? "-":headerSegmentObj.perfbond.value.trim() === "" ? "-": headerSegmentObj.perfbond.value  ,
          update : headerSegmentObj.perfbond === null ? false: headerSegmentObj.perfbond.update === null ? false : headerSegmentObj.perfbond.update
        },
        perfdura : {
          value : headerSegmentObj.perfdura === null ? "-": headerSegmentObj.perfdura.value.trim() === "" ? "-":headerSegmentObj.perfdura.value  ,
          update :headerSegmentObj.perfdura === null ? false: headerSegmentObj.perfdura.update === null ? false : headerSegmentObj.perfdura.update
        },
        advance : {
          value : headerSegmentObj.advance === null ? "-":headerSegmentObj.advance.value.trim() === "" ? "-": this.getValue(headerSegmentObj.advance.value),
          update: headerSegmentObj.advance === null ? false:headerSegmentObj.advance.update === null ? false : headerSegmentObj.advance.update
        },
        advancebond : {
          value : headerSegmentObj.advancebond === null ? "-":headerSegmentObj.advancebond.value.trim() === "" ? "-": headerSegmentObj.advancebond.value ,
          update: headerSegmentObj.advancebond === null ? false: headerSegmentObj.advancebond.update === null ? false : headerSegmentObj.advancebond.update
        },
        advancedura : {
          value : headerSegmentObj.advancedura  === null ? "-":headerSegmentObj.advancedura.value.trim() === "" ? "-": headerSegmentObj.advancedura.value  ,
          update: headerSegmentObj.advancedura  === null ? false: headerSegmentObj.advancedura.update === null ? false : headerSegmentObj.advancedura.update
        },
        retention : {
          value :  headerSegmentObj.retention === null ? "-": headerSegmentObj.retention.value.trim() === "" ? "-": this.getValue(headerSegmentObj.retention.value),
          update : headerSegmentObj.retention === null ? false: headerSegmentObj.retention.update === null ? false : headerSegmentObj.retention.update
        },
        retentionbond : {
          value : headerSegmentObj.retentionbond === null ? "-":headerSegmentObj.retentionbond.value.trim() === "" ? "-": headerSegmentObj.retentionbond.value ,
          update: headerSegmentObj.retentionbond === null ? false:headerSegmentObj.retentionbond.update === null ? false : headerSegmentObj.retentionbond.update
        },
        retentiondura : {
          value : headerSegmentObj.retentiondura  === null ? "-":headerSegmentObj.retentiondura.value.trim() === "" ? "-": headerSegmentObj.retentiondura.value,
          update: headerSegmentObj.retentiondura  === null ? false:headerSegmentObj.retentiondura.update === null ? false : headerSegmentObj.retentiondura.update
        },
        other : {
          value : headerSegmentObj.other === null ? "-": headerSegmentObj.other.value.trim() === "" ? "-": this.getValue(headerSegmentObj.other.value),
          update: headerSegmentObj.other === null ? false:headerSegmentObj.other.update === null ? false : headerSegmentObj.other.update
        },
        otherbond : {
          value : headerSegmentObj.otherbond  === null ? "-": headerSegmentObj.otherbond.value.trim() === "" ? "-": headerSegmentObj.otherbond.value,
          update: headerSegmentObj.otherbond  === null ? false: headerSegmentObj.otherbond.update === null ? false : headerSegmentObj.otherbond.update
        },
        otherdura : {
          value : headerSegmentObj.otherdura === null ? "-":headerSegmentObj.otherdura.value.trim() === "" ? "-": headerSegmentObj.otherdura.value,
          update: headerSegmentObj.otherdura === null ? false:headerSegmentObj.otherdura.update === null ? false : headerSegmentObj.otherdura.update
        },
        insurance : {
          value : headerSegmentObj.insurance === null ? "-": headerSegmentObj.insurance.value.trim() === "" ? "-": this.getValue(headerSegmentObj.insurance.value),
          update: headerSegmentObj.insurance === null ? false: headerSegmentObj.insurance.update === null ? false : headerSegmentObj.insurance.update
        },
        insutext : {
          value : headerSegmentObj.insutext === null ? "-": headerSegmentObj.insutext.value.trim() === "" ? "-": headerSegmentObj.insutext.value,
          update: headerSegmentObj.insutext === null ? false: headerSegmentObj.insutext.update === null ? false : headerSegmentObj.insutext.update
        },
        dltfrom : {
          value : headerSegmentObj.dltfrom  === null ? "-":headerSegmentObj.dltfrom.value.trim() === "" ? "-":headerSegmentObj.dltfrom.value,
          update: headerSegmentObj.dltfrom  === null ? false:headerSegmentObj.dltfrom.update === null ? false : headerSegmentObj.dltfrom.update
        },
        dltto : {
          value : headerSegmentObj.dltto  === null ? "-":headerSegmentObj.dltto.value.trim() === "" ? "-":headerSegmentObj.dltto.value,
          update: headerSegmentObj.dltto  === null ? false:headerSegmentObj.dltto.update === null ? false : headerSegmentObj.dltto.update
        },
        adltfrom : {
          value : headerSegmentObj.adltfrom  === null ? "-":headerSegmentObj.adltfrom.value.trim() === "" ? "-":headerSegmentObj.adltfrom.value,
          update :headerSegmentObj.adltfrom  === null ? false:headerSegmentObj.adltfrom.update === null ? false : headerSegmentObj.adltfrom.update
        },
        adltto : {
          value : headerSegmentObj.adltto === null ? "-": headerSegmentObj.adltto.value.trim() === "" ? "-":headerSegmentObj.adltto.value,
          update :headerSegmentObj.adltto === null ? false:headerSegmentObj.adltto.update === null ? false : headerSegmentObj.adltto.update
        },
        inco1 : {
          value : headerSegmentObj.inco1  === null ? "-": headerSegmentObj.inco1.value.trim() === "" ? "-":headerSegmentObj.inco1.value,
          update: headerSegmentObj.inco1  === null ? false:headerSegmentObj.inco1.update === null ? false : headerSegmentObj.inco1.update
        },
        inco1TXT : {
          value : headerSegmentObj.inco1TXT === null ? "-":headerSegmentObj.inco1TXT.value.trim() === "" ? "-": headerSegmentObj.inco1TXT.value,
          update: headerSegmentObj.inco1TXT === null ? false:headerSegmentObj.inco1TXT.update === null ? false : headerSegmentObj.inco1TXT.update
        },
        spaint : {
          value : headerSegmentObj.spaint === null ? "-": headerSegmentObj.spaint.value.trim() === "" ? "-":this.getValue(headerSegmentObj.spaint.value),
          update: headerSegmentObj.spaint === null ? false:headerSegmentObj.spaint.update === null ? false : headerSegmentObj.spaint.update
        },
        spainttext : {
          value :  headerSegmentObj.spainttext === null ? "-":headerSegmentObj.spainttext.value.trim() === "" ? "-": headerSegmentObj.spainttext.value ,
          update : headerSegmentObj.spainttext === null ? false: headerSegmentObj.spainttext.update === null ? false : headerSegmentObj.spainttext.update
        },
        swp : {
          value : headerSegmentObj.swp === null ? "-": headerSegmentObj.swp.value.trim() === "" ? "-": this.getValue(headerSegmentObj.swp.value),
          update: headerSegmentObj.swp === null ? false:headerSegmentObj.swp.update === null ? false : headerSegmentObj.swp.update
        },
        swptext : {
          value :  headerSegmentObj.swptext === null ? "-": headerSegmentObj.swptext.value.trim() === "" ? "-": headerSegmentObj.swptext.value ,
          update : headerSegmentObj.swptext === null ? false:  headerSegmentObj.swptext.update === null ? false : headerSegmentObj.swptext.update
        },
        owj : {
          value : headerSegmentObj.owj === null ? "-": headerSegmentObj.owj.value.trim() === "" ? "-": this.getValue(headerSegmentObj.owj.value),
          update: headerSegmentObj.owj === null ? false: headerSegmentObj.owj.update === null ? false : headerSegmentObj.owj.update
        },
        owjtext : {
          value : headerSegmentObj.owjtext === null ? "-":headerSegmentObj.owjtext.value.trim() === "" ? "-": headerSegmentObj.owjtext.value ,
          update: headerSegmentObj.owjtext === null ? false: headerSegmentObj.owjtext.update === null ? false : headerSegmentObj.owjtext.update
        },
        dsw : {
          value : headerSegmentObj.dsw === null ? "-":headerSegmentObj.dsw.value.trim() === "" ? "-": this.getValue(headerSegmentObj.dsw.value),
          update: headerSegmentObj.dsw === null ? false: headerSegmentObj.dsw.update === null ? false : headerSegmentObj.dsw.update
        },
        dswtext : {
          value : headerSegmentObj.dswtext === null ? "-":headerSegmentObj.dswtext.value.trim() === "" ? "-": headerSegmentObj.dswtext.value ,
          update: headerSegmentObj.dswtext === null ? false: headerSegmentObj.dswtext.update === null ? false : headerSegmentObj.dswtext.update
        },
        osp : {
          value : headerSegmentObj.osp === null ? "-": headerSegmentObj.osp.value.trim() === "" ? "-":this.getValue(headerSegmentObj.osp.value),
          update: headerSegmentObj.osp === null ? false:headerSegmentObj.osp.update === null ? false : headerSegmentObj.osp.update
        },
        osptext : {
          value : headerSegmentObj.osptext  === null ? "-":headerSegmentObj.osptext.value.trim() === "" ? "-":headerSegmentObj.osptext.value ,
          update: headerSegmentObj.osptext  === null ? false:headerSegmentObj.osptext.update === null ? false : headerSegmentObj.osptext.update
        },
        remarks : {
          value : headerSegmentObj.remarks === null ? "-":headerSegmentObj.remarks.value.trim() === "" ? "-": headerSegmentObj.remarks.value,
          update: headerSegmentObj.remarks === null ? false:headerSegmentObj.remarks.update === null ? false : headerSegmentObj.remarks.update
        },
        justificationForAboveDiscount : {
          value :  headerSegmentObj.justificationForAboveDiscount === null ? "-": headerSegmentObj.justificationForAboveDiscount.value.trim() === "" ? "-":headerSegmentObj.justificationForAboveDiscount.value ,
          update : headerSegmentObj.justificationForAboveDiscount === null ? false: headerSegmentObj.justificationForAboveDiscount.update === null ? false : headerSegmentObj.justificationForAboveDiscount.update
        },
        frieght1 : {
          value :  headerSegmentObj.frieght1 === null ? "-":headerSegmentObj.frieght1.value.trim() === "" ? "-": headerSegmentObj.frieght1.value,
          update : headerSegmentObj.frieght1 === null ? false:headerSegmentObj.frieght1.update === null ? false : headerSegmentObj.frieght1.update
        },
        trucks1 : {
          value :  headerSegmentObj.trucks1  === null ? "-": headerSegmentObj.trucks1.value.trim() === "" ? "-":headerSegmentObj.trucks1.value ,
          update : headerSegmentObj.trucks1  === null ? false: headerSegmentObj.trucks1.update === null ? false : headerSegmentObj.trucks1.update
        },
        frieght2 : {
          value :  headerSegmentObj.frieght2 === null ? "-":headerSegmentObj.frieght2.value.trim() === "" ? "-":headerSegmentObj.frieght2.value,
          update : headerSegmentObj.frieght2   === null ? false:headerSegmentObj.frieght2.update === null ? false : headerSegmentObj.frieght2.update
        },
        trucks2 : {
          value : headerSegmentObj.trucks2 === null ? "-":headerSegmentObj.trucks2.value.trim() === "" ? "-":headerSegmentObj.trucks2.value  ,
          update: headerSegmentObj.trucks2 === null ?false:headerSegmentObj.trucks2.update === null ? false : headerSegmentObj.trucks2.update
        },
        status : {
          value : headerSegmentObj.status === null ? "-":headerSegmentObj.status.value.trim() === "" ? "-":headerSegmentObj.status.value ,
          update: headerSegmentObj.status === null ? false:headerSegmentObj.status.update === null ? false : headerSegmentObj.status.update
        },
        ernam : {
          value : headerSegmentObj.ernam === null ? "-":headerSegmentObj.ernam.value.trim() === "" ? "-":headerSegmentObj.ernam.value ,
          update: headerSegmentObj.ernam === null ? false:headerSegmentObj.ernam.update === null ? false : headerSegmentObj.ernam.update
        },
        erzet : {
          value : headerSegmentObj.erzet  === null ? "-":headerSegmentObj.erzet.value.trim() === "" ? "-":headerSegmentObj.erzet.value,
          update: headerSegmentObj.erzet  === null ? false:headerSegmentObj.erzet.update === null ? false : headerSegmentObj.erzet.update
        },
        smtpaddr : {
          value : headerSegmentObj.smtpaddr === null ? "-": headerSegmentObj.smtpaddr.value.trim() === "" ? "-":headerSegmentObj.smtpaddr.value ,
          update: headerSegmentObj.smtpaddr === null ? false: headerSegmentObj.smtpaddr.update === null ? false : headerSegmentObj.smtpaddr.update
        },
        projmt : {
          value : headerSegmentObj.projmt === null ? "-": headerSegmentObj.projmt.value.trim() === "" ? "-":headerSegmentObj.projmt.value,
          update: headerSegmentObj.projmt === null ? false: headerSegmentObj.projmt.update === null ? false : headerSegmentObj.projmt.update
        },
        projprice : {
          value : headerSegmentObj.projprice === null ? "-": headerSegmentObj.projprice.value.trim() === "" ? "-":headerSegmentObj.projprice.value ,
          update: headerSegmentObj.projprice === null ? false: headerSegmentObj.projprice.update === null ? false : headerSegmentObj.projprice.update
        },
        projother : {
          value : headerSegmentObj.projother === null ? "-":headerSegmentObj.projother.value.trim() === "" ? "-": headerSegmentObj.projother.value  ,
          update: headerSegmentObj.projother === null ? false:headerSegmentObj.projother.update === null ? false : headerSegmentObj.projother.update
        },
        projnet : {
          value : headerSegmentObj.projnet === null ? "-":headerSegmentObj.projnet.value.trim() === "" ? "-": headerSegmentObj.projnet.value ,
          update: headerSegmentObj.projnet === null ? false:headerSegmentObj.projnet.update === null ? false : headerSegmentObj.projnet.update
        },
        projreal : {
          value : headerSegmentObj.projreal  === null ? "-":headerSegmentObj.projreal.value.trim() === "" ? "-": headerSegmentObj.projreal.value,
          update: headerSegmentObj.projreal  === null ? false: headerSegmentObj.projreal.update === null ? false : headerSegmentObj.projreal.update
        },
        oafpdf : {
          value :  headerSegmentObj.oafpdf === null ? "-":headerSegmentObj.oafpdf.value.trim() === "" ? "-": this.getValue(headerSegmentObj.oafpdf.value),
          update:  headerSegmentObj.oafpdf === null ? false:headerSegmentObj.oafpdf.update === null ? false : headerSegmentObj.oafpdf.update
        },
        zsdkr60N : {
          value : headerSegmentObj.zsdkr60N === null ? "-": headerSegmentObj.zsdkr60N.value.trim() === "" ? "-": this.getValue(headerSegmentObj.zsdkr60N.value),
          update: headerSegmentObj.zsdkr60N === null ? false:headerSegmentObj.zsdkr60N.update === null ? false : headerSegmentObj.zsdkr60N.update
        },
        zsdkr67 : {
          value : headerSegmentObj.zsdkr67 === null ? "-": headerSegmentObj.zsdkr67.value.trim() === "" ? "-":this.getValue(headerSegmentObj.zsdkr67.value),
          update: headerSegmentObj.zsdkr67 === null ? false: headerSegmentObj.zsdkr67.update === null ? false : headerSegmentObj.zsdkr67.update
        },
        zsdkr60NMaterial : {
          value : headerSegmentObj.zsdkr60NMaterial === null ? "-":headerSegmentObj.zsdkr60NMaterial.value.trim() === "" ? "-":  this.getValue(headerSegmentObj.zsdkr60NMaterial.value),
          update: headerSegmentObj.zsdkr60NMaterial === null ? false:headerSegmentObj.zsdkr60NMaterial.update === null ? false : headerSegmentObj.zsdkr60NMaterial.update
        },
        contatt : {
          value : headerSegmentObj.contatt  === null ? "-":headerSegmentObj.contatt.value.trim() === "" ? "-": headerSegmentObj.contatt.value  ,
          update: headerSegmentObj.contatt  === null ? false:headerSegmentObj.contatt.update === null ? false : headerSegmentObj.contatt.update
        },
        otheratt : {
          value : headerSegmentObj.otheratt === null ? "-":headerSegmentObj.otheratt.value.trim() === "" ? "-": headerSegmentObj.otheratt.value,
          update: headerSegmentObj.otheratt === null ? false:headerSegmentObj.otheratt.update === null ? false : headerSegmentObj.otheratt.update
        },
        marginLHE : {
          value : headerSegmentObj.marginLHE  === null ? "-":headerSegmentObj.marginLHE.value.trim() === "" ? "-":headerSegmentObj.marginLHE.value,
          update: headerSegmentObj.marginLHE  === null ? false:headerSegmentObj.marginLHE.update === null ? false : headerSegmentObj.marginLHE.update
        },
        marginWAC : {
          value : headerSegmentObj.marginWAC === null ? "-":headerSegmentObj.marginWAC.value.trim() === "" ? "-":headerSegmentObj.marginWAC.value ,
          update: headerSegmentObj.marginWAC === null ? false:headerSegmentObj.marginWAC.update === null ? false : headerSegmentObj.marginWAC.update
        },
        marginCPP : {
          value : headerSegmentObj.marginCPP  === null ? "-":headerSegmentObj.marginCPP.value.trim() === "" ? "-":headerSegmentObj.marginCPP.value,
          update: headerSegmentObj.marginCPP  === null ? false:headerSegmentObj.marginCPP.update === null ? false : headerSegmentObj.marginCPP.update
        },
        marginPO : {
          value : headerSegmentObj.marginPO === null ? "-":headerSegmentObj.marginPO.value.trim() === "" ? "-":headerSegmentObj.marginPO.value,
          update: headerSegmentObj.marginPO === null ? false:headerSegmentObj.marginPO.update === null ? false : headerSegmentObj.marginPO.update
        },
        add1 : {
          value : headerSegmentObj.add1  === null ? "-":headerSegmentObj.add1.value.trim() === "" ? "-":headerSegmentObj.add1.value,
          update: headerSegmentObj.add1  === null ? false:headerSegmentObj.add1.update === null ? false : headerSegmentObj.add1.update
        },
        add2 : {
          value : headerSegmentObj.add2 === null ? "-":headerSegmentObj.add2.value.trim() === "" ? "-": headerSegmentObj.add2.value,
          update: headerSegmentObj.add2 === null ? false:headerSegmentObj.add2.update === null ? false : headerSegmentObj.add2.update
        },
        add3 : {
          value : headerSegmentObj.add3 === null ? "-":headerSegmentObj.add3.value.trim() === "" ? "-": headerSegmentObj.add3.value ,
          update: headerSegmentObj.add3 === null ? false:headerSegmentObj.add3.update === null ? false : headerSegmentObj.add3.update
        },
        add4 : {
          value : headerSegmentObj.add4  === null ? "-":headerSegmentObj.add4.value.trim() === "" ? "-": headerSegmentObj.add4.value,
          update: headerSegmentObj.add4  === null ? false:headerSegmentObj.add4.update === null ? false : headerSegmentObj.add4.update
        },
        add5 : {
          value : headerSegmentObj.add5 === null ? "-":headerSegmentObj.add5.value.trim() === "" ? "-": headerSegmentObj.add5.value ,
          update: headerSegmentObj.add5 === null ? false: headerSegmentObj.add5.update === null ? false : headerSegmentObj.add5.update
        },
        add6 : {
          value : headerSegmentObj.add6  === null ? "-":headerSegmentObj.add6.value.trim() === "" ? "-": headerSegmentObj.add6.value,
          update: headerSegmentObj.add6  === null ? false: headerSegmentObj.add6.update === null ? false : headerSegmentObj.add6.update
        },
        add7 : {
          value : headerSegmentObj.add7 === null ? "-": headerSegmentObj.add7.value.trim() === "" ? "-":headerSegmentObj.add7.value  ,
          update: headerSegmentObj.add7 === null ? false:headerSegmentObj.add7.update === null ? false : headerSegmentObj.add7.update
        }
      },
      otherCharges : otherChargeList,
      priceAndWeightBreakup : priceAndWeightBreakupList,
      attachments : this.attachmentList,
      }
    }

  }

  ngAfterViewChecked() {
    if(this.counterForDesignation === 0) {
         this.counterForDesignation = 1;
          this.attachmentList = [];
          if(this.ofaApwConstantsService.oafForm.attachments.length > 0) {
            this.taskListService.getTaskDetails(this.taskId).subscribe(
              (res: TaskDetailsModel) => {
                this.taskDetails = res;                
                if(this.taskDetails.endDate != null)
                {
                  this.oafApwApiService.getProcessInstanceVariables(this.taskDetails.processInstanceId).then(result => {
                    if(result) {
                      this.genrateAttachmentList(result);
                    }
                    this.quotationDetail.attachments =  this.attachmentList;
                  }).catch((error)=>{
                    console.log("Error is comming when call process instance varibale service",+error);
                  });
                }
                else
                {
                  this.processService.getProcessInstanceVariables(this.taskDetails.processInstanceId).subscribe(result => {
                    if(result) {
                      this.genrateAttachmentList(result);
                    }
                    this.quotationDetail.attachments =  this.attachmentList;
                  });
                }
           });
          }
      }
  }

  genrateAttachmentList(result :any) {
    let finalObject;
    for(let i=0;i < result.length ; i++) {
      if(result[i].name === "user_category" || result[i].id === "user_category") {
        finalObject = result[i].value;
        break;
      }
    }
    if(finalObject) {
      finalObject = JSON.parse(finalObject);
      
      Object.keys(finalObject).forEach(key => {
        if (this.taskDetails.name.includes(key)) {
            this.valueForCategory = finalObject[key];
        }
      });
      let arrayOfCategory = this.valueForCategory.split(",");
      if(arrayOfCategory.length > 0) {
        this.ofaApwConstantsService.oafForm.attachments.forEach(element => {
          for(let i=0; i < arrayOfCategory.length; i++) {
            if(arrayOfCategory[i].toString().toLowerCase().trim() === element.category.toLowerCase()){
              let foundResult = this.attachmentList.some(x=>x.docName === element.docName);
              if(element.downloadURL.trim() != ""){                
              if(!foundResult)
              {
               // element.date = this.convertDate(element.date);
                this.attachmentList.push(element);
              } 
              }
            }
          }
        });
      }
    }
}


  addedObjectInAttachmentList(element:any){
    this.attachmentList.push({
      category:element.category,
      date:this.convertDate(element.date),
      docName:element.docName,
      downloadURL:element.downloadURL,
      guid:element.guid
    });
  }

  getValue(value:any) {
    if(value.toString().trim() === "")
      return false;
    else if(value.toString().trim().toLowerCase() === "x")
      return true;
    else if(value.toString().trim().toLowerCase() === "n")
      return false;
    else if(value.toString().trim().toLowerCase() === "y")
      return true;
    else
      return value;  
  }

    
  /*This function used for return the true/false in check box ngModel*
   * 
   * @param value 
   */
  canbeChecked(value) {
      if(value) 
        return "Yes";
      else
        return "No";
  }

  
  /*This funciton used for convert date*
   * 
   * @param requestDate 
   */
  convertDate(requestDate:any) {
      if(requestDate && !requestDate.includes('/')) {
          let date = requestDate.slice(6,8);
          let month = requestDate.slice(4,6); 
          let year  = requestDate.slice(0,4);
          requestDate =  date +"/"+ month +"/"+year;
          // let addFirst = [requestDate.slice(0,4),"/",requestDate.slice(4)].join("");
          // requestDate =  [addFirst.slice(0,7),"/",addFirst.slice(7)].join("");
      }
    return requestDate;
  }

  downloadFile(nodeRef:any,docName:any) {
    this.loader.loader = true;
    const url=this.domain.apsDomain+`/activiti-app/api/enterprise/oaf-download-document?nodeRef=${nodeRef}&document=${docName}`;
    this.coreService.getFile(url).subscribe(fileData => {      
      let b: any= new Blob([fileData],{type:"application/octet-stream"});
      var url = window.URL.createObjectURL(b);
      const a: HTMLAnchorElement = document.createElement('a') as HTMLAnchorElement;
      a.href = url;
      a.download = docName;
      document.body.appendChild(a);
      a.click();        
      document.body.removeChild(a);
      URL.revokeObjectURL(url);   
      this.loader.loader = false;
      this.toaster.successToastr("Document downloaded successfully", 'Success ', {animate: "slideFromTop" , showCloseButton: true});        
    },(error) => {
      this.loader.loader=false;
      this.toaster.errorToastr("Document failed to download", 'Error ', {animate: "slideFromTop" , showCloseButton: true});      
    }); 
  }

}
