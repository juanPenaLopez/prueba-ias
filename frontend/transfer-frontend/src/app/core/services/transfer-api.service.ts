import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  CreateTransferPayload,
  ListTransfersFilters,
  Transfer,
  UpdateTransferStatusPayload
} from '../models/transfer.model';

@Injectable({
  providedIn: 'root'
})
export class TransferApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/transfers`;

  createTransfer(payload: CreateTransferPayload): Observable<ApiResponse<Transfer>> {
    return this.http.post<ApiResponse<Transfer>>(this.baseUrl, payload, {
      headers: {
        'Idempotency-Key': crypto.randomUUID()
      }
    });
  }

  listTransfers(filters: ListTransfersFilters): Observable<Transfer[]> {
    let params = new HttpParams()
      .set('clientId', filters.clientId.trim())
      .set('page', filters.page)
      .set('size', filters.size);

    if (filters.status) {
      params = params.set('status', filters.status.trim().toUpperCase());
    }

    return this.http.get<Transfer[]>(this.baseUrl, { params });
  }

  updateTransferStatus(
    transferId: string,
    payload: UpdateTransferStatusPayload
  ): Observable<ApiResponse<Transfer>> {
    return this.http.patch<ApiResponse<Transfer>>(`${this.baseUrl}/${transferId}/status`, payload);
  }
}
