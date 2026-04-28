import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { TransferApiService } from '../../../../core/services/transfer-api.service';
import { TransfersPageComponent } from './transfers-page.component';

describe('TransfersPageComponent', () => {
  let component: TransfersPageComponent;
  let fixture: ComponentFixture<TransfersPageComponent>;
  let transferApiSpy: {
    listTransfers: ReturnType<typeof vi.fn>;
    updateTransferStatus: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    transferApiSpy = {
      listTransfers: vi.fn(),
      updateTransferStatus: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [TransfersPageComponent],
      providers: [{ provide: TransferApiService, useValue: transferApiSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(TransfersPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe cargar transferencias cuando cambian los filtros', () => {
    transferApiSpy.listTransfers.mockReturnValue(
      of([
        {
          transferId: 't-1',
          sourceAccountId: 'ACC-001',
          targetAccountId: 'ACC-002',
          amount: 100,
          currency: 'COP',
          description: null,
          clientId: '0ec72937-d6f5-4f4f-a5fc-a6b835ebfb21',
          status: 'PENDING',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          processedAt: null,
          idempotencyKey: 'idem-1'
        }
      ])
    );

    component.onFiltersChanged({
      clientId: '0ec72937-d6f5-4f4f-a5fc-a6b835ebfb21',
      status: '',
      page: 0,
      size: 10
    });

    expect(transferApiSpy.listTransfers).toHaveBeenCalled();
    expect(component.transfers().length).toBe(1);
    expect(component.loading()).toBe(false);
  });

  it('debe dejar mensaje de error si falla el listado', () => {
    transferApiSpy.listTransfers.mockReturnValue(
      throwError(() => ({ error: { message: 'Fallo en listado' } }))
    );

    component.onFiltersChanged({
      clientId: '0ec72937-d6f5-4f4f-a5fc-a6b835ebfb21',
      status: 'PENDING',
      page: 0,
      size: 10
    });

    expect(component.errorMessage()).toBe('Fallo en listado');
    expect(component.transfers()).toEqual([]);
  });

  it('debe actualizar estado y recargar listado', () => {
    transferApiSpy.updateTransferStatus.mockReturnValue(
      of({ data: {} as never, status: 200, message: 'ok', traceId: 'trace-1' })
    );
    transferApiSpy.listTransfers.mockReturnValue(of([]));

    component.onFiltersChanged({
      clientId: '0ec72937-d6f5-4f4f-a5fc-a6b835ebfb21',
      status: '',
      page: 0,
      size: 10
    });
    component.onStatusUpdated({ transferId: 't-1', status: 'APPROVED' });

    expect(transferApiSpy.updateTransferStatus).toHaveBeenCalledWith('t-1', { status: 'APPROVED' });
    expect(component.actionMessage()).toBe('Estado actualizado correctamente.');
  });
});
