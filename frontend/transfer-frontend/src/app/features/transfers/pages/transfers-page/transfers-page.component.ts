import { CommonModule } from '@angular/common';
import { Component, signal, inject } from '@angular/core';
import { finalize } from 'rxjs';
import { ListTransfersFilters, Transfer, TransferStatus } from '../../../../core/models/transfer.model';
import { TransferApiService } from '../../../../core/services/transfer-api.service';
import { CreateTransferFormComponent } from '../../components/create-transfer-form/create-transfer-form.component';
import { TransferFiltersComponent } from '../../components/transfer-filters/transfer-filters.component';
import { TransferListComponent } from '../../components/transfer-list/transfer-list.component';

@Component({
  selector: 'app-transfers-page',
  standalone: true,
  imports: [CommonModule, CreateTransferFormComponent, TransferFiltersComponent, TransferListComponent],
  templateUrl: './transfers-page.component.html',
  styleUrl: './transfers-page.component.css'
})
export class TransfersPageComponent {
  private readonly transferApi = inject(TransferApiService);

  readonly filters = signal<ListTransfersFilters>({
    clientId: '',
    status: '',
    page: 0,
    size: 10
  });
  readonly transfers = signal<Transfer[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly actionMessage = signal('');

  onFiltersChanged(filters: ListTransfersFilters): void {
    this.filters.set(filters);
    this.loadTransfers();
  }

  onPageChanged(page: number): void {
    this.filters.update((current) => ({ ...current, page }));
    this.loadTransfers();
  }

  onTransferCreated(): void {
    this.actionMessage.set('Transferencia creada correctamente.');
    this.loadTransfers();
  }

  onStatusUpdated(event: { transferId: string; status: TransferStatus }): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.transferApi
      .updateTransferStatus(event.transferId, { status: event.status })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.actionMessage.set('Estado actualizado correctamente.');
          this.loadTransfers();
        },
        error: (error) => {
          this.errorMessage.set(
            error?.error?.message ?? 'No fue posible actualizar el estado de la transferencia.'
          );
        }
      });
  }

  loadTransfers(): void {
    const currentFilters = this.filters();
    if (!currentFilters.clientId) {
      this.transfers.set([]);
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    this.transferApi
      .listTransfers(currentFilters)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (transfers) => {
          this.transfers.set(transfers);
        },
        error: (error) => {
          this.errorMessage.set(error?.error?.message ?? 'No fue posible cargar las transferencias.');
          this.transfers.set([]);
        }
      });
  }
}
