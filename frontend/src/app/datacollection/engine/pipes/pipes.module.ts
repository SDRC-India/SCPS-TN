import { NgModule } from '@angular/core';
import { FormSearchPipe } from './form-search/form-search';
import { HomeFormSearchPipe } from './home-form-search/home-form-search';
import { SortRecordPipe } from './sort-record/sort-record';
import { RemoveExtraKeysPipe } from './remove-extra-keys';
@NgModule({
	declarations: [FormSearchPipe,SortRecordPipe,RemoveExtraKeysPipe,
    HomeFormSearchPipe],
	imports: [],
	exports: [FormSearchPipe, 
    HomeFormSearchPipe]
})
export class PipesModule {}
