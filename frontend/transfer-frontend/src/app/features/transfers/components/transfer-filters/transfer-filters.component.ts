import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ListTransfersFilters, TransferStatus } from '../../../../core/models/transfer.model';

@Component({
  selector: 'app-transfer-filters',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transfer-filters.component.html',
  styleUrl: './transfer-filters.component.css'
})
export class TransferFiltersComponent {
  @Output() filtersChanged = new EventEmitter<ListTransfersFilters>();

  private readonly fb = inject(FormBuilder);
  readonly statuses: Array<TransferStatus | ''> = ['', 'PENDING', 'PROCESSING', 'APPROVED', 'REJECTED'];

  readonly form = this.fb.nonNullable.group({
    clientId: ['', [Validators.required]],
    status: ['' as TransferStatus | ''],
    size: [10, [Validators.required, Validators.min(1), Validators.max(100)]]
  });

  submitFilters(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.filtersChanged.emit({
      clientId: this.form.controls.clientId.value.trim(),
      status: this.form.controls.status.value,
      page: 0,
      size: Number(this.form.controls.size.value)
    });
  }
}
