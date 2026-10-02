import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Pokemon, PokemonSearchResult } from '../models/pokemon';

@Injectable({
  providedIn: 'root',
})
export class PokemonService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8088/api/pokemon';

  search(search: string): Observable<PokemonSearchResult[]> {
    const params = new HttpParams().set('search', search);

    return this.http.get<PokemonSearchResult[]>(this.apiUrl, { params });
  }

  getByExternalId(externalId: number): Observable<Pokemon> {
    return this.http.get<Pokemon>(`${this.apiUrl}/external/${externalId}`);
  }
}
