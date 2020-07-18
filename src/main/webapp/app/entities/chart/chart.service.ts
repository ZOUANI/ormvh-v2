import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from '../../app.constants';
import { IChart } from 'app/shared/model/chart-model';

type EntityResponseType = HttpResponse<IChart>;

@Injectable({ providedIn: 'root' })
export class ChartService {
  public resourceUrl = SERVER_API_URL + 'api/chart';

  constructor(protected http: HttpClient) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IChart>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
