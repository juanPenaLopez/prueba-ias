import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { vi } from 'vitest';
import { environment } from '../../../environments/environment';
import { TransferApiService } from './transfer-api.service';

describe('TransferApiService', () => {
  let service: TransferApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TransferApiService, provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(TransferApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe crear transferencia con Idempotency-Key', () => {
    vi.spyOn(crypto, 'randomUUID').mockReturnValue('123e4567-e89b-12d3-a456-426614174000');

    const payload = {
      clientId: '0ec72937-d6f5-4f4f-a5fc-a6b835ebfb21',
      sourceAccountId: 'ACC-001',
      targetAccountId: 'ACC-002',
      amount: 100,
      currency: 'COP'
    };

    service.createTransfer(payload).subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/transfers`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Idempotency-Key')).toBe('123e4567-e89b-12d3-a456-426614174000');
    expect(req.request.body).toEqual(payload);
    req.flush({ data: {}, status: 200, message: 'ok', traceId: 'trace-1' });
  });

  it('debe listar transferencias con filtros y paginacion', () => {
    service
      .listTransfers({
        clientId: '0ec72937-d6f5-4f4f-a5fc-a6b835ebfb21',
        status: 'PENDING',
        page: 2,
        size: 25
      })
      .subscribe();

    const req = httpMock.expectOne((request) => request.url === `${environment.apiBaseUrl}/transfers`);

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('clientId')).toBe('0ec72937-d6f5-4f4f-a5fc-a6b835ebfb21');
    expect(req.request.params.get('status')).toBe('PENDING');
    expect(req.request.params.get('page')).toBe('2');
    expect(req.request.params.get('size')).toBe('25');
    req.flush([]);
  });

  it('debe actualizar estado de transferencia', () => {
    service.updateTransferStatus('t-1', { status: 'APPROVED' }).subscribe();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/transfers/t-1/status`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ status: 'APPROVED' });
    req.flush({ data: {}, status: 200, message: 'ok', traceId: 'trace-1' });
  });
});
