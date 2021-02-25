import { Component, OnInit, Input, ViewContainerRef } from '@angular/core';
import { CommonsEngineProvider } from '../engine/commons-engine';
import { EngineUtilsProvider } from '../engine/engine-utils.service';
import { DatePipe } from '@angular/common';
import { MessageServiceProvider } from '../engine/message-service/message-service';
import { DataSharingServiceProvider } from '../engine/data-sharing-service/data-sharing-service';
import { Constants } from 'src/app/constants';
import { FormService } from 'src/app/service/form-service.service';
import { UUID } from 'angular2-uuid';
import { Router } from '@angular/router';
import { ToastsManager } from 'ng6-toastr/ng2-toastr';
declare var $:any;
import { ConstraintTokenizer } from '../engine/constraintTokenizer';
@Component({
  selector: 'app-new-blank-form',
  templateUrl: './new-blank-form.component.html',
  styleUrls: ['./new-blank-form.component.scss']
})
export class NewBlankFormComponent implements OnInit {

  isWeb: boolean = false;
  section: String;
  dataSharingService: DataSharingServiceProvider;
  repeatSubSection: Map < Number, IQuestionModel > = new Map();
  sectionNames = [];
  subSections: Array < Map < String, Array < IQuestionModel >>> ;
  sectionMap: Map < String, Array < Map < String, Array < IQuestionModel >>> > = new Map();
  data: Map < String, Array < Map < String, Array < IQuestionModel >>> > = new Map();
  dbFormModel: IDbFormModel;
  sectionHeading: any;
  questionMap: {} = {};

  formTitle: String;
  formModel: {} = {};
  dataModel: {} = {};
  maxDate: any;
  errorStatus: boolean = false;
  mandatoryQuestion: {} = {};
  disableStatus: boolean = false;
  disablePrimaryStatus: boolean = false;
  questionDependencyArray: {} = {};
  questionFeaturesArray: {} = {};
  constraintsArray: {} = {};
  beginRepeatArray: {} = {};
  uniqueId: String;
  checkFieldContainsAnyvalueStatus: boolean = false;
  fullDate: any; 
  createdDate: String = "";
  updatedDate: String = "";
  updatedTime: String = "";
  base64Image: any;
  currentLocation: any;
  options = {
      enableHighAccuracy: true
  };
  formTimeperiod: any;
  scoreKeyMapper : {} = [];
  selectedLanguage:string= 'EN_UK';
  errorInSection:string=null;
  errorKey:string=null
  // @Input("formId")
  formId: Number;
  sessionMap:any;

  viewType:string;
  sendingDataObj: any;
  constructor(private commonsEngineProvider : CommonsEngineProvider, 
    //  public questionService: QuestionServiceProvider,
     public messageService: MessageServiceProvider,
     public datepipe: DatePipe, 
     public formService: FormService,
     private router:Router,
    
    private engineUtilsProvider: EngineUtilsProvider,private dataSharingProvider: DataSharingServiceProvider, private constraintTokenizer:ConstraintTokenizer
    ,public toastr: ToastsManager, vcr: ViewContainerRef
    //  private atp: AmazingTimePickerService,
) { 
      this.dataSharingService = dataSharingProvider;
      this.toastr.setRootViewContainerRef(vcr);
     
  }
  async ngOnInit() {
    this.sessionMap = JSON.parse(sessionStorage.getItem('userDetails'));
        this.formTimeperiod = '';
        let formDataModel = this.formService.getDraftData()
        if(!formDataModel){
            this.formService.getBlankQuestion().subscribe(formData=>{
                this.isWeb =  true
                this.maxDate = this.datepipe.transform(new Date(), "yyyy-MM-dd");
                this.fullDate = this.datepipe.transform(new Date(), "yyyy-MM-dd").split("-");
         
                this.data = formData;
                this.disableStatus = false
                this.uniqueId =    UUID.UUID();
                this.createdDate = this.datepipe.transform(new Date(), 'dd-MM-yyyy')
                this.updatedDate = this.datepipe.transform(new Date(), 'dd-MM-yyyy')
                this.updatedTime = this.datepipe.transform(new Date(), 'HH:mm:ss')
                this.disablePrimaryStatus = true;
                this.loadQuestionBankIntoUI(this.data[Object.keys(this.data)[0]]);

                this.formService.getMonthOfForm().subscribe(res => {
                    this.formTimeperiod = res;
                })

            });
        }else{
            this.formTimeperiod=formDataModel.extraKeys.monthYearOfSubmission;
            this.isWeb =  true
            this.maxDate = this.datepipe.transform(new Date(), "yyyy-MM-dd");
            this.fullDate = this.datepipe.transform(new Date(), "yyyy-MM-dd").split("-");
        
            this.data = formDataModel.formData;
            this.disableStatus =  formDataModel.extraKeys.view==="preview"? true:false;
            this.uniqueId =    formDataModel.uniqueId
  
            this.createdDate = this.datepipe.transform(new Date(), 'dd-MM-yyyy')
            this.updatedDate = this.datepipe.transform(new Date(), 'dd-MM-yyyy')
            this.updatedTime = this.datepipe.transform(new Date(), 'HH:mm:ss')
  
            this.disablePrimaryStatus = true;
          
      
            this.loadQuestionBankIntoUI(this.data);

            // if(formDataModel.extraKeys.view=="edit"){
            //     this.checkFinalizedConstraints()
            // }
        }
     
  }
sectionSelected(ss: any, key ? : any) {

    this.sectionHeading = ss
    this.subSections = this.sectionMap.get(ss);
    $(window).scrollTop(0);
  }
  scrollToError(ss,key){
    this.sectionHeading = ss
    this.subSections = this.sectionMap.get(ss)
    if (this.tempCaryId != null)
      this.removeColor(this.tempCaryId);
    setTimeout(() => {
        let pos = window.pageYOffset;
        let eleId = document.getElementById(key+"");
        $(eleId).focus();
    }, 20);
    

  }
  getGeoLocation(question) {
      // this.geolocation.getCurrentPosition(this.options).then(resp => {
      //     this.questionMap[question.columnName].value = "Latitude :" + resp.coords.latitude + " Longitude :" + resp.coords.longitude;
      // }).catch(error => {
      //     console.log("Error getting location", error);
      // });
  }

  openCamera(question) {
      document.getElementById(question.columnName + "file-input").click();
  }
  onCameraFileChange($event, question) {
      let files = $event.target.files;
      let file = files[0];
      if ((file.name.split(".")[(file.name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === "png" 
      || (file.name.split(".")[(file.name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === "jpg" 
      || (file.name.split(".")[(file.name.split(".") as string[]).length - 1] as String).toLocaleLowerCase() === "jpeg") {
          let reader = new FileReader();
          reader.onload = this._handleReaderLoaded.bind(this);
          this.base64Image = question;
          reader.readAsBinaryString(file);
      } else {
          this.messageService.showErrorToast("Please select a image",this.toastr);
      }
  }
  _handleReaderLoaded(readerEvt) {
           let binaryString = readerEvt.target.result;
           navigator.geolocation.getCurrentPosition((position)=>{
           this.questionMap[this.base64Image.columnName].value = {
                        src: "data:image/jpeg;base64," + btoa(binaryString),
                        meta_info: "Latitude :" + position.coords.latitude + "; Longitude :" + position.coords.longitude + "; Accuracy :" + position.coords.accuracy
                    };
           },(err)=>{
            this.questionMap[this.base64Image.columnName].value = {
                      src: "data:image/jpeg;base64," + btoa(binaryString)
                  };
           })      
      }

      /**
       * This function is use to set the options in the date picker like dateFormat,disableSince,editableDateField,showTodayBtn,showClearDateBtn
       *
       * @author Jagat Bandhu (jagat@sdrc.co.in)
       */
  // public myDatePickerOptions: IMyDpOptions = {
  //     // other options...
  //     dateFormat: "dd-mm-yyyy",
  //     disableSince: {
  //         year: Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split("-")[0]),
  //         month: Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split("-")[1]),
  //         day: Number(this.datepipe.transform(new Date(), "yyyy-MM-dd").split("-")[2]) + 1
  //     },
  //     editableDateField: false,
  //     showTodayBtn: false,
  //     showClearDateBtn: false
  // };
  loadQuestionBankIntoUI(data) {
      this.data = data;
      for (let index = 0; index < Object.keys(data).length; index++) {
          this.sectionMap.set(Object.keys(data)[index], data[Object.keys(data)[index]]);
          for (let j = 0; j < data[Object.keys(data)[index]].length; j++) {
              let subSections = data[Object.keys(data)[index]][0];
              for (let qs = 0; qs < Object.keys(subSections).length; qs++) {
                  for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
                      let question: IQuestionModel = subSections[Object.keys(subSections)[qs]][q];
                    
                      if (question.attachedFiles == null) question.attachedFiles = [];
                      switch (question.controlType) {
                        case "sub-score-keeper":
                        case "score-keeper":
                        case "score-holder":
                
                        question.dependecy = false;
                        question.displayComponent = true  
                        this.questionMap[question.columnName] = question;
                   
             
                        break

                          case "table":
                          case "tableWithRowWiseArithmetic":
                              question.displayComponent = true;
                              for (let row = 0; row < question.tableModel.length; row++) {
                                  for (let column = 0; column < Object.keys(question.tableModel[row]).length; column++) {
                                      let value = question.tableModel[row][Object.keys(question.tableModel[row])[column]];
                                      if (typeof value == "object") {
                                          let cell = value;
                                          cell.dependecy = cell.relevance != null ? true : false;
                                          cell.displayComponent = cell.relevance == null ? true : false;
                                          
                                          cell = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(cell);
                                          cell.tempFinalizedMandatory = cell.finalizeMandatory;
                                          cell.tempSaveMandatory = cell.saveMandatory;
                                          cell.relevance != null ? this.drawDependencyGraph(cell.relevance, cell) : null;
                                          this.questionMap[cell.columnName] = cell;
                                          this.mandatoryQuestion[cell.columnName] = cell.finalizeMandatory;
                                         
                                      }
                                  }
                              }
                              break;
                          case 'beginrepeat':
                              question.displayComponent = true
                              question.beginRepeatMinusDisable = false
                              question.beginrepeatDisableStatus = false
                              this.repeatSubSection.set(question.key, question)
                              question = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(question)
                              question.beginRepeatMinusDisable = false
                              if (question.beginRepeat.length == 1) {
                                  question.beginRepeatMinusDisable = true
                              }
                              this.questionMap[question.columnName] = question;
                              for (let index = 0; index < question.beginRepeat.length; index++) {
                                  let beginRepeatQuestions: IQuestionModel[] = question.beginRepeat[index];
                                  for (let beginRepeatQuestion of beginRepeatQuestions) {
                                      beginRepeatQuestion.dependecy = beginRepeatQuestion.relevance != null ? true : false;
                                      beginRepeatQuestion.displayComponent = beginRepeatQuestion.relevance == null ? true : false;
                                      beginRepeatQuestion.tempFinalizedMandatory = beginRepeatQuestion.finalizeMandatory;
                                      beginRepeatQuestion.tempSaveMandatory = beginRepeatQuestion.saveMandatory;
                                      this.questionMap[beginRepeatQuestion.columnName] = beginRepeatQuestion;
                                      beginRepeatQuestion = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(beginRepeatQuestion);
                                      beginRepeatQuestion.relevance != null ? this.drawDependencyGraph(beginRepeatQuestion.relevance, beginRepeatQuestion) : null;
                                      this.mandatoryQuestion[beginRepeatQuestion.columnName] = beginRepeatQuestion.finalizeMandatory;
                                   
                                    }
                              }
                              break;
                          case "dropdown":
                          case "textbox":
                          case "textarea":
                          case "heading":
                          case "Time Widget":
                          case "cell":
                          case "uuid":
                          case "file":
                          case "mfile":
                          case 'geolocation':
                          case 'camera':
                          case 'segment':

                              question.dependecy = question.relevance != null ? true : false;
                              question.displayComponent = question.relevance == null ? true : false;
                              question.tempFinalizedMandatory = question.finalizeMandatory;
                              question.tempSaveMandatory = question.saveMandatory;
                              question = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(question)
                              this.questionMap[question.columnName] = question;
                              question.relevance != null ? this.drawDependencyGraph(question.relevance, question) : null;
                             
                              this.mandatoryQuestion[question.columnName] = question.finalizeMandatory;
                              break;
                          case "Date Widget":
                              question.dependecy = question.relevance != null ? true : false;
                              question.displayComponent = question.relevance == null ? true : false;
                              question = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(question);
                              question.tempFinalizedMandatory = question.finalizeMandatory;
                              question.tempSaveMandatory = question.saveMandatory;
                              this.questionMap[question.columnName] = question;
                              this.mandatoryQuestion[question.columnName] = question.finalizeMandatory;
                              question.relevance != null ? this.drawDependencyGraph(question.relevance, question) : null;
                           
                              break;
                          case "checkbox":
                              question.dependecy = question.relevance != null ? true : false;
                              question.displayComponent = question.relevance == null ? true : false;
                              question = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(question);
                              question.tempFinalizedMandatory = question.finalizeMandatory;
                              question.tempSaveMandatory = question.saveMandatory;
                              this.questionMap[question.columnName] = question;
                              this.mandatoryQuestion[question.columnName] = question.finalizeMandatory;
                              question.relevance != null ? this.drawDependencyGraph(question.relevance, question) : null;
                           
                              break;
                      }
                      //set the default
                  }
              }
          }
      }
      this.checkRelevanceForEachQuestion()
      for (let questionKey of this.dataSharingService.getKeys(this.beginRepeatArray)) {
          let question = this.questionMap[questionKey];
          let bgQuestion = this.beginRepeatArray[questionKey];
          if (question.value == null || question.value == 0) {
              bgQuestion.beginrepeatDisableStatus = true;
          }
      }
    
    //   console.log(this.questionMap);
    //   console.log("features array: ", this.questionFeaturesArray);
    //   console.log("dependencies array: ", this.questionDependencyArray);
    //   console.log("beginRepeatArray", this.beginRepeatArray);
    //   console.log("repeat Subsection", this.repeatSubSection);
    //   console.log('constraints array', this.constraintsArray)
    //   console.log('scoreKeyMapper array', this.scoreKeyMapper)
      this.sectionNames = Array.from(this.sectionMap.keys());
      this.section = this.sectionNames[0];
      this.sectionSelected(this.section);
  }
  addAnotherWorker(key: Number) {
          let beginRepeatParent: IQuestionModel = this.repeatSubSection.get(key);
          let beginRepeatQuestionList: IQuestionModel[] = beginRepeatParent.beginRepeat[beginRepeatParent.beginRepeat.length - 1];
          let size = beginRepeatParent.beginRepeat.length;
          let clonedQuestion: IQuestionModel[];
          clonedQuestion = JSON.parse(JSON.stringify(beginRepeatQuestionList));
 
          for (let index = 0; index < clonedQuestion.length; index++) {
              //if dependent question is inside begin repeat section,
              // we have rename dependent column name as we have renamed depending question column name
              let colName = (clonedQuestion[index].columnName as String).split("-")[3];
              let colIndex = (clonedQuestion[index].columnName as String).split("-")[2];


              clonedQuestion[index].value = null;
              clonedQuestion[index].othersValue = false;
              clonedQuestion[index].isOthersSelected = false;
              clonedQuestion[index].dependecy =  clonedQuestion[index].relevance != null ? true : false;
              clonedQuestion[index].columnName = beginRepeatParent.columnName + "-" + size + "-" + colIndex + "-" + colName;
              clonedQuestion[index] = this.commonsEngineProvider.renameRelevanceAndFeaturesAndConstraintsAndScoreExpression(clonedQuestion[index],this.questionMap,beginRepeatParent,size);

              //setting up default setting and added to dependency array and feature array
              clonedQuestion[index].displayComponent = clonedQuestion[index].relevance == null ? true : false;
              this.questionMap[clonedQuestion[index].columnName] = clonedQuestion[index];
              clonedQuestion[index] = this.setupDefaultSettingsAndConstraintsAndFeatureGraph(clonedQuestion[index]);
            //  clonedQuestion[index].displayComponent = true
          }

          for (let index = 0; index < clonedQuestion.length; index++) {
            clonedQuestion[index].relevance != null ? this.drawDependencyGraph(clonedQuestion[index].relevance, clonedQuestion[index]) : null;
        }

          this.checkRelevanceForEachQuestion()
          if (beginRepeatParent.limit_bg_repeat) {
              if (this.questionMap[beginRepeatParent.bgDependentColumn as any].value != null) {
                  if (beginRepeatParent.beginRepeat.length < this.questionMap[beginRepeatParent.bgDependentColumn as any].value) {
                    beginRepeatParent.beginRepeat.push(clonedQuestion);
                  } else {
                      this.messageService.showErrorToast("Exceed Size",this.toastr);
                  }
              } else {
                  this.sectionSelected(Object.keys(this.data)[0]);
                  this.messageService.showErrorToast("Please enter " + this.questionMap[beginRepeatParent.bgDependentColumn as any].label,this.toastr);
              } 
          } else {
            beginRepeatParent.beginRepeat.push(clonedQuestion);
          }
          if (beginRepeatParent.beginRepeat.length > 1) {
            beginRepeatParent.beginRepeatMinusDisable = false;
          }
         
      }
      /**
       * This method is called, when clicks on the delete icon to delete the beginRepeat section and clear the value of fields inside beginRepeat.
       *
       * @author Azhar (azaruddin@sdrc.co.in)
       * @param key
       */
  deleteLastWorker(key: Number, bgquestion: IQuestionModel) {

      let beginRepeatParent: IQuestionModel = this.repeatSubSection.get(key);
      let clonedQuestion: IQuestionModel[] = beginRepeatParent.beginRepeat[beginRepeatParent.beginRepeat.length - 1];
      for (let index = 0; index < clonedQuestion.length; index++) {
        clonedQuestion[index].relevance !=null ? this.removeFromDependencyGraph(clonedQuestion[index].relevance,clonedQuestion[index]) : null
      }
    
    if (bgquestion.beginRepeat.length > 1) {
              bgquestion.beginRepeat.pop();
              if (bgquestion.beginRepeat.length == 1) {
                  bgquestion.beginRepeatMinusDisable = true;
              } else {
                  bgquestion.beginRepeatMinusDisable = false;
              }
          } else {
              for (let i = 0; i < bgquestion.beginRepeat.length; i++) {
                  for (let j = 0; j < bgquestion.beginRepeat[i].length; j++) {
                      bgquestion.beginRepeat[i][j].value = null;
                  }
              }
          }
      }
      //Biswa
  tempCaryId: any;
  
      //Biswa
  async onSave(type: String) {
      let formId;
      let headerData: {} = {}
      let image: string = Constants.defaultImage;
      this.errorInSection = null;
      this.errorKey=null;
     
      let wasConstraintFailedOnFinalize = false;
      if(type=='finalized'){
        wasConstraintFailedOnFinalize =  this.checkFinalizedConstraints();
        if(wasConstraintFailedOnFinalize){
            this.scrollToError(this.errorInSection,this.errorKey)
        }
      }else{
        this.checkFinalizedConstraints();
        wasConstraintFailedOnFinalize = false;
      }
    
      if (type=='save' ||  !wasConstraintFailedOnFinalize) {
          this.errorStatus = false;
          loop1: for (let index = 0; index < Object.keys(this.data).length; index++) {
              this.sectionMap.set(Object.keys(this.data)[index], this.data[Object.keys(this.data)[index]])
              for (let j = 0; j < this.data[Object.keys(this.data)[index]].length; j++) {
                  let subSections = this.data[Object.keys(this.data)[index]][0]
                  for (let qs = 0; qs < Object.keys(subSections).length; qs++) {
                      for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
                          let question: IQuestionModel = subSections[Object.keys(subSections)[qs]][q]
                          formId = question.formId
                          if (question.finalizeMandatory == true) {
                              switch (question.controlType) {
                                  case "geolocation":
                                      if (((question.tempSaveMandatory && type=="save") || (question.tempFinalizedMandatory && type=="finalized")) && (question.value == null || question.value == "")) {
                                          this.errorStatus = true;
                                        //   this.errorColor(Object.keys(this.data)[index], question.columnName);
                                          this.sectionSelected(Object.keys(this.data)[index], question.columnName)
                                          this.messageService.showErrorToast("Please enter " + question.label,this.toastr);
                                          break loop1;
                                      }
                                      break;
                                  case "camera":
                                      if (((question.tempSaveMandatory && type=="save") || (question.tempFinalizedMandatory && type=="finalized")) && (question.value == null || question.value == "")) {
                                          this.errorStatus = true;
                                        //   this.errorColor(Object.keys(this.data)[index], question.columnName);
                                        this.sectionSelected(Object.keys(this.data)[index], question.columnName)
                                          this.messageService.showErrorToast("Please enter " + question.label,this.toastr);
                                          break loop1;
                                      }
                                      break;
                                  case 'dropdown':
                                      for (let i = 0; i < question.options.length; i++) {
                                          if (((question.tempSaveMandatory && type=="save") || (question.tempFinalizedMandatory && type=="finalized")) && question.value == null) {
                                              this.errorStatus = true
                                            //   this.errorColor(Object.keys(this.data)[index], question.columnName);
                                            this.sectionSelected(Object.keys(this.data)[index], question.columnName)
                                              this.messageService.showErrorToast("Please select " + question.label,this.toastr)
                                              break loop1
                                          } else if (((question.tempSaveMandatory && type=="save") || (question.tempFinalizedMandatory && type=="finalized")) && question.value == null) {
                                              this.errorStatus = true
                                            //   this.errorColor(Object.keys(this.data)[index], question.columnName);
                                            this.sectionSelected(Object.keys(this.data)[index], question.columnName)
                                              this.messageService.showErrorToast("Please select " + question.label,this.toastr)
                                              break loop1
                                          }
                                      }
                                      break;
                                  case 'textarea':
                                  case 'textbox':
                                      if (((question.tempSaveMandatory && type=="save") || (question.tempFinalizedMandatory && type=="finalized")) && (question.value == null || question.value == '')) {
                                          this.errorStatus = true
                                        //   this.errorColor(Object.keys(this.data)[index], question.columnName);
                                          this.messageService.showErrorToast("Please enter a valid " + question.label,this.toastr)
                                          this.sectionSelected(Object.keys(this.data)[index], question.columnName)
                                          break loop1
                                      }
                                      break;
                                  case 'Time Widget':
                                      if (((question.tempSaveMandatory && type=="save") || (question.tempFinalizedMandatory && type=="finalized")) && (question.value == null || question.value == '')) {
                                          this.errorStatus = true
                                        //   this.errorColor(Object.keys(this.data)[index], question.columnName);
                                          this.messageService.showErrorToast("Please select " + question.label,this.toastr)
                                          break loop1
                                      }
                                      break;
                                  case 'Date Widget':
                                      if (((question.tempSaveMandatory && type=="save") || (question.tempFinalizedMandatory && type=="finalized")) && (question.value == null || question.value == '')) {
                                          this.errorStatus = true
                                        //   this.errorColor(Object.keys(this.data)[index], question.columnName);
                                          this.messageService.showErrorToast("Please select " + question.label,this.toastr)
                                          break loop1
                                      }
                                      break;
                              
                                  case 'tableWithRowWiseArithmetic':
                                      let tableData = question.tableModel
                                      for (let i = 0; i < tableData.length; i++) {
                                          for (let j = 0; j < Object.keys(tableData[i]).length; j++) {
                                              let cell = (tableData[i])[Object.keys(tableData[i])[j]]
                                              if (typeof cell == 'object') {
                                               
                                                  if ( ((cell.tempSaveMandatory && type=="save") || (cell.tempFinalizedMandatory && type=="finalized")) && (cell.value == null || (cell.value as string).trim() == "")) {
                                                      this.errorStatus = true
                                                    //   this.errorColor(Object.keys(this.data)[index], cell.columnName);
                                                      this.messageService.showErrorToast("Please enter " + question.label + " " + (cell.label).replace('@@split@@', ''),this.toastr)
                                                      break loop1;
                                                  }
                                     
                                              }
                                          }
                                      }
                                      break;
                                 
                                      case 'beginrepeat':
                                         
                                          for (let bgindex = 0; bgindex < question.beginRepeat.length; bgindex++) {
                                              let beginRepeatQuestions: IQuestionModel[] = question.beginRepeat[bgindex];
                                              for (let beginRepeatQuestion of beginRepeatQuestions) {
                                                if (((beginRepeatQuestion.tempSaveMandatory && type=="save") || (beginRepeatQuestion.tempFinalizedMandatory && type=="finalized")) && (beginRepeatQuestion.value == null || beginRepeatQuestion.value == '')) {
                                                  this.errorStatus = true
                                                //   this.errorColor(Object.keys(this.data)[index], beginRepeatQuestion.columnName);
                                                  this.messageService.showErrorToast("Please select " + beginRepeatQuestion.label,this.toastr)
                                                  break loop1
                                              }
                                              
                                                }
                                          }
                              break;
                              }
                        
                          }
                     
                        if (question.reviewHeader) {
                            switch (question.controlType) {
                                case 'dropdown':
                                    headerData[question.reviewHeader] = question.options && question.options.length ? question.options.filter(d => d.key === question.value)[0].value : question.value;
                                    break;
                                case 'textbox':
                                    headerData[question.reviewHeader] =  question.value;
                                    break;
                                case 'camera':
                                    headerData[question.reviewHeader] =  question.value.src;
                                    image = question.value.src;
                                    break;
                            }
                        }
                      }
                  }
              }
          }
          if (!this.errorStatus) {
              if(type == 'finalized') {
                this.sendingDataObj = {type: type, formId: formId, headerData: headerData, image: image}
                $("#preview").modal('show');
              } else {
                  this.sendData(type, formId, headerData, image);
              }
            }
      } 
  }
  async sendData(type, formId, headerData, image) {
    $("#preview").modal('hide');
    this.dbFormModel = {
        createdDate: this.createdDate,
        updatedDate: this.updatedDate,
        updatedTime: this.updatedTime,
        formStatus: type == 'save' ? 'save' : 'finalized',
        formData: this.data,
        formId: formId,
        uniqueId: this.uniqueId,
        formDataHead: headerData,
        image: image,
        attachmentCount: 0
    }
    let s =  await this.formService.saveData(this.formId, this.dbFormModel)
    .subscribe(data => {
        if (data == 'success') {
          sessionStorage.setItem("dataDontEntryAllowRoute",JSON.stringify(true));
            if (type == 'save') {
                this.messageService.showSuccessToast(Constants.message.saveSuccess,this.toastr)
            } else {
                this.messageService.showSuccessToast(Constants.message.finalizedSuccess,this.toastr)
                setTimeout(d=>{this.router.navigateByUrl("/drafts");},1500)
            }
                                 
        } else {
            this.router.navigateByUrl("/drafts");
        }
    },error=>{
        if(error.error !=null)
      this.messageService.showErrorToast(error.error,this.toastr)
      else
      this.messageService.showErrorToast(error.statusText,this.toastr)
    });
  }
  checkNumber(e, type, question) {
      if (type == 'tel' && question.controlType == 'Date Widget') {
          if (Number(question.value) <= Number(this.datepipe.transform(new Date, 'yyyy'))) {
              return e.keyCode == 101 || e.keyCode == 69 ? false : true
          } else {
              question.value = this.datepipe.transform(new Date(), "yyyy");
              return false;
          }
      } else {
          return e.keyCode == 101 || e.keyCode == 69 ? false : true;
      }
  }
  calculateTableArithmetic(cellQ: any, columnIndex: number, rowIndex: number, tableModel: IQuestionModel[]) {
      //-------------calculation using features array-------------------------
      let cellEventSource = tableModel[rowIndex][Object.keys(tableModel[rowIndex])[columnIndex]];
      let fresult: number = null;
      let cells = this.questionFeaturesArray[cellEventSource.columnName];
      if (cells) {
          for (let cell of cells) {
              if (typeof cell == "object" && cell.features != null && cell.features.includes("exp:") && cell.features.includes("{" + cellEventSource.columnName + "}")) {
                  for (let feature of cell.features.split("@AND")) {
                      switch (feature.split(":")[0]) {
                          case "exp":
                              for (let cols of feature.split(":")[1].split("&")) {
                                  let arithmeticExpression = feature.split(":")[1];
                                  let result = this.engineUtilsProvider.resolveExpression(arithmeticExpression, this.questionMap);
                                  if (result != null && result != "NaN" && cell.type == "tel") {
                                      fresult = parseInt(result as string);
                                      cell.value = fresult;
                                  }
                                  if (fresult == null) {
                                      cell.value = null;
                                  }
                              }
                              break;
                      }
                  }
              }
          }
      }
      let constraintsCells = this.constraintsArray[cellEventSource.columnName];
        if (constraintsCells) {
            for (let constraintsCell of constraintsCells) {
                if ((constraintsCell.controlType == 'cell') && (constraintsCell.constraints != null) && (constraintsCell.constraints.includes('exp:'))) {
                    for (let c of constraintsCell.constraints.split('@AND')) {
                        switch (c.split(":")[0]) {
                            case 'exp':
                                {
                                    let exp = c.split(":")[1]
                                    let rr = this.engineUtilsProvider.resolveExpression(exp, this.questionMap)
                                    if (parseInt(rr) == 0) {
                                        // if in some devices data is not clear make sure to increase delay time
                                        setTimeout(() => {
                                            cellEventSource.value = null;
                                        }, 500)
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
  }
  numberInput(evt, question) {
      if (question.type == "tel") {
          let pass = /[4][8-9]{1}/.test(evt.charCode) || /[5][0-7]{1}/.test(evt.charCode);
          if (!pass) {
              return false;
          }
	  } else if( question.type == "doubledecimal") {
					  var charCode = (evt.which) ? evt.which : evt.keyCode
					  console.log(charCode,evt.srcElement.value.length,evt.srcElement.value.split('.'))
			if (charCode == 46 && evt.srcElement.value.length == 0) {
				return false;
			}
             if (charCode == 46 && evt.srcElement.value.split('.').length> 1) {
				return false;
			}
			 if (evt.srcElement.value.split('.').length > 1 && evt.srcElement.value.split('.')[1].length == 2
			) {
				return false;
			}
		     if (charCode != 46 && charCode > 31 && (charCode < 48 || charCode > 57))
			return false;
		
		return true;
	  
	  } else {
          // return event.charCode >= 97 && event.charCode <= 122 || event.charCode >= 65 && event.charCode <= 90 || event.charCode == 32
          return true;
      }
  }
  compute(focusedQuestion: IQuestionModel) {
      if (focusedQuestion.type == "tel" || focusedQuestion.type == "doubledecimal"  ) {
          let dependencies = this.questionFeaturesArray[focusedQuestion.columnName];
          if (dependencies) {
              for (let question of dependencies) {
                  if (question.controlType == "textbox" && question.features != null && question.features.includes("exp:")) {
                      for (let feature of question.features.split("@AND")) {
                          switch (feature.split(":")[0]) {
                              case "exp":
                                  let expression = feature.split(":")[1];
                                
                                      let result = this.engineUtilsProvider.resolveExpression(expression, this.questionMap);
                                      if (result != null && result != "NaN" && result !="null") question.value = String(result);
                                      else question.value = null;
                                  break;
                          }
                      }
                  }
                  if(question.features != null){
                    this.compute(question);
                }
              }
             
          }
          if (this.beginRepeatArray[focusedQuestion.columnName]) this.validateBeginRepeat(focusedQuestion);
      }
  }
  validateBeginRepeat(focusedQuestion) {
      if (this.beginRepeatArray[focusedQuestion.columnName]) {
          let bgQuestion = this.beginRepeatArray[focusedQuestion.columnName];
          let dependentQuestion = this.questionMap[focusedQuestion.columnName];
          if (dependentQuestion.value != null && dependentQuestion.value > 0) {
              bgQuestion.beginrepeatDisableStatus = false;
              this.setRenderDefault(true, bgQuestion);
          }
          if (dependentQuestion.value == null || dependentQuestion.value == 0) {
              while (bgQuestion.beginRepeat.length > 1) {
                  bgQuestion.beginRepeat.pop();
              }
              for (let i = 0; i < bgQuestion.beginRepeat.length; i++) {
                  for (let j = 0; j < bgQuestion.beginRepeat[i].length; j++) {
                      bgQuestion.beginRepeat[i][j].value = null;
                  }
              }
              bgQuestion.beginrepeatDisableStatus = true;
          } else {
              let diff = bgQuestion.beginRepeat.length - dependentQuestion.value;
              while (diff >= 1) {
                  bgQuestion.beginRepeat.pop();
                  diff--;
              }
          }
          if (bgQuestion.beginRepeat.length == 1) {
              bgQuestion.beginRepeatMinusDisable = true;
          } else {
              bgQuestion.beginRepeatMinusDisable = false;
          }
      }
  }
  setRenderDefault(status: boolean, bgquestion: IQuestionModel) {
      let beginrepeat = bgquestion.beginRepeat;
      for (let i = 0; i < beginrepeat.length; i++) {
          for (let j = 0; j < beginrepeat[i].length; j++) {
              if (beginrepeat[i][j].controlType == "dropdown") {
                  if (beginrepeat[i][j].features && beginrepeat[i][j].features.includes("render_default")) {
                      if (status) {
                          beginrepeat[i][j].value = Number(beginrepeat[i][j].defaultValue);
                      } else {
                          beginrepeat[i][j].value = null;
                      }
                  }
              }
          }
      }
  }
  formatDate(date) {
      var d = new Date(date),
          month = "" + (d.getMonth() + 1),
          day = "" + d.getDate(),
          year = d.getFullYear();
      if (month.length < 2) month = "0" + month;
      if (day.length < 2) day = "0" + day;
      return [day, month, year].join("-");
  }
  checkRelevance(question: IQuestionModel) {

      if (this.questionDependencyArray[question.columnName + ":" + question.key + ":" + question.controlType + ":" + question.label] != null)
          for (let q of this.questionDependencyArray[question.columnName + ":" + question.key + ":" + question.controlType + ":" + question.label]) {
            
              if(q.relevance){
                let arithmeticExpression: String = this.engineUtilsProvider.expressionToArithmeticExpressionTransfomerForRelevance(q.relevance, this.questionMap);
                let rpn: String[] = this.engineUtilsProvider.transformInfixToReversePolishNotationForRelevance(arithmeticExpression.split(" "));
                let isRelevant = this.engineUtilsProvider.arithmeticExpressionResolverForRelevance(rpn);
                q.tempFinalizedMandatory = false;
                q.tempSaveMandatory = false;
             
                if (isRelevant) {
                    q.displayComponent = isRelevant;
                    q.disabled = false
                    q.dependecy = true;
                    if (q.finalizeMandatory && isRelevant) q.tempFinalizedMandatory = true;
                    if (q.saveMandatory && isRelevant) q.tempSaveMandatory = true;
                } else {
                    q.displayComponent = false;
                    q.disabled = true
                    q.value = null;
                    if (q.controlType == "file" || q.controlType == "mfile") q.attachedFiles = [];
                    q.dependecy = false;
                    q.duplicateFilesDetected = false;
                    q.errorMsg = null;
                    q.wrongFileExtensions = false;
                    q.fileSizeExceeds = false;
                    if (q.finalizeMandatory && !isRelevant) q.tempFinalizedMandatory = false;
                    if (q.saveMandatory && !isRelevant) q.tempSaveMandatory = false;
                }
              }
         
           
          }
   
  }
  clearFeatureFilters(question: IQuestionModel) {
      if (this.questionFeaturesArray[question.columnName + ":" + question.key + ":" + question.controlType + ":" + question.label] != null)
          for (let q of this.questionFeaturesArray[question.columnName + ":" + question.key + ":" + question.controlType + ":" + question.label]) {
              for (let feature of q.features.split("@AND")) {
                  switch (feature) {
                      case "area_group":
                      case "filter_single":
                      case "filter_multiple":
                          q.value = null;
                          break;
                  }
              }
          }
  }
  syncGroup(question: IQuestionModel, parentQuestion: IQuestionModel, event) {
      if (question.features == null) return;
      for (let feature of question.features.split("@AND")) {
          switch (feature.split(":")[0].trim()) {
              case "date_sync":
                  {
                      let groupQuestions = "";
                      for (let f of feature.split(":")[1].split("&")) {
                          groupQuestions = groupQuestions + f + ",";
                      }
                      groupQuestions = groupQuestions.substring(0, groupQuestions.length - 1);
                      switch (question.controlType) {
                          case "Date Widget":
                              {
                                  if (question.value != null) {
                                      for (let qcolname of groupQuestions.split(",")) {
                                          let groupQuestion: IQuestionModel;
                                          if (parentQuestion == null) groupQuestion = this.questionMap[qcolname] as IQuestionModel;
                                          else if (parentQuestion.controlType == "beginrepeat") {
                                              let rowIndexOfQuestion = question.columnName.split("-")[1];
                                              let questions: IQuestionModel[] = parentQuestion.beginRepeat[rowIndexOfQuestion];
                                              for (let ques of questions) {
                                                  if (ques.columnName == qcolname) {
                                                      groupQuestion = this.questionMap[ques.columnName] as IQuestionModel;
                                                      break;
                                                  }
                                              }
                                          }
                                          switch (groupQuestion.controlType) {
                                              case "textbox":
                                                  {
                                                      let dt1 = new Date();
                                                      let dt2 = new Date(question.value);
                                                      var diff = (dt1.getTime() - dt2.getTime()) / 1000;
                                                      diff /= 60 * 60 * 24;
                                                      let yearDiff = Math.abs(Math.round(diff / 365.25));
                                                      groupQuestion.value = String(yearDiff);
                                                  }
                                                  break;
                                              case "dropdown":
                                                  {
                                                      let dt1 = new Date();
                                                      let dt2 = new Date(question.value);
                                                      let diff = (dt1.getTime() - dt2.getTime()) / 1000;
                                                      diff /= 60 * 60 * 24;
                                                      let yearDiff = Math.abs(Math.round(diff / 365.25));
                                                      let enteredValue = yearDiff;
                                                      for (let option of groupQuestion.options) {
                                                          let start: number;
                                                          let end: number;
                                                          if ((option["value"] as String).includes("-")) {
                                                              start = parseInt(option["value"].split("-")[0]);
                                                              end = parseInt(option["value"].split("-")[1]);
                                                              if (enteredValue >= start && enteredValue <= end) {
                                                                  groupQuestion.value = option["key"];
                                                                  break;
                                                              }
                                                          } else {
                                                              start = parseInt(option["value"].split(" ")[0]);
                                                              if (enteredValue >= start) {
                                                                  groupQuestion.value = option["key"];
                                                                  break;
                                                              }
                                                          }
                                                      }
                                                  }
                                          }
                                      }
                                  }
                              }
                              break;
                          case "textbox":
                              {
                                  for (let qcolname of groupQuestions.split(",")) {
                                      let groupQuestion: IQuestionModel = this.questionMap[qcolname] as IQuestionModel;
                                      // if (parentQuestion == null)
                                      //   groupQuestion = this.questionMap[qcolname] as IQuestionModel
                                      // else if (parentQuestion.controlType == 'beginrepeat') {
                                      //   let rowIndexOfQuestion = question.columnName.split('-')[1]
                                      //   let questions: IQuestionModel[] = parentQuestion.beginRepeat[rowIndexOfQuestion]
                                      //   for (let ques of questions) {
                                      //     if (ques.columnName == qcolname) {
                                      //       groupQuestion = this.questionMap[ques.columnName] as IQuestionModel
                                      //       break;
                                      //     }
                                      //   }
                                      // }
                                      if (question.value == null || question.value == "") {
                                          groupQuestion.value = null;
                                      }
                                      switch (groupQuestion.controlType) {
                                          case "dropdown":
                                              {
                                                  let enteredValue = question.value;
                                                  for (let option of groupQuestion.options) {
                                                      let start: number;
                                                      let end: number;
                                                      if ((option["value"] as String).includes("-")) {
                                                          start = parseInt(option["value"].split("-")[0]);
                                                          end = parseInt(option["value"].split("-")[1]);
                                                          if (parseInt(enteredValue) >= start && parseInt(enteredValue) <= end) {
                                                              groupQuestion.value = option["key"];
                                                              break;
                                                          }
                                                      } else {
                                                          start = parseInt(option["value"].split(" ")[0]);
                                                          if (parseInt(enteredValue) >= start) {
                                                              groupQuestion.value = option["key"];
                                                              break;
                                                          }
                                                      }
                                                  }
                                                  if (question.value == null || question.value == "") {
                                                      groupQuestion.value = null;
                                                  }
                                              }
                                              break;
                                          case "Date Widget":
                                              {
                                                  if (event == "" || (event.keyCode != undefined && event.keyCode === 8)) {
                                                      groupQuestion.value = null;
                                                  }
                                              }
                                              break;
                                      }
                                  }
                              }
                              break;
                      }
                      break;
                  }
              case "area_group":
              case "filter_single":
                  {
                      if (question.features != null && (question.features.includes("area_group") || question.features.includes("filter_single"))) {
                          let groupQuestions = feature.split(":")[1];
                          let childLevelQuestion = this.questionMap[groupQuestions];
                          for (let option of childLevelQuestion.options) {
                              if (option["parentId"] == question.value) {
                                  option["visible"] = true;
                              } else {
                                  option["visible"] = false;
                              }
                          }
                          childLevelQuestion.value = null;
                      }
                  }
                  break;
              case "filter_multiple":
                  {
                      if (question.features != null && question.features.includes("filter_multiple")) {
                          let groupQuestions = feature.split(":")[1];
                          let childLevelQuestion = this.questionMap[groupQuestions];
                          for (let option of childLevelQuestion.options) {
                              option["visible"] = false;
                              for (let parentId of option["parentIds"]) {
                                  if (parentId == question.value) {
                                      option["visible"] = true;
                                      break;
                                  }
                              }
                          }
                          childLevelQuestion.value = null;
                      }
                  }
                  break;
              case "dropdown_auto_select":
                  {}
          }
      }
  }
  onPaste(question: any) {
      setTimeout(() => {
          question.value = null;
      }, 0);
  }
  checkFieldContainsAnyvalue(data: any) {
          for (let index = 0; index < Object.keys(this.data).length; index++) {
              for (let j = 0; j < this.data[Object.keys(this.data)[index]].length; j++) {
                  let subSections = this.data[Object.keys(this.data)[index]][0];
                  for (let qs = 0; qs < Object.keys(subSections).length; qs++) {
                      for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
                          let question: IQuestionModel = subSections[Object.keys(subSections)[qs]][q];
                          console.log(question.label);
                          switch (question.controlType) {
                              case "textbox":
                                  if (question.value != null && (question.value as string).trim() != "") return true;
                                  break;
                              case "segment":
                              case "dropdown":
                                  if (question.value != null && question.value != "") return true;
                                  break;
                              case "Time Widget":
                                  if (question.value != null && question.value != "") return true;
                                  break;
                              case "Date Widget":
                                  if (question.value != null && question.value != "") return true;
                                  break;
                              case "checkbox":
                                  if (question.value != null && question.value != "") return true;
                                  break;
                              case "tableWithRowWiseArithmetic":
                                  {
                                      let tableData = question.tableModel;
                                      for (let i = 0; i < tableData.length; i++) {
                                          for (let j = 0; j < Object.keys(tableData[i]).length; j++) {
                                              let cell = tableData[i][Object.keys(tableData[i])[j]];
                                              if (typeof cell == "object") {
                                                  if (cell.value != null && cell.value.trim() != "") return true;
                                                  break;
                                              }
                                          }
                                      }
                                  }
                                  break;
                              case "beginrepeat":
                                  let beginrepeat = question.beginRepeat;
                                  let beginrepeatArray: any[] = [];
                                  let beginrepeatMap: {} = {};
                                  for (let i = 0; i < beginrepeat.length; i++) {
                                      beginrepeatMap = {};
                                      for (let j = 0; j < beginrepeat[i].length; j++) {
                                          let colName = (beginrepeat[i][j].columnName as String).split("-")[3];
                                          beginrepeatMap[colName] = beginrepeat[i][j].value;
                                          console.log("begin-repeat", beginrepeat[i][j]);
                                          switch (beginrepeat[i][j].controlType) {
                                              case "textbox":
                                                  if (beginrepeat[i][j].value != null && (beginrepeat[i][j].value as string).trim() != "") return true;
                                                  break;
                                              case "dropdown":
                                              case "segment":
                                                  if (beginrepeat[i][j].value != null && beginrepeat[i][j].valu != "") return true;
                                                  break;
                                              case "Time Widget":
                                                  if (beginrepeat[i][j].value != null && beginrepeat[i][j].value != "") return true;
                                                  break;
                                              case "Date Widget":
                                                  if (beginrepeat[i][j].value != null && beginrepeat[i][j].value != "") return true;
                                                  break;
                                              case "checkbox":
                                                  if (beginrepeat[i][j].value != null && beginrepeat[i][j].value != "") return true;
                                                  break;
                                          }
                                      }
                                      beginrepeatArray.push(beginrepeatMap);
                                  }
                                  break;
                          }
                      }
                  }
              }
          }
          return false;
      }

  drawDependencyGraph(expression: String, question: IQuestionModel) {
      for (let str of expression.split("}")) {
          let expressions: String[] = str.split(":");
          for (let i = 0; i < expressions.length; i++) {
              let exp: String = expressions[i];
              switch (exp) {
                  case "optionEquals":
                  case "optionEqualsMultiple":
                      {
                          let dColName: any = expressions[i - 1];
                          if (question.dependecy && this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] == undefined) {
                              this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = [this.questionMap[question.columnName]];
                          } else if (question.dependecy == true) {
                              let a = this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label];
                              let keyFound = false
                              for(let dps of a){
                                  if(dps.columnName == question.columnName)
                                  keyFound = true
                              }
                              if(!keyFound){
                                a.push(this.questionMap[question.columnName]);
                                this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = a;

                              }
                           
                          }
                          i = i + 2;
                      }
                      break;
                  case "textEquals":
                  case "equals":
                  case "greaterThan":
                  case "greaterThanEquals":
                  case "lessThan":
                  case "lessThanEquals":
                      {
                          let dColName: any = expressions[i - 1];
                          if (question.dependecy && this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] == undefined) {
                              this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = [this.questionMap[question.columnName]];
                          } else if (question.dependecy == true) {
                              let a = this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label];
                              let keyFound = false
                              for(let dps of a){
                                  if(dps.columnName == question.columnName)
                                  keyFound = true
                              }
                              if(!keyFound){
                                a.push(this.questionMap[question.columnName]);
                                this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = a;

                              }
                            }
                          i = i + 1;
                      }
                      break;
              }
          }
      }
  }
  setupDefaultSettingsAndConstraintsAndFeatureGraph(question: IQuestionModel): IQuestionModel {
          if (question.defaultSettings != null) {
              for (let settings of question.defaultSettings.split(",")) {
                  switch (settings.split(":")[0]) {
                      case "current_date":
                          question.value = this.datepipe.transform(new Date(), "yyyy-MM-dd");
                          break;
                      case "prefetchNumber":
                          question.value = (settings.split(":")[1])
                          break;
                      case "prefetchText":
                          question.value = new String(settings.split(":")[1]);
                          break;
                      case "prefetchDropdownWithValue":
                          question.value = new Number(settings.split(":")[1]);
                          break;
                      case "disabled":
                          question.disabled = true;
                          break;
                      case "prefetchDate":
                          if (settings.split(":")[1] == "current_date") {
                              if (question.value != null) {
                                  if (question.value.date == null) {
                                      let fullDate = question.value.split('-')
                                      question.value = {
                                          date: {
                                              year: Number(fullDate[0]),
                                              month: Number(fullDate[1]),
                                              day: Number(fullDate[2])
                                          }
                                      }
                                  }
                              } else if (question.value == null) {
                                  let fullDate = this.datepipe.transform(new Date(), "yyyy-MM-dd").split('-')
                                  question.value = {
                                      date: {
                                          year: Number(fullDate[0]),
                                          month: Number(fullDate[1]),
                                          day: Number(fullDate[2])
                                      }
                                  }
                              } else if (question.value.date == null) {
                                  let fullDate = question.value.split('-')
                                  question.value = {
                                      date: {
                                          year: Number(fullDate[0]),
                                          month: Number(fullDate[1]),
                                          day: Number(fullDate[2])
                                      }
                                  }
                              }
                          }
                          break;
                  }
              }
          }
          if (question.constraints != null) {
              for (let settings of question.constraints.split("@AND")) {
                  switch (settings.split(":")[0].trim()) {
                      case "maxLength":
                          question.maxLength = parseInt(settings.split(":")[1]);
                          break;
                      case "minLength":
                          question.minLength = parseInt(settings.split(":")[1]);
                          break;
                      case "maxValue":
                          question.maxValue = parseInt(settings.split(":")[1]);
                          break;
                      case "minValue":
                          question.minValue = parseInt(settings.split(":")[1]);
                          break;
                      case 'lessThan':
                      case 'lessThanEquals':
                      case 'greaterThan':
                      case 'greaterThanEquals':
                      case 'exp':
                          if (this.constraintsArray[question.columnName] == null) this.constraintsArray[question.columnName] = [question];
                          else {
                              let constraints = this.constraintsArray[question.columnName];
                              constraints.push(question);
                              this.constraintsArray[question.columnName] = constraints;
                          }
                          break;
                      case "limit_bg_repeat":
                          question.limit_bg_repeat = settings;
                          let dcolName = settings.split(":")[1];
                          question.bgDependentColumn = dcolName;
                          this.beginRepeatArray[dcolName] = question;
                          break;
                  }
              }
          }
          if (question.features != null) {
              for (let features of question.features.split("@AND")) {
                  switch (features.split(":")[0]) {
                      case "exp":
                          let exp = features.split(":")[1] as String;
                          let str = exp.split("");
                          for (let i = 0; i < str.length; i++) {
                              if (str[i] == "$") {
                                  let qName = "";
                                  for (let j = i + 2; j < str.length; j++) {
                                      if (str[j] == "}") {
                                          i = j;
                                          break;
                                      }
                                      qName = qName + str[j];
                                  }
                                  if (this.questionFeaturesArray[qName] == undefined) this.questionFeaturesArray[qName] = [question];
                                  else {
                                      let a = this.questionFeaturesArray[qName];
                                      a.push(question);
                                      this.questionFeaturesArray[qName] = a;
                                  }
                              }
                          }
                          break;
                  }
              }
          }
          return question;
      }
      ///resolver methods to handle Relevance using Djisktra Shunting Algorithm
     
  // onDateChanged(question1: any, question2: any, event: IMyDateModel) {
    onDateChanged(question1: any, question2: any, event: any) {
      question1.value = event.formatted;
      this.syncGroup(question1, question2, null);
  }
  open(question: any) {
      // if (!this.disableStatus) {
      //     const amazingTimePicker = this.atp.open();
      //     amazingTimePicker.afterClose().subscribe(time => {
      //         question.value = time;
      //         if (question.constraints != null && question.constraints != "" && question.controlType == "Time Widget") {
      //             for (let settings of question.constraints.split("@AND")) {
      //                 switch (settings.split(":")[0]) {
      //                     case "lessThan":
      //                     case "greaterThan":
      //                         {
      //                             if (question.value != null && this.questionMap[question.constraints.split(":")[1]].value != null) {
      //                                 this.questionMap[question.constraints.split(":")[1]].value = null
      //                             }
      //                         }
      //                 }
      //             }
      //         }
      //     });
      // }
  }
  checkMinMax(question1, question2) {
      if (question1.value != null && question1.constraints != "" && question1.controlType == "textbox") {
          if (question1.maxValue != null && Number(question1.value) >= question1.maxValue) {
              question1.value = null;
              this.syncGroup(question1, question2, null);
              return (question1.value = null);
          } else if (question1.maxValue != null && Number(question1.value) <= question1.minValue) {
              question1.value = null;
              this.syncGroup(question1, question2, null);
              return (question1.value = null);
          }
      } else if (question1.value != null && question1.constraints != "" && question1.controlType == "Time Widget") {
          for (let settings of question1.constraints.split("@AND")) {
              switch (settings.split(":")[0].trim()) {
                  case "greaterThan":
                      {
                          if (this.questionMap[settings.split(":")[1]].value != null && question1.value) {
                              let timeOfConstraint = this.questionMap[settings.split(":")[1]].value;
                              let timeOfActiveQ = question1.value;
                              let hourOfConstraint = parseInt(timeOfConstraint.split(":")[0]);
                              let minuteOfConstraint = parseInt(timeOfConstraint.split(":")[1]);
                              let hourOfActiveQ = parseInt(timeOfActiveQ.split(":")[0]);
                              let minuteOfActiveQ = parseInt(timeOfActiveQ.split(":")[1]);
                              // passing year, month, day, hourOfA and minuteOfA to Date()
                              let dateOfConstraint: Date = new Date(2010, 6, 15, hourOfConstraint, minuteOfConstraint);
                              let dateOfActiveQ: Date = new Date(2010, 6, 15, hourOfActiveQ, minuteOfActiveQ);
                              if (dateOfConstraint > dateOfActiveQ) {
                                  setTimeout(() => {
                                      question1.value = null;
                                  }, 100)
                              }
                          }
                      }
                  case "lessThan":
                      {
                          if (this.questionMap[settings.split(":")[1]].value != null && question1.value) {
                              let timeOfConstraint = this.questionMap[settings.split(":")[1]].value;
                              let timeOfActiveQ = question1.value;
                              let hourOfConstraint = parseInt(timeOfConstraint.split(":")[0]);
                              let minuteOfConstraint = parseInt(timeOfConstraint.split(":")[1]);
                              let hourOfActiveQ = parseInt(timeOfActiveQ.split(":")[0]);
                              let minuteOfActiveQ = parseInt(timeOfActiveQ.split(":")[1]);
                              // passing year, month, day, hourOfA and minuteOfA to Date()
                              let dateOfConstraint: Date = new Date(2010, 6, 15, hourOfConstraint, minuteOfConstraint);
                              let dateOfActiveQ: Date = new Date(2010, 6, 15, hourOfActiveQ, minuteOfActiveQ);
                              if (dateOfActiveQ > dateOfConstraint) {
                                  setTimeout(() => {
                                      question1.value = null;
                                  }, 100)
                              }
                          }
                      }
              }
          }
      }
  }
  onFileChange(event, question: IQuestionModel) {
      if (event.target.files) {
          let files = event.target.files;
          console.log(files);
          let totalFileSize = 0;
          question.duplicateFilesDetected = false;
          question.wrongFileExtensions = false;
          let testDuplicate: boolean = false;
          let fileSizeLimit: number = 100;
          if (question.controlType == "mfile") {
              for (let j = 0; j < question.attachedFiles.length; j++) {
                  if (question.attachedFiles.length > 1 && question.attachedFiles[question.attachedFiles.length - 1] && event.target.files[event.target.files.length - 1].name == question.attachedFiles[j].fileName) {
                      // duplicate file being attached
                      // show error msg and return
                      testDuplicate = true;
                  }
              }
          } else {
              question.attachedFiles = [];
          }
          if (files.length) {
              for (let a = 0; a < files.length; a++) {
                  let file = files[a];
                  let extension = file.name.split(".")[file.name.split(".").length - 1];
                  if (extension.toLowerCase() == "pdf" || extension.toLowerCase() == "doc" || extension.toLowerCase() == "docx" || extension.toLowerCase() == "xls" || extension.toLowerCase() == "xlsx") {
                      let reader = new FileReader();
                      reader.readAsDataURL(file);
                      reader.onload = () => {
                          let f = {
                              base64: (reader.result as String).split(",")[1],
                              fileName: file.name,
                              fileSize: file.size,
                              columnName: question.columnName,
                              attachmentId: null,
                              fileType: extension.toLowerCase()
                          };
                          if (testDuplicate) {
                              question.errorMsg = file.name + " :File has been already attached!!";
                          } else if (Math.round(files[a].size / 1024) >= fileSizeLimit) {
                              question.errorMsg = "Can not upload!! size limit exceeds (" + fileSizeLimit + " kb) for " + file.name + " !!";
                              question.fileSizeExceeds = true;
                          } else {
                              question.errorMsg = null;
                              question.attachedFiles.push(f as any);
                              question.fileSizeExceeds = false;
                          }
                      };
                  } else {
                      question.wrongFileExtensions = true;
                     
                  }
               
              }
          } else {
              question.duplicateFilesDetected = true;
          }
      } else {
          question.attachedFiles = [];
      }
  }
  deleteFile(fIndex, question) {
          question.attachedFiles.splice(fIndex, 1);
          question.errorMsg = null;
      }
      // checkBRLimitSize(question) {}
  checkConstraints(fq: IQuestionModel) {
    
      let ccd = this.constraintsArray[fq.columnName as any]
    //   if (ccd && fq.value && fq.value.length > 0 && parseInt(fq.value) != 0) {
        if (ccd) {
          for (let qq of ccd) {
              if ((qq.controlType == 'textbox' || qq.controlType == 'cell') && (qq.constraints != null) && (qq.constraints.includes('exp:'))) {
                  for (let c of qq.constraints.split('@AND')) {
                    switch (c.split(":")[0].replace(/\s/g, '')) {
                          case 'exp':
                              {

                                if((fq.value == null || fq.value=="") && fq.tempFinalizedMandatory){
                                    fq.requiredMessage ="This field is required!!";
                                    fq.showErrMessage = false
                                    setTimeout(() => {
                                        fq.showErrMessage = false
                                    },500)
                                } else{
                                    let exp = c.split(":")[1]
                                    let rr = this.constraintTokenizer.resolveExpression(exp, this.questionMap,"constraint")
                                    if (rr != null && rr != NaN && rr != "null" && rr != "NaN") {
                                    if (parseInt(rr) == 0) {
                                        // if in some devices data is not clear make sure to increase delay time
                                        setTimeout(() => {
                                            fq.showErrMessage = true
                                            this.checkRelevance(fq);
                                            this.clearFeatureFilters(fq);
                                            this.compute(fq);
                                             this.validateBeginRepeat(fq.columnName);
                                            this.calculateScore(fq)
                                        }, 500)
                                    }else{
                                      this.removeColor(fq.columnName)
                                      fq.showErrMessage = false
                                    }
                                  }
                                }
                                
                              }
                              break;
                        
                      }
                  }
              }
          }
      }

    
    

  }

// removeColor(key: any) {
//         if (this.tempCaryId != null) {
//         let temp = document.getElementById(this.tempCaryId + "");
//         if (temp != null && temp != undefined) document.getElementById(this.tempCaryId + "").style.removeProperty("outline");
//         this.tempCaryId = null;
//         }
//         if (key != null && key != "" && key != undefined) {
//         let temp = document.getElementById(key + "");
//         if (temp != null && temp != undefined) document.getElementById(key + "").style.removeProperty("outline");
//         this.tempCaryId = key;
//         }
// }

removeColor(key: any) {
    this.questionMap[key].requiredMessage=null
    if (this.tempCaryId != null) 
    { 
    let temp = document.getElementById(this.tempCaryId + "")
    if (temp != null && temp != undefined)
    {
    temp.style.removeProperty("border");
    temp.style.removeProperty("border-style");
    temp.style.removeProperty("border-color");
    this.tempCaryId = null;
    }
    }
    if (key != null && key != '' && key != undefined) 
    {
    let temp = document.getElementById(key + "")
    if (temp != null && temp != undefined)
    {
    temp.style.removeProperty("border-width");
    temp.style.removeProperty("border-style");
    temp.style.removeProperty("border-color");
    this.tempCaryId = key;
    } 
    }
    };


errorColor(sectionHeading: any, key: any) {
          
    if (this.tempCaryId != null) this.removeColor(this.tempCaryId);
                if (key != null && key != "" && key != undefined) {
             

                setTimeout(() => {
                let eleId = document.getElementById(key + "");
                if (eleId != null) {
               
                // eleId.style.setProperty("border", "#FF0000 solid 1px", "important");
                // let toscr = eleId.parentNode.parentElement.offsetTop;
                // document.getElementsByClassName("scroll-content")[3].scrollTo(0, toscrl);
                this.tempCaryId = key;
                } else {
                // document.getElementsByClassName("scroll-content")[3].scrollTop;
                }
                }, 50);
                } else {
                // document.getElementsByClassName("scroll-content")[3].scrollTop = 0;
                }
}

    checkQuestionSizeBasedOnRelevance(questions:IQuestionModel[]){
                for(let q of questions){
                    if(q.displayComponent == true){
                        return true
                    }
                }
                return false
    }

    calculateScore(question:IQuestionModel){
        if(this.scoreKeyMapper[question.columnName]){
            for(let impactedScoreHolders of this.scoreKeyMapper[question.columnName]){
                let result =  this.commonsEngineProvider.calculateScore(this.questionMap[impactedScoreHolders],this.questionMap)
            
                this.questionMap[impactedScoreHolders].value = result
            }
        }
    }

    drawScoreDependencyGraph(question : IQuestionModel){
            let scoreExpChars = question.scoreExp.split("");
            for(let i = 0 ; i < scoreExpChars.length;i++){
                let ch: string = scoreExpChars[i] as string;
                if (ch == '$') {
                  let qName = "";
                  for (let j = i + 2; j < scoreExpChars.length; j++) {
                    if (scoreExpChars[j] == "}") {
                      i = j;
                      break;
                    }
                    qName = qName + (scoreExpChars[j]);
                }
                    if(this.scoreKeyMapper[qName])
                         this.scoreKeyMapper[qName].push(question.columnName)
                    else{
                        this.scoreKeyMapper[qName] = []
                        this.scoreKeyMapper[qName].push(question.columnName)
                         }
                  }
            }
    
}


checkRelevanceForEachQuestion(){
    for (let questionKey of this.dataSharingService.getKeys(this.questionMap)) {
      let question = this.questionMap[questionKey];

      switch (question.controlType) {
        case "sub-score-keeper":
        case "score-keeper":
        case "score-holder":
            question.scoreExp != null ? this.drawScoreDependencyGraph(question) : null ;     
        break;
          case "dropdown":
          case "segment":
              if (question.groupType == "area_group" || question.groupType == "filter_single" || question.groupType == "filter_multiple") {
                  if (question.groupQuestions != null) {
                      let childLevelQuestion = this.questionMap[question.groupQuestions];
                      let optionCount = 0;
                      for (let option of childLevelQuestion.options) {
                          if (option["parentId"] == question.value) {
                              option["visible"] = true;
                          } else {
                              option["visible"] = false;
                              optionCount++;
                          }
                      }
                      if (optionCount == childLevelQuestion.options.length) {
                          childLevelQuestion.constraints = "disabled";
                      } else {
                          childLevelQuestion.constraints = "";
                      }
                  }
              }
              this.checkRelevance(question)
              break;
              case "table":
              case "tableWithRowWiseArithmetic":
                  question.displayComponent = true;
                  for (let row = 0; row < question.tableModel.length; row++) {
                      for (let column = 0; column < Object.keys(question.tableModel[row]).length; column++) {
                          let value = question.tableModel[row][Object.keys(question.tableModel[row])[column]];
                          if (typeof value == "object") {
                              let cell = value;
                              this.checkRelevance(cell)
                          }
                      }
                  }
                  break;
              case 'beginrepeat':
                  for (let index = 0; index < question.beginRepeat.length; index++) {
                      let beginRepeatQuestions: IQuestionModel[] = question.beginRepeat[index];
                      for (let beginRepeatQuestion of beginRepeatQuestions) {
                          this.checkRelevance(beginRepeatQuestion)
                        }
                  }
                  break;
                  default:
                  this.checkRelevance(question)
                  break;
      }
  }
  }

  removeFromDependencyGraph(expression: String, question: IQuestionModel) {
    if(this.questionMap[question.parentColumnName] && this.questionMap[question.parentColumnName].controlType == 'beginrepeat'){
        delete this.questionDependencyArray[question.columnName]
        return 
    }
    for (let str of expression.split("}")) {
        let expressions: String[] = str.split(":");
        for (let i = 0; i < expressions.length; i++) {
            let exp: String = expressions[i];
            switch (exp) {
                case "optionEquals":
                case "optionEqualsMultiple":
                    {
                        let dColName: any = expressions[i - 1];
                        if (question.dependecy && this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] == undefined) {
                            this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = [this.questionMap[question.columnName]];
                        } else if (question.dependecy == true) {
                            let a = this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label];
                            let keyFoundIndex = -1
                            for(let i=0;a.length;i++){
                                if(a[i].columnName == question.columnName){
                                    // remove the object from dependent key. if array size is 1, remove the key itself
                                    if(a.length == 1){
                                        delete this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label]
                                    }else{
                                        keyFoundIndex = i
                                        break
                                    }
                                }
                        
                            }
                            if(keyFoundIndex>-1){
                                a.splice(keyFoundIndex,1)
                            }
                        
                        }
                        i = i + 2;
                    }
                    break;
                case "textEquals":
                case "equals":
                case "greaterThan":
                case "greaterThanEquals":
                case "lessThan":
                case "lessThanEquals":
                    {
                        let dColName: any = expressions[i - 1];
                        if (question.dependecy && this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] == undefined) {
                            this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = [this.questionMap[question.columnName]];
                        } else if (question.dependecy == true) {
                            let a = this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label];
                            let keyFound = false
                            for(let dps of a){
                                if(dps.columnName == question.columnName)
                                keyFound = true
                            }
                            if(!keyFound){
                              a.push(this.questionMap[question.columnName]);
                              this.questionDependencyArray[this.questionMap[dColName].columnName + ":" + this.questionMap[dColName].key + ":" + this.questionMap[dColName].controlType + ":" + this.questionMap[dColName].label] = a;

                            }
                          }
                        i = i + 1;
                    }
                    break;
            }
        }
    }
}

checkFinalizedConstraints(): boolean {
    let wasConstraintFailed = false;
    for (let index = 0; index < Object.keys(this.data).length; index++) {
      this.sectionMap.set(Object.keys(this.data)[index], this.data[Object.keys(this.data)[index]]);
      for (let j = 0; j < this.data[Object.keys(this.data)[index]].length; j++) {
        let subSections = this.data[Object.keys(this.data)[index]][0];
        for (let qs = 0; qs < Object.keys(subSections).length; qs++) {
          for (let q = 0; q < subSections[Object.keys(subSections)[qs]].length; q++) {
            let fq: IQuestionModel = subSections[Object.keys(subSections)[qs]][q];
            if (fq.controlType == 'tableWithRowWiseArithmetic' || fq.controlType == 'table') {
              {
                let tableData = fq.tableModel;
                for (let i = 0; i < tableData.length; i++) {
                  for (let j = 0; j < Object.keys(tableData[i]).length; j++) {
                    let cell = tableData[i][Object.keys(tableData[i])[j]];
                    if (typeof cell == "object") {
                      let ccd = this.constraintsArray[cell.columnName as any]
                      if (ccd) {
                        for (let qq of ccd) {
                          if ((qq.controlType == 'textbox' || qq.controlType == 'cell') && (qq.constraints != null) && (qq.constraints.includes('exp:'))) {
                            console.log("question"+qq.columnName)
                            for (let c of qq.constraints.split('@AND')) {
                              switch (c.split(":")[0].replace(/\s/g, '')) {
                                case 'exp':
                                  {
                                    let exp = c.split(":")[1]
                                 
                                    let rr = this.constraintTokenizer.resolveExpression(exp, this.questionMap, "constraint")
                                    if (rr != null && rr != NaN && rr != "null" && rr != "NaN") {
                                      // if in some devices data is not clear make sure to increase delay time
                                      if (parseInt(rr) == 0) {
                                        setTimeout(() => {
                                          cell.showErrMessage = true
                                        }, 500)
                                        if (!wasConstraintFailed) {
                                          wasConstraintFailed = true;
                                          this.errorColor(Object.keys(subSections)[qs],fq.columnName)
                                          this.errorInSection = Object.keys(subSections)[qs]
                                        }
                                      }else{
                                        setTimeout(() => {
                                            cell.showErrMessage = false
                                            this.removeColor(fq.columnName)
                                          }, 500)
                                         
                                      }
                                    }
                                  }
                                  break;
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            } else if(fq.controlType == 'beginrepeat'){
              for (let bgindex = 0; bgindex < fq.beginRepeat.length; bgindex++) {
                let beginRepeatQuestions: IQuestionModel[] = fq.beginRepeat[bgindex];
                for (let beginRepeatQuestion of beginRepeatQuestions) {
                  let ccd = this.constraintsArray[beginRepeatQuestion.columnName as any]
                  if (ccd) {
                    for (let qq of ccd) {
                      if ((qq.controlType == 'textbox' || qq.controlType == 'cell') && (qq.constraints != null) && (qq.constraints.includes('exp:'))) {
                        for (let c of qq.constraints.split('@AND')) {
                          switch (c.split(":")[0].replace(/\s/g, '')) {
                            case 'exp':
                              {
                                let exp = c.split(":")[1]
                                let rr = this.constraintTokenizer.resolveExpression(exp, this.questionMap, "constraint")
                                if (rr != null && rr != NaN && rr != "null" && rr != "NaN") {
                                  // if in some devices data is not clear make sure to increase delay time
                                  if (parseInt(rr) == 0) {
                                    setTimeout(() => {
                                      beginRepeatQuestion.showErrMessage = true
                                    }, 500)
                                    if (!wasConstraintFailed) {
                                      wasConstraintFailed = true;
                                    //   this.errorColor(subSections[Object.keys(subSections)[qs]],fq.columnName)
                                    // this.sectionSelected(subSections[Object.keys(subSections)[qs]], fq.columnName)
                                    //   this.scrollToError(subSections[Object.keys(subSections)[qs]],fq.columnName)
                                    }
                                  }
                                }
                              }
                              break;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }else if(fq.controlType!="heading"  && fq.tempFinalizedMandatory){
                console.log(fq,fq.value)
                if(fq.value == null || fq.value == "" || fq.value=="null" || fq.value == NaN ){
                   
                    fq.requiredMessage ="This field is required!!";
                    fq.showErrMessage = false

                    if (!wasConstraintFailed) {
                        wasConstraintFailed = true;
                        this.errorColor(Object.keys(this.data)[index],fq.columnName)
                        this.errorInSection = Object.keys(this.data)[index]
                        this.errorKey = fq.columnName
                      }
                }else{
                    fq.requiredMessage ="";
                    this.removeColor(fq.columnName)
                    let ccd = this.constraintsArray[fq.columnName as any]
                    if (ccd) {
                      for (let qq of ccd) {
                        if ((qq.controlType == 'textbox' || qq.controlType == 'cell') && (qq.constraints != null) && (qq.constraints.includes('exp:'))) {
                          for (let c of qq.constraints.split('@AND')) {
                            switch (c.split(":")[0].replace(/\s/g, '')) {
                              case 'exp':
                                {
                                  let exp = c.split(":")[1]
                                  let rr = this.constraintTokenizer.resolveExpression(exp, this.questionMap, "constraint")
                                  if (rr != null && rr != NaN && rr != "null" && rr != "NaN") {
                                    // if in some devices data is not clear make sure to increase delay time
                                    if (parseInt(rr) == 0) {
                                      setTimeout(() => {
                                        fq.showErrMessage = true
                                      }, 500)
                                      if (!wasConstraintFailed) {
                                        wasConstraintFailed = true;
                                        this.errorColor(Object.keys(this.data)[index],fq.columnName)
                                        this.errorInSection = Object.keys(this.data)[index]
                                        this.errorKey = fq.columnName
                                      }
                                    }
                                    else{
                                     
                                      setTimeout(() => {
                                          fq.showErrMessage = false
                                          this.removeColor(fq.columnName)
                                        }, 500)
                                    }
                                  }
                                }
                                break;
                            }
                          }
                        }
                      }
                    }
                  
                }   
                
            }else{
             
          }}
        }
      }
    }
    return wasConstraintFailed;
  }

showLists(event){
    if($(window).width() < 767) {
        event.stopPropagation();    
        $(".left-list, .backed-drop").attr("style", "display: block !important"); 
        // $('.mob-left-list').attr("style", "display: none !important");
    }
    
  }
hideLists() {
    if($(window).width() < 767){
    $(".left-list, .backed-drop").attr("style", "display: none !important"); 
    }
}

ngOnDestroy(){
    this.formService.setDraftData(null)
    
}


  changeLanguage(languageKey){
        for(let key of Object.keys(this.questionMap)){
             let question = this.questionMap[key]
             let label = question.languages[languageKey]
             if(label!=null){
                 question.label = label
             }
        }
  }

}
