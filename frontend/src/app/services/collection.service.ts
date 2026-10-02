import { Injectable, inject } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import {
  AddCollectionItemRequest,
  CollectionItem,
  UpdateCollectionItemRequest,
} from '../models/collection';

@Injectable({
  providedIn: 'root',
})
export class CollectionService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8088/api/me/collection';

  getCollection(): Observable<CollectionItem[]> {
    return this.http.get<CollectionItem[]>(this.apiUrl);
  }

  add(request: AddCollectionItemRequest): Observable<CollectionItem> {
    return this.http.post<CollectionItem>(this.apiUrl, request);
  }

  update(id: number, request: UpdateCollectionItemRequest): Observable<CollectionItem> {
    return this.http.patch<CollectionItem>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
