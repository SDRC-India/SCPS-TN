export class Constants {
    // public static get HOME_URL(): string { return "https://devserver.sdrc.co.in/scpstamilnadu/"; };
    // public static get HOME_URL(): string { return "http://localhost:8080/scpstamilnadu/"; };

    public static get HOME_URL(): string { return "/scpstn/"; };
    public static get API_URL(): string { return "/scpstn/api/"; };
    public static get CMS_URL(): string { return "/cms/"; }
    public static get CONTACT_URL(): string { return '/contact/'; }
    public static get PERFORMANCE_URL(): string { return '/scpstn/bypass/performance/api/'; }
    public static get STATE_NAME():string{return 'Tamil Nadu'}
    public static get QUICK_STATS_NOTE():string{return 'Data displayed is as per the last reporting month .'}
  public static get QUICK_STATS_FILE_NAME_PREFIX(): string { return 'CCI List _'}



    public static defaultImage:string;

    public static get INDEX_COMPUTE_FOOT_NOTE():string{return 'The basis of calculation of indices have changed from TBD'}
  
    static message: IMessages = { 
        checkInternetConnection: "Please check your internet connection.",
        serverError:"Error connecting to server ! Please try after some time.",
        networkError: 'Server error.',
        pleaseWait: 'Please wait..', 
        validUserName: 'Please enter username.',
        validPassword:'Please enter Password.',
        dataClearMsg:'Last user saved data will be erased. Are you sure you want to login?',
        invalidUser:'No data entry facility available for state and national level user.',
        invalidUserNameOrPassword:'Invalid usename or password.',
        syncingPleaseWait: 'Syncing please wait...',
        syncSuccessfull: 'Sync Successful.',
        getForm: 'Fetching forms from server, please wait...',
        warning: 'Warning',
        deleteFrom: 'Do you want to delete the selected record?',
        saveSuccess: 'Saved Successfully.',
        finalizedSuccess: 'Submitted Successfully.',
        fillAtleastOnField: 'Please fill data of atleast one field',
        autoSave: 'Auto save Successfully',
        anganwadiCenter: 'Please select the anganwadi center number.',
        schoolname: 'Please enter the school name.',
        respondentName: 'Please enter the respondent name.',
        womanName: 'Please enter the woman name.',
        errorWhileClearingFile: 'Error while deleting data of previous user.',
        clearingDataPleaseWait: 'Clearing data, please wait...'
      }
}