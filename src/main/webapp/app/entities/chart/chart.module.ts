import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxChartsModule } from '@swimlane/ngx-charts';

import { ChartComponent } from './chart.component';
import { OrmvahSharedModule } from '../../shared/shared.module';
import { courrierRoute } from '../courrier/courrier.route';
import { chartRoute } from 'app/entities/chart/chart.route';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [OrmvahSharedModule, NgxChartsModule, RouterModule.forChild(chartRoute)],
  declarations: [ChartComponent],
  exports: [ChartComponent],
  bootstrap: [ChartComponent]
})
export class OrmvahChartModule {}
