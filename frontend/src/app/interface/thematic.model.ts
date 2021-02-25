import { Options } from "selenium-webdriver/safari";

export interface ThematicModel {
    areaId: string,
    areaNid: number,
    areaName: string,
    value: any,
    cssClass: string,
    legends: any;
}
export interface ThematicModelDropDown {
    key: number,
    value: string,  
    label: string,
    controlType: string,
    type: string,
    Options: any;
    indiactorList: any;
}

