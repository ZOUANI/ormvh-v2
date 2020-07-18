import { Component, NgModule, OnInit, OnDestroy } from '@angular/core';
import { CourrierService } from '../courrier/courrier.service';

@Component({
  selector: 'jhi-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['chart.component.scss']
})
export class ChartComponent implements OnInit, OnDestroy {
  pieChart2Data: any[] = [];

  pieChart1Data: any[] = [];
  pieChartView = [700, 300];
  pieChartColorScheme = {
    domain: ['#a81934', '#4a81e4', '#5AA454']
  };

  single: any[] = [];
  view = [1200, 150];
  colorScheme = {
    domain: ['#9392cf', '#9392cf', '#9392cf', '#a81934', '#4a81e4', '#5AA454']
  };

  cardColor = '#232837';

  constructor(protected courrierService: CourrierService) {}

  ngOnInit(): void {
    this.courrierService.stats().subscribe(value => {
      if (value.body) {
        this.pieChart1Data = [
          {
            name: 'Ouverts',
            value: value.body.ccOpenCount
          },
          {
            name: 'En cours',
            value: value.body.ccWipCount
          },
          {
            name: 'Traités',
            value: value.body.ccTraiteCount
          }
        ];

        this.pieChart2Data = [
          {
            name: 'Arrivée',
            value: value.body.ccArriveeCount
          },
          {
            name: 'Départ',
            value: value.body.ccDepartCount
          }
        ];

        this.single = [
          {
            name: 'Tous les courriers',
            value: value.body.count
          },
          {
            name: 'Arrivée',
            value: value.body.ccArriveeCount
          },
          {
            name: 'Départ',
            value: value.body.ccDepartCount
          },
          {
            name: 'Ouverts',
            value: value.body.ccOpenCount
          },
          {
            name: 'En cours',
            value: value.body.ccWipCount
          },
          {
            name: 'Traités',
            value: value.body.ccTraiteCount
          }
        ];
      }
    });
  }

  ngOnDestroy(): void {}

  onSelect(event: any): void {}

  onActivate(data: any): void {}

  onDeactivate(data: any): void {}
}
