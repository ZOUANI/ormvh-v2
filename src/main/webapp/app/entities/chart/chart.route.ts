import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { UserRouteAccessService } from '../../core/auth/user-route-access-service';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';
import { ChartService } from './chart.service';

import { ChartComponent } from './chart.component';
import { Chart, IChart } from 'app/shared/model/chart-model';

@Injectable({ providedIn: 'root' })
export class ChartResolve implements Resolve<IChart> {
  constructor(private service: ChartService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IChart> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((response: HttpResponse<IChart>) => {
          if (response.body) {
            return of(response.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Chart());
  }
}

export const chartRoute: Routes = [
  {
    path: '',
    component: ChartComponent,
    resolve: {
      chart: ChartResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'chartApp.chart.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
