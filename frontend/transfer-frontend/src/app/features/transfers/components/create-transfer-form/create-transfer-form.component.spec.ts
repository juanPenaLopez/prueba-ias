import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { TransferApiService } from '../../../../core/services/transfer-api.service';
import { CreateTransferFormComponent } from './create-transfer-form.component';

describe('CreateTransferFormComponent', () => {
  let component: CreateTransferFormComponent;
  let fixture: ComponentFixture<CreateTransferFormComponent>;
  let transferApiSpy: { createTransfer: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    transferApiSpy = {
      createTransfer: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [CreateTransferFormComponent],
      providers: [{ provide: TransferApiService, useValue: transferApiSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateTransferFormComponent);
    component = fixture.componentInstance;
    component.clientId = '0ec72937-d6f5-4f4f-a5fc-a6b835ebfb21';
    fixture.detectChanges();
  });

  it('debe llamar al servicio y emitir transferCreated cuando el formulario es valido', () => {
    const emitSpy = vi.spyOn(component.transferCreated, 'emit');
    transferApiSpy.createTransfer.mockReturnValue(
      of({
        data: {
          transferId: 't-1',
          sourceAccountId: 'ACC-001',
          targetAccountId: 'ACC-002',
          amount: 100,
          currency: 'COP',
          description: null,
          clientId: component.clientId,
          status: 'PENDING',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          processedAt: null,
          idempotencyKey: 'idem-1'
        },
        status: 200,
        message: 'ok',
        traceId: 'trace-1'
      })
    );

    component.form.setValue({
      clientId: component.clientId,
      sourceAccountId: 'ACC-001',
      targetAccountId: 'ACC-002',
      amount: 100,
      currency: 'cop',
      description: 'pago'
    });

    component.onSubmit();

    expect(transferApiSpy.createTransfer).toHaveBeenCalled();
    expect(emitSpy).toHaveBeenCalled();
    expect(component.errorMessage).toBe('');
    expect(component.loading).toBe(false);
  });

  it('no debe llamar al servicio si el formulario es invalido', () => {
    component.form.setValue({
      clientId: '',
      sourceAccountId: '',
      targetAccountId: '',
      amount: 0,
      currency: '',
      description: ''
    });

    component.onSubmit();

    expect(transferApiSpy.createTransfer).not.toHaveBeenCalled();
  });

  it('debe mostrar error cuando el servicio falla', () => {
    transferApiSpy.createTransfer.mockReturnValue(
      throwError(() => ({ error: { message: 'Error de negocio' } }))
    );

    component.form.setValue({
      clientId: component.clientId,
      sourceAccountId: 'ACC-001',
      targetAccountId: 'ACC-002',
      amount: 100,
      currency: 'COP',
      description: 'pago'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe('Error de negocio');
    expect(component.loading).toBe(false);
  });
});
