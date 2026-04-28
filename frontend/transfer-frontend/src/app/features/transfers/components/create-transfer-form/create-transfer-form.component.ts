import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { CreateTransferPayload, Transfer } from '../../../../core/models/transfer.model';
import { TransferApiService } from '../../../../core/services/transfer-api.service';

@Component({
  selector: 'app-create-transfer-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-transfer-form.component.html',
  styleUrl: './create-transfer-form.component.css'
})
export class CreateTransferFormComponent implements OnChanges {
  @Input() clientId = '';
  @Output() transferCreated = new EventEmitter<Transfer>();

  private readonly fb = inject(FormBuilder);
  private readonly transferApi = inject(TransferApiService);

  loading = false;
  errorMessage = '';

  readonly form = this.fb.nonNullable.group({
    clientId: ['', [Validators.required]],
    sourceAccountId: ['', [Validators.required, Validators.minLength(3)]],
    targetAccountId: ['', [Validators.required, Validators.minLength(3)]],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['COP', [Validators.required, Validators.minLength(3), Validators.maxLength(3)]],
    description: ['']
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['clientId']?.currentValue) {
      this.form.controls.clientId.setValue(this.clientId);
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.errorMessage = 'Completa los campos requeridos para crear la transferencia.';
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const payload: CreateTransferPayload = {
      clientId: this.form.controls.clientId.value.trim(),
      sourceAccountId: this.form.controls.sourceAccountId.value,
      targetAccountId: this.form.controls.targetAccountId.value,
      amount: Number(this.form.controls.amount.value),
      currency: this.form.controls.currency.value.toUpperCase(),
      description: this.form.controls.description.value.trim() || undefined
    };

    this.transferApi
      .createTransfer(payload)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (response) => {
          this.transferCreated.emit(response.data);
          this.form.patchValue({ amount: 0, description: '' });
        },
        error: (error) => {
          this.errorMessage = error?.error?.message ?? 'No fue posible crear la transferencia.';
        }
      });
  }
}
